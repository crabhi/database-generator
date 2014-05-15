/*
 * Copyright (c) 1998-2014 ChemAxon Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * ChemAxon. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the agreements
 * you entered into with ChemAxon.
 */
package cz.flih.database.generator.random;

import com.google.common.math.IntMath;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;

/**
 *
 * @author krab
 */
public class IntGenerator implements ValueGenerator<Integer> {
    private final IntegerDistribution distribution;

    public IntGenerator(int size) {
        int max = IntMath.pow(10, size);

        distribution = new BinomialDistribution(max, 0.05);
    }

    @Override
    public Integer nextValue() {
        return distribution.sample();
    }

}
