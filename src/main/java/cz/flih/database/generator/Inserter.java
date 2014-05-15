package cz.flih.database.generator;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import cz.flih.database.generator.artifacts.Column;
import cz.flih.database.generator.random.SqlNull;
import cz.flih.database.generator.ref.ColumnName;
import cz.flih.database.generator.ref.TableName;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jooq.DSLContext;
import org.jooq.RenderContext;
import org.jooq.impl.DSL;

/**
 *
 * @author krab
 */
public class Inserter implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(Inserter.class.getName());

    private final PreparedStatement stmt;
    private final Map<ColumnName, Integer> columnIndices;
    private final TableName table;

    public Inserter(Connection conn, TableName table, Set<Column> cols) throws SQLException {
        ImmutableMap.Builder<ColumnName, Integer> indicesBuilder = ImmutableMap.<ColumnName, Integer>builder();
        String query = buildQuery(conn, table, cols, indicesBuilder);

        this.table = table;

        LOG.log(Level.INFO, "Inserter statement: {0}", query);
        stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        columnIndices = indicesBuilder.build();
    }

    private String buildQuery(Connection conn,
            TableName table,
            Set<Column> cols,
            ImmutableMap.Builder<ColumnName, Integer> indicesBuilder) {
        DSLContext context = DSL.using(conn);
        if (cols.isEmpty()) {
            return context.insertInto(table.toJooq()).defaultValues().getSQL();
        } else {
            RenderContext rc = context.renderContext();
            rc.sql("INSERT INTO ");
            rc.visit(table.toJooq());
            rc.sql("(");
            int i = 1;
            for (Column col : cols) {
                indicesBuilder.put(col.getName(), i);
                rc.visit(col.getName().toJooq());
                if (i != cols.size()) {
                    rc.sql(", ");
                }
                i++;
            }
            rc.sql(") VALUES (");
            rc.sql(Joiner.on(", ").join(Iterables.limit(Iterables.cycle("?"), cols.size())));
            rc.sql(")");
            return rc.render();
        }
    }

    public Map<ColumnName, Object> insert(Map<ColumnName, Object> row) throws SQLException {
        stmt.clearParameters();
        for (Map.Entry<ColumnName, Object> entry : row.entrySet()) {
            Integer index = columnIndices.get(entry.getKey());
            if (index == null) {
                throw new IllegalArgumentException(entry.getKey() + " not present in the initial set of columns.");
            }
            if (entry.getValue() == SqlNull.INSTANCE) {
                stmt.setObject(index, null);
            } else {
                stmt.setObject(index, entry.getValue());
            }
            LOG.log(Level.FINER, "Setting {0} at {1} to {2}", new Object[]{entry.getKey(), index, entry.getValue()});
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
            assert "".equals(md.getTableName(i)) ||new TableName(md.getTableName(i)).equals(table) : MessageFormat
                    .format("Generated keys should be part of "
                            + "the table where the insert was performed ({0}), was {1}.",
                            table, md.getTableName(i));
            ColumnName col = new ColumnName(table, md.getColumnName(i));
            Object val = rs.getObject(i);
            if (val != null) {
                builder.put(col, val);
            }
        }
        assert !rs.next() : "One row expected";
        return builder.build();
    }
}
