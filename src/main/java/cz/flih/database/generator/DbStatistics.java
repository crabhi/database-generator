/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 *
 * @author Krab
 */
public class DbStatistics {

    private final Map<TableName, Integer> sizes;

    public DbStatistics(Map<TableName, Integer> sizes) {
        this.sizes = sizes;
    }

    /**
     * Loads statistics from a file with columns table name and row count. The file header (first line) is ignored.
     *
     * @param csvFile the file to read
     * @return built statistics
     * @throws java.io.IOException on read error
     */
    public static DbStatistics from(File csvFile) throws IOException {
        ImmutableMap.Builder<TableName, Integer> builder = ImmutableMap.builder();
        try (Reader r = new BufferedReader(new FileReader(csvFile));
                CSVReader csvReader = new CSVReader(r)) {

            csvReader.readNext(); // ignore the header

            while (true) {
                String[] row = csvReader.readNext();
                if (row == null) {
                    return new DbStatistics(builder.build());
                } else {
                    builder.put(TableName.parse(row[0]), Integer.valueOf(row[1]));
                }
            }
        }
    }
}
