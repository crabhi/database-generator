package cz.flih.database.generator.random;

import cz.flih.database.generator.artifacts.Column;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krab
 */
public class ValueGeneratorRegistry {

    private static final Logger LOG = Logger.getLogger(ValueGeneratorRegistry.class.getName());
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
                return new IntGenerator(col.getSize() - col.getScale());
            case Types.DECIMAL:
            case Types.NUMERIC:
                if (col.getScale() == 0) {
                    return new IntGenerator(col.getSize());
                }
                // intentional fallthrough
            case Types.FLOAT:
            case Types.DOUBLE:
                return new RealGenerator(col.getSize(), col.getScale());
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.CLOB:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
            case Types.NCHAR:
            case Types.NCLOB:
                return new TextGenerator(col.getSize());
            default:
                if (col.getNullable() != DatabaseMetaData.columnNoNulls) {
                    LOG.log(Level.WARNING, "SQL type {0} not supported, using NULL generator for {1}",
                            new Object[]{col.getJdbcType(), col.getName()});
                    return new NullGenerator();
                }
                throw new UnsupportedOperationException(
                        MessageFormat.format("SQL type {0} not supported (column {1})",
                                col.getJdbcType(), col.getName()));
        }
    }

}
