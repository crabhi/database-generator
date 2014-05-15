/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator.random;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import cz.flih.database.generator.artifacts.Column;
import cz.flih.database.generator.ref.ColumnName;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Krab
 */
public class RowGenerator {

    private final Map<ColumnName, ValueGenerator<?>> generators;

    public RowGenerator(Set<Column> columns) {
        ImmutableMap.Builder<ColumnName, ValueGenerator<?>> builder = ImmutableMap.builder();
        for (Column column : columns) {
            builder.put(column.getName(), ValueGeneratorRegistry.getInstance().createGenerator(column));
        }
        generators = builder.build();
    }

    public Map<ColumnName, Object> generateRow() {
        Map<ColumnName, Object> lazyView = Maps.transformEntries(generators, (col, generator) -> {
            return generator.nextValue();
        });
        return ImmutableMap.copyOf(lazyView);
    }

}
