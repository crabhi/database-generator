package cz.flih.database.generator;

import com.google.common.collect.ImmutableList;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public final class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static final List<String> DRIVERS = ImmutableList.of("org.apache.derby.jdbc.EmbeddedDriver");

    private static void loadDrivers() {
        for (String driver : DRIVERS) {
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, "Driver not found: {0}", driver);
            }
        }
    }

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        String url = args[0];

        run(url);
    }

    public static void run(String jdbcUrl) throws SQLException {
        loadDrivers();

        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            MetaData md = new MetaData(conn.getMetaData());
            Generator gen = new Generator(conn, md);
            gen.addData();
        }
    }
}
