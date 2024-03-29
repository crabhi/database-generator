/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator.ref;

import java.util.Objects;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

/**
 *
 * @author Krab
 */
public final class TableName {

    private final String table;

    public TableName(String table) {
        this.table = table;
    }

    public static TableName parse(String name) {
        if (name.contains(".")) {
            throw new UnsupportedOperationException("Multiple schemas not yet supported.");
        }
        return new TableName(name);
    }

    public Table<Record> toJooq() {
        return DSL.tableByName(table);
    }

    @Override
    public String toString() {
        return table;
    }

    public String getTable() {
        return table;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.table);
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
        final TableName other = (TableName) obj;
        return Objects.equals(this.table, other.table);
    }
}
