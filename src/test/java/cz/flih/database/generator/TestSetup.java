package cz.flih.database.generator;

import cz.flih.database.generator.thirdparty.ScriptRunner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author krab
 */
public final class TestSetup {

    private static final String JDBC_PREFIX = "jdbc:derby:";

    private TestSetup() {
    }

    public static Connection prepareDB(String location) throws IOException, SQLException {
        deleteDir(location);
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException ex) {
            throw new AssertionError(ex);
        }

        Connection connection = DriverManager.getConnection(JDBC_PREFIX + location + ";create=true");
        ScriptRunner runner = new ScriptRunner(connection, true, true);
        try (Reader fr = new FileReader("src/test/sql/main.sql");
                Reader reader = new BufferedReader(fr)) {
            runner.runScript(reader);
        }
        return connection;
    }

    private static void deleteDir(String dir) throws IOException {
        File f = new File(dir);
        if (!f.exists()) {
            return;
        }
        Files.walkFileTree(f.toPath(), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
