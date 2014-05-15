/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator;

import com.google.common.collect.ImmutableSet;
import cz.flih.database.generator.artifacts.Column;
import cz.flih.database.generator.ref.ColumnName;
import cz.flih.database.generator.ref.TableName;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Krab
 */
public class InserterTest {

    private Connection connection;

    @Before
    public void setUp() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        connection = DriverManager.getConnection("jdbc:derby:memory:myDB;create=true");
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testCreateQuery() throws Exception {
        TableName aTable = new TableName("a table");
        ColumnName aColName = new ColumnName(aTable, "a column");
        ImmutableSet<Column> columns = ImmutableSet.of(
                new Column(aColName, Types.INTEGER, 5, 0, DatabaseMetaData.columnNullable));
        Inserter inserter = new Inserter(connection, aTable, columns);
        Assert.assertEquals("INSERT INTO \"a table\" (\"a table\".\"a column\") VALUES(?)", inserter.getQuery());
    }

}
