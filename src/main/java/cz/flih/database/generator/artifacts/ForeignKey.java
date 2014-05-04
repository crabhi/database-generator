/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator.artifacts;

import cz.flih.database.generator.values.ColumnName;
import cz.flih.database.generator.values.TableName;
import java.util.Objects;

/**
 *
 * @author Krab
 */
public class ForeignKey {

    private final ColumnName fkColumn;
    private final ColumnName pkColumn;

    public ForeignKey(ColumnName fkColumn, ColumnName pkColumn) {
        this.fkColumn = fkColumn;
        this.pkColumn = pkColumn;
    }

    public TableName getPkTable() {
        return pkColumn.getTable();
    }

    public TableName getFkTable() {
        return fkColumn.getTable();
    }

    public ColumnName getFkColumn() {
        return fkColumn;
    }

    public ColumnName getPkColumn() {
        return pkColumn;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.fkColumn);
        hash = 97 * hash + Objects.hashCode(this.pkColumn);
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
        final ForeignKey other = (ForeignKey) obj;
        if (!Objects.equals(this.fkColumn, other.fkColumn)) {
            return false;
        }
        return Objects.equals(this.pkColumn, other.pkColumn);
    }
}
