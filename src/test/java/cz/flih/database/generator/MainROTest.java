/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator;

import cz.flih.database.generator.ref.TableName;
import cz.flih.database.generator.artifacts.ForeignKey;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.AfterClass;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Krab
 */
public class MainROTest {

    private static final String DB_LOCATION = "test/db";

    private static Connection conn;

    @BeforeClass
    public static void setUp() throws Exception {
        conn = TestSetup.prepareDB(DB_LOCATION);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    public void testStartingTables() throws Exception {
        MetaData metaData = new MetaData(conn.getMetaData());
        Generator gen = new Generator(conn, metaData, new DbStatistics(ImmutableMap.of()));

        Set<TableName> expected = ImmutableSet.of(new TableName("SINGLE"), new TableName("MAIN"));
        Set<TableName> tables = gen.getStartingTables().collect(Collectors.toSet());

        assertEquals(expected, tables);
    }

    @Test
    public void testOutgoingRels() throws Exception {
        MetaData metaData = new MetaData(conn.getMetaData());
        Set<ForeignKey> oneToManyRels = metaData.getOutgoingRelations(new TableName("MAIN"));

        ForeignKey fk = Iterables.getOnlyElement(oneToManyRels);
        assertEquals(new TableName("MAIN"), fk.getPkTable());
        assertEquals(new TableName("DETAIL"), fk.getFkTable());
    }

}
