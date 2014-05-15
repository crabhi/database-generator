package cz.flih.database.generator;

import com.google.common.collect.ImmutableMap;
import cz.flih.database.generator.ref.TableName;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author krab
 */
public class MainWriteTest {
    private static final String DB_LOCATION = "test/writedb";

    private static Connection conn;
    private static Q q;

    @Before
    public void setUp() throws Exception {
        conn = TestSetup.prepareDB(DB_LOCATION);
        q = new Q(conn.getMetaData());
    }

    @After
    public void tearDown() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    public void testGenerator() throws Exception {
        Map<TableName, Integer> tableSizes = ImmutableMap.of(TableName.parse("SINGLE"), 20,
                                                             TableName.parse("MAIN"), 50);

        Generator gen = new Generator(conn, new MetaData(conn.getMetaData()), new DbStatistics(tableSizes));
        gen.start();

        for (Map.Entry<TableName, Integer> e : tableSizes.entrySet()) {
            assertEquals("Checking size of " + e.getKey(), e.getValue(), getTableSize(e.getKey()));
        }
    }

    private Integer getTableSize(TableName table) throws SQLException {
        return DSL.using(conn, SQLDialect.DERBY).selectCount().from(table.toJooq()).fetchOne(0, int.class);
    }

}
