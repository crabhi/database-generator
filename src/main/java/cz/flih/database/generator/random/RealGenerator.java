/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.flih.database.generator.random;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

/**
 *
 * @author Krab
 */
public class RealGenerator implements ValueGenerator<Double> {
    private final UniformRealDistribution dist;

    public RealGenerator(int size, int scale) {
        dist = new UniformRealDistribution(-Math.pow(10, size - scale), Math.pow(10, size - scale));
    }

    @Override
    public Double nextValue() {
        return dist.sample();
    }

}
