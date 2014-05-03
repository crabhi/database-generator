/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator;

import java.util.Objects;

/**
 *
 * @author Krab
 */
public final class ColumnName {

    private final String column;
    private final TableName table;

    ColumnName(TableName table, String column) {
        this.table = table;
        this.column = column;
    }

    @Override
    public String toString() {
        return column;
    }

    public String getColumn() {
        return column;
    }

    public TableName getTable() {
        return table;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.column);
        hash = 59 * hash + Objects.hashCode(this.table);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ColumnName other = (ColumnName) obj;
        if (!Objects.equals(this.column, other.column)) {
            return false;
        }
        return Objects.equals(this.table, other.table);
    }

}
