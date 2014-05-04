/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.flih.database.generator.random;

import com.google.common.collect.ImmutableMap;
import cz.flih.database.generator.artifacts.Column;
import cz.flih.database.generator.values.ColumnName;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Krab
 */
public class RowGenerator {

    public Map<ColumnName, Object> generateRow(Stream<Column> columns) {
        return ImmutableMap.of();
    }

}
