/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.flih.database.generator.random;

/**
 * Generates always nulls.
 */
public class NullGenerator implements ValueGenerator<Object> {

    @Override
    public Object nextValue() {
        return SqlNull.INSTANCE;
    }

}
