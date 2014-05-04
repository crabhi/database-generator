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

    public Column(ColumnName name) {
        this.name = name;
    }

    public ColumnName getName() {
        return name;
    }

}
