/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.flih.database.generator;

import cz.flih.database.generator.ref.TableName;
import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import cz.flih.database.generator.artifacts.ForeignKey;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author Krab
 */
public class DbStatistics {

    private final Map<TableName, Integer> sizes;
    private final RandomGenerator rng;
    // private final Map<ForeignKey, IntegerDistribution>
    private final IntegerDistribution defaultKeyDist;

    public DbStatistics(Map<TableName, Integer> sizes) {
        this(sizes, new JDKRandomGenerator());
    }

    public DbStatistics(Map<TableName, Integer> sizes, RandomGenerator rng) {
        this.sizes = Preconditions.checkNotNull(sizes);
        this.rng = rng;

        defaultKeyDist = createQuasiGeometricDist(rng);
    }

    private IntegerDistribution createQuasiGeometricDist(RandomGenerator rng) {
        int samples = 100;
        double p = 0.5;
        int[] singletons = IntStream.range(0, samples).toArray();
        double[] probabilities = IntStream.range(0, samples).mapToDouble((k) -> {
            return Math.pow(1 - p, k) * p;
        }).toArray();
        return new EnumeratedIntegerDistribution(rng, singletons, probabilities);
    }

    public Optional<Integer> getTableSize(TableName table) {
        return Optional.ofNullable(sizes.get(table));
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

    public int getRandomChildrenCount(ForeignKey foreignKey) {
        return defaultKeyDist.sample();
    }
}
