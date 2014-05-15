/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator.artifacts;

import cz.flih.database.generator.ref.ColumnName;

/**
 *
 * @author Krab
 */
public class Column {

    private final ColumnName name;
    private final int jdbcType;
    private final int size;
    private final int scale;

    public Column(ColumnName name, int jdbcType, int size, int scale) {
        this.name = name;
        this.jdbcType = jdbcType;
        this.size = size;
        this.scale = scale;
    }

    public ColumnName getName() {
        return name;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public int getSize() {
        return size;
    }

    public int getScale() {
        return scale;
    }
}
