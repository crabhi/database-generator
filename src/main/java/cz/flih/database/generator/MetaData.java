package cz.flih.database.generator;

import com.google.common.base.Preconditions;
import cz.flih.database.generator.ref.ColumnName;
import cz.flih.database.generator.ref.TableName;
import cz.flih.database.generator.artifacts.ForeignKey;
import cz.flih.database.generator.artifacts.Column;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.text.MessageFormat.format;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

class MetaData {

    private final DatabaseMetaData metaData;
    private Collection<TableName> tables;
    private Map<TableName, Set<ForeignKey>> fks;

    MetaData(DatabaseMetaData metaData) {
        this.metaData = metaData;
    }

    private Collection<TableName> loadTables() {
        return findInfo(
                (md) -> {
                    return md.getTables(null, null, null, new String[]{"TABLE"});
                },
                (rs) -> {
                    return new TableName(rs.getString("TABLE_NAME"));
                });
    }

    public Collection<TableName> getTables() {
        if (tables == null) {
            tables = loadTables();
        }
        // ImmSet.copyOf to get rid of IDE warning
        return ImmutableSet.copyOf(tables);
    }

    public Map<TableName, Set<ForeignKey>> getAllFKs() {
        if (fks == null) {
            fks = loadAllFKs();
        }
        return ImmutableMap.copyOf(fks);
    }

    private Map<TableName, Set<ForeignKey>> loadAllFKs() {
        ImmutableMap.Builder<TableName, Set<ForeignKey>> builder = ImmutableMap.builder();
        for (TableName tableName : getTables()) {
            builder.put(tableName, getFKs(tableName));
        }
        return builder.build();
    }

    private <T> Set<T> findInfo(MetaDataGetter getter, MDItemBuilder<T> rowParser) {
        ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        try {
            try (ResultSet rs = getter.get(metaData)) {
                while (rs.next()) {
                    builder.add(rowParser.build(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return builder.build();
    }

    private Set<ForeignKey> getFKs(final TableName table) {
        return findInfo((md) -> {
            return md.getImportedKeys(null, null, table.getTable());
        }, fkBuilder);
    }

    /**
     * Builds FK from a {@link ResultSet}.
     */
    private static final MDItemBuilder<ForeignKey> fkBuilder = (rs) -> {
        TableName fkTable = new TableName(rs.getString("FKTABLE_NAME"));
        TableName pkTable = new TableName(rs.getString("PKTABLE_NAME"));

        if (rs.getInt("KEY_SEQ") > 1) {
            throw new RuntimeException(
                    format("Unsupported multiple column keys (in tables {0} -> {1})", fkTable, pkTable));
        }

        ColumnName fkColumn = new ColumnName(fkTable, rs.getString("FKCOLUMN_NAME"));
        ColumnName pkColumn = new ColumnName(pkTable, rs.getString("PKCOLUMN_NAME"));

        return new ForeignKey(fkColumn, pkColumn);
    };

    /**
     * @param table table
     * @return a set of keys dependent on this table
     */
    public Set<ForeignKey> getOutgoingRelations(final TableName table) {
        return findInfo((md) -> {
            return md.getExportedKeys(null, null, table.getTable());
        }, fkBuilder);
    }

    Set<Column> getColumns(final TableName table) {
        Set<String> uniqueIndices = findInfo((md) -> {
            return md.getIndexInfo(null, null, table.getTable(), true, true);
        }, (rs) -> {
            if (rs.getInt("ORDINAL_POSITION") > 1) {
                throw new RuntimeException(
                        format("Unsupported multiple column keys (in table {0})", table));
            }

            assert !rs.getBoolean("NON_UNIQUE") : "Was supposed to get only unique indexes";

            return rs.getString("COLUMN_NAME");
        });

        Set<String> pkCols = findInfo((md) -> {
            return md.getPrimaryKeys(null, null, table.getTable());
        }, (rs) -> {
            if (rs.getInt("KEY_SEQ") > 1) {
                throw new RuntimeException(
                        format("Unsupported multiple primary keys (in table {0})", table));
            }
            return Preconditions.checkNotNull(rs.getString("COLUMN_NAME"));
        });

        final Set<String> uniqueCols = Sets.union(uniqueIndices, pkCols);

        return findInfo((md) -> {
            return metaData.getColumns(null, null, table.getTable(), null);
        }, (rs) -> {
            TableName tableName = new TableName(rs.getString("TABLE_NAME"));
            assert table.equals(tableName);
            String colNameStr = rs.getString("COLUMN_NAME");
            ColumnName colName = new ColumnName(tableName, colNameStr);
            int jdbcType = rs.getInt("DATA_TYPE");
            int size = rs.getInt("COLUMN_SIZE");
            int scale = rs.getInt("DECIMAL_DIGITS");
            int nullable = rs.getInt("NULLABLE");
            return new Column(colName, jdbcType, size, scale, nullable, uniqueCols.contains(colNameStr));
        });
    }

    @FunctionalInterface
    private interface MetaDataGetter {

        ResultSet get(DatabaseMetaData metaData) throws SQLException;
    }

    @FunctionalInterface
    private interface MDItemBuilder<T> {

        T build(ResultSet rs) throws SQLException;
    }

}
