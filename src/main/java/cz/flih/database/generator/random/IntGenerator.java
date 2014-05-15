package cz.flih.database.generator.random;

import com.google.common.math.IntMath;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;

/**
 *
 * @author krab
 */
public class IntGenerator implements ValueGenerator<Integer> {
    private final IntegerDistribution distribution;
    private final EnumeratedIntegerDistribution signDistribution;

    public IntGenerator(int size) {
        int max = IntMath.pow(10, size);

        distribution = new BinomialDistribution(max, 0.05);
        signDistribution = new EnumeratedIntegerDistribution(new int[]{-1, 1}, new double[] {0.5, 0.5});
    }

    @Override
    public Integer nextValue() {
        return distribution.sample() * signDistribution.sample();
    }

}
