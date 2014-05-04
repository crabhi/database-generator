/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator;

/**
 *
 * @author Krab
 */
class ForeignKey {

    private final ColumnName fkColumn;
    private final ColumnName pkColumn;

    ForeignKey(ColumnName fkColumn, ColumnName pkColumn) {
        this.fkColumn = fkColumn;
        this.pkColumn = pkColumn;
    }

    public TableName getPkTable() {
        return pkColumn.getTable();
    }

    public TableName getFkTable() {
        return fkColumn.getTable();
    }
}
