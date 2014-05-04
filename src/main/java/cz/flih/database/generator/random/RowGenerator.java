/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator.random;

import com.google.common.collect.ImmutableSet;
import cz.flih.database.generator.artifacts.Column;
import cz.flih.database.generator.ref.ColumnName;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Krab
 */
public class RowGenerator {

    private final Set<Column> columns;

    public RowGenerator(Set<Column> columns) {
        this.columns = ImmutableSet.copyOf(columns);
    }

    public Map<ColumnName, Object> generateRow() {
        throw new UnsupportedOperationException("Not supported");
    }

}
