/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import cz.flih.database.generator.artifacts.ForeignKey;
import cz.flih.database.generator.random.RowGenerator;
import cz.flih.database.generator.ref.ColumnName;
import cz.flih.database.generator.ref.TableName;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Krab
 */
public class Generator {

    private static final Logger LOG = Logger.getLogger(Generator.class.getName());
    private final Connection conn;
    private final MetaData md;
    private final DbStatistics stats;

    Generator(Connection conn, MetaData md, DbStatistics stats) {
        this.conn = conn;
        this.md = md;
        this.stats = stats;
    }

    public void start() {
        Stream<TableName> start = getStartingTables();

        start.forEach((table) -> {
            Optional<Integer> tableSize = stats.getTableSize(table);
            if (!tableSize.isPresent()) {
                LOG.severe(MessageFormat.format("Skipping {0}, its size is unknown.", table));
            } else {
                addData(table, tableSize.get(), ImmutableMap.of());
            }
        });
    }

    private void addData(final TableName table, final int rows, final Map<ColumnName, Object> fixedValues) {
        RowGenerator rowgen = new RowGenerator(md.getColumns(table).stream().filter((col) -> {
                return !fixedValues.containsKey(col.getName());
            }).collect(Collectors.toSet()));

        for (int i = 0; i < rows; i++) {
            Map<ColumnName, Object> randomRow = rowgen.generateRow();
            ImmutableMap<ColumnName, Object> row = ImmutableMap.<ColumnName, Object>builder()
                    .putAll(randomRow)
                    .putAll(fixedValues).build();

            Map<ColumnName, Object> generated = insert(table, row);

            ImmutableMap<ColumnName, Object> insertedRow = ImmutableMap.<ColumnName, Object>builder()
                    .putAll(row)
                    .putAll(generated).build();

            for (ForeignKey foreignKey : md.getOutgoingRelations(table)) {
                assert foreignKey.getPkTable().equals(table);

                int childRows = stats.getRandomChildrenCount(foreignKey);

                Object parentValue = insertedRow.get(foreignKey.getPkColumn());
                Map<ColumnName, Object> childFixedValues = ImmutableMap.of(foreignKey.getFkColumn(), parentValue);

                addData(foreignKey.getFkTable(), childRows, childFixedValues);
            }
        }
    }

    /**
     * @return The tables to which lead no FK constraints and thus can be filled without any external limitations.
     */
    @VisibleForTesting
    Stream<TableName> getStartingTables() {
        final Map<TableName, Set<ForeignKey>> fks = md.getAllFKs();

        // TODO: assert no cycles

        return md.getTables().stream().filter((table) -> {
            return !fks.containsKey(table) || fks.get(table).isEmpty();
        });
    }

    private Map<ColumnName, Object> insert(TableName table, ImmutableMap<ColumnName, Object> row) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
