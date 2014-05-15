package cz.flih.database.generator;

import cz.flih.database.generator.ref.TableName;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Quoting utility.
 */
public class Q {

    private final String quotestr;

    public Q(DatabaseMetaData dbInfo) throws SQLException {
        quotestr = dbInfo.getIdentifierQuoteString();
    }

    public String quoted(TableName table) {
        return quoted(table.getTable());
    }

    private String quoted(String val) {
        if (val.contains(quotestr)) {
            throw new UnsupportedOperationException("Quoting values containing " + quotestr + " not supported.");
        }
        return quotestr + val + quotestr;
    }
}
