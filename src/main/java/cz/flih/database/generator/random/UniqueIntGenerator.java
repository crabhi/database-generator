/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.flih.database.generator.random;

import com.google.common.math.IntMath;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.math.RandomUtils;

/**
 *
 * @author Krab
 */
public class UniqueIntGenerator implements ValueGenerator<Integer> {
    private final int max;
    private final AtomicInteger lastValue;

    public UniqueIntGenerator(int size) {
        max = IntMath.pow(10, size);
        int start = RandomUtils.nextInt(max);
        lastValue = new AtomicInteger(start);
    }

    @Override
    public Integer nextValue() {
        int nextVal = lastValue.incrementAndGet();
        while (nextVal >= max) {
            lastValue.compareAndSet(nextVal, 0);
            nextVal = lastValue.incrementAndGet();
        }
        return nextVal;
    }

}
