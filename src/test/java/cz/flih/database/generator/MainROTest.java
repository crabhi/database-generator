/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
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
import java.util.Collection;
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
    private static final String JDBC_URL = "jdbc:derby:" + DB_LOCATION;
    private static Connection conn;

    @BeforeClass
    public static void setUp() throws Exception {
        deleteDir(DB_LOCATION);
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

        conn = DriverManager.getConnection(JDBC_URL + ";create=true");
        ScriptRunner runner = new ScriptRunner(conn, true, true);
        try (Reader fr = new FileReader("src/test/sql/main.sql");
                Reader reader = new BufferedReader(fr)) {
            runner.runScript(reader);
        }
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
        Generator gen = new Generator(conn, metaData);

        Set<TableName> expected = ImmutableSet.of(new TableName("SINGLE"), new TableName("MAIN"));
        Set<TableName> tables = gen.getStartingTables().collect(Collectors.toSet());

        assertEquals(expected, tables);
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