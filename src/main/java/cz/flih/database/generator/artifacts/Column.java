/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator.artifacts;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import cz.flih.database.generator.ref.ColumnName;
import java.sql.DatabaseMetaData;

/**
 *
 * @author Krab
 */
public class Column {

    private static final ImmutableList<Integer> NULLABLE_OPTIONS = ImmutableList.of(
            DatabaseMetaData.columnNoNulls, DatabaseMetaData.columnNullable, DatabaseMetaData.columnNullableUnknown);

    private final ColumnName name;
    private final int jdbcType;
    private final int size;
    private final int scale;
    private final int nullable;

    public Column(ColumnName name, int jdbcType, int size, int scale, int nullable) {
        this.name = name;
        this.jdbcType = jdbcType;
        this.size = size;
        this.scale = scale;

        Preconditions.checkArgument(NULLABLE_OPTIONS
                .contains(nullable));
        this.nullable = nullable;
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

    /**
     * @return One of {@link DatabaseMetaData#columnNoNulls}, {@link DatabaseMetaData#columnNullable},
     * {@link DatabaseMetaData#columnNullableUnknown}.
     *
     */
    public int getNullable() {
        return nullable;
    }

    @Override
    public String toString() {
        return getName().toString();
    }
}
