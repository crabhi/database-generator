/*
 * Copyright (c) 1998-2014 ChemAxon Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * ChemAxon. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the agreements
 * you entered into with ChemAxon.
 */
package cz.flih.database.generator.random;

import cz.flih.database.generator.artifacts.Column;
import java.sql.Types;
import java.text.MessageFormat;

/**
 *
 * @author krab
 */
public class ValueGeneratorRegistry {

    private static final ValueGeneratorRegistry INSTANCE = new ValueGeneratorRegistry();

    public static ValueGeneratorRegistry getInstance() {
        return INSTANCE;
    }

    private ValueGeneratorRegistry() {
    }

    @SuppressWarnings("fallthrough")
    public ValueGenerator<?> createGenerator(Column col) {
        switch (col.getJdbcType()) {
            case Types.BIGINT:
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.BIT:
                return new IntGenerator(col.getSize());
            case Types.DECIMAL:
            case Types.NUMERIC:
                if (col.getScale() == 0) {
                    return new IntGenerator(col.getSize());
                }
            default:
                throw new UnsupportedOperationException(
                        MessageFormat.format("SQL type {0} not supported", col.getJdbcType()));
        }
    }

}
