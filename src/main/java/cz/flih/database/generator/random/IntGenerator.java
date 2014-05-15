package cz.flih.database.generator.random;

import com.google.common.math.IntMath;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.GeometricDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;

/**
 *
 * @author krab
 */
public class IntGenerator implements ValueGenerator<Integer> {
    private final IntegerDistribution distribution;
    private final EnumeratedIntegerDistribution signDistribution;
    private final int max;

    public IntGenerator(int size) {
        max = IntMath.pow(10, size);

        distribution = new GeometricDistribution(Math.pow(2, -size));
        signDistribution = new EnumeratedIntegerDistribution(new int[]{-1, 1}, new double[] {3, 7});
    }

    @Override
    public Integer nextValue() {
        int sample = Math.min(distribution.sample(), max);
        return sample * signDistribution.sample();
    }

}
