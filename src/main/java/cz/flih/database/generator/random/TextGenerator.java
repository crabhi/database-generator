/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.flih.database.generator.random;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

/**
 *
 * @author Krab
 */
public class TextGenerator implements ValueGenerator<String> {
    private final IntegerDistribution sizeDist;

    public TextGenerator(int size) {
        sizeDist = new UniformIntegerDistribution(0, Math.min(size, 6000));
    }

    @Override
    public String nextValue() {
        int textSize = sizeDist.sample();
        return RandomStringUtils.randomAlphanumeric(textSize);
    }

}
