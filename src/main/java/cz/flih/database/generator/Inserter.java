/*
 * Copyright (c) 1998-2014 ChemAxon Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * ChemAxon. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the agreements
 * you entered into with ChemAxon.
 */
package cz.flih.database.generator;

import com.google.common.collect.ImmutableMap;
import cz.flih.database.generator.artifacts.Column;
import cz.flih.database.generator.ref.ColumnName;
import cz.flih.database.generator.ref.TableName;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.jooq.Field;
import org.jooq.InsertReturningStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

/**
 *
 * @author krab
 */
public class Inserter implements AutoCloseable {

    private final PreparedStatement stmt;
    private final Map<ColumnName, Integer> columnIndices;
    private final TableName table;

    public Inserter(Connection conn, TableName table, Set<Column> cols) throws SQLException {
        ImmutableMap.Builder<ColumnName, Integer> indicesBuilder = ImmutableMap.<ColumnName, Integer>builder();
        String query = buildQuery(conn, table, cols, indicesBuilder);

        this.table = table;
        stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        columnIndices = indicesBuilder.build();
    }

    private String buildQuery(Connection conn,
            TableName table,
            Set<Column> cols,
            ImmutableMap.Builder<ColumnName, Integer> indicesBuilder) {

        int indexInPS = 1; // SQL indices start with 1
        InsertSetStep<Record> initialQuery = DSL.using(conn).insertInto(table.toJooq());
        InsertReturningStep<Record> query2 = initialQuery.defaultValues();
        Iterator<Column> it = cols.iterator();
        while (it.hasNext()) {
            Column col = it.next();
            indicesBuilder.put(col.getName(), indexInPS);
            query2 = setToQuery(initialQuery, col.getName().toJooq(), null);
        }
        return query2.getSQL(ParamType.INDEXED);
    }

    // To help compiler resolve the ambiguity introduced by null value.
    private <T> InsertReturningStep<Record> setToQuery(InsertSetStep<Record> query, Field<T> field, T val) {
        return query.set(field, val);
    }

    public Map<ColumnName, Object> insert(Map<ColumnName, Object> row) throws SQLException {
        stmt.clearParameters();
        for (Map.Entry<ColumnName, Object> entry : row.entrySet()) {
            Integer index = columnIndices.get(entry.getKey());
            if (index == null) {
                throw new IllegalArgumentException(entry.getKey() + " not present in the initial set of columns.");
            }
            stmt.setObject(index, entry.getValue());
        }

        stmt.executeUpdate();
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            return extractGeneratedKeys(rs);
        }
    }

    @Override
    public void close() throws Exception {
        stmt.close();
    }

    private Map<ColumnName, Object> extractGeneratedKeys(ResultSet rs) throws SQLException {
        ImmutableMap.Builder<ColumnName, Object> builder = ImmutableMap.builder();
        rs.next();
        ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            assert new TableName(md.getTableName(i)).equals(table) : MessageFormat
                    .format("Generated keys should be part of "
                            + "the table where the insert was performed ({0}), was {1}.",
                            table, md.getTableName(i));
            ColumnName col = new ColumnName(table, md.getColumnName(i));
            builder.put(col, rs.getObject(i));
        }
        assert !rs.next() : "One row expected";
        return builder.build();
    }
}
