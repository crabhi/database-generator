/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator;

import com.google.common.annotations.VisibleForTesting;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Krab
 */
public class Generator {

    private final Connection conn;
    private final MetaData md;

    Generator(Connection conn, MetaData md) {
        this.conn = conn;
        this.md = md;
    }

    void addData() {
        Stream<TableName> start = getStartingTables();
    }

    /**
     * @return The tables to which lead no FK constraints and thus can be filled without any external limitations.
     */
    @VisibleForTesting
    Stream<TableName> getStartingTables() {
        final Map<TableName, Set<ForeignKey>> fks = md.getAllFKs();

        return md.getTables().parallelStream().filter((table) -> {
            return !fks.containsKey(table) || fks.get(table).isEmpty();
        });
    }
}
