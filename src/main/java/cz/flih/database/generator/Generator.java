/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.flih.database.generator;

import java.sql.Connection;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
