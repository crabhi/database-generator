/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator;

import com.google.common.collect.ImmutableSet;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 *
 * @author Krab
 *
 * // NOT THREAD SAFE
 */
class MetaData {

    private final DatabaseMetaData metaData;
    private Collection<TableName> tables;

    MetaData(DatabaseMetaData metaData) {
        this.metaData = metaData;
    }

    private Collection<TableName> loadTables() {
        ImmutableSet.Builder<TableName> builder = ImmutableSet.builder();
        try {
            try (ResultSet rs = metaData.getTables(null, null, null, new String[]{"TABLE"})) {
                while (rs.next()) {
                    builder.add(new TableName(rs.getString("TABLE_NAME")));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return builder.build();
    }

    public Collection<TableName> getTables() {
        if (tables == null) {
            tables = loadTables();
        }
        // ImmSet.copyOf to get rid of IDE warning
        return ImmutableSet.copyOf(tables);
    }


}
