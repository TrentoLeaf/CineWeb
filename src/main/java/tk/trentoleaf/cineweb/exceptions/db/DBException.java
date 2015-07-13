package tk.trentoleaf.cineweb.exceptions.db;

import java.sql.SQLException;

/**
 * Wrap a database error.
 */
public class DBException extends RuntimeException {

    // A collection of common SQL error states in PostgresSQL
    private static final String NOT_NULL_VIOLATION_CODE = "23502";
    private static final String FOREIGN_KEY_VIOLATION_CODE = "23503";
    private static final String UNIQUE_VIOLATION_CODE = "23505";

    /**
     * Construct a new DBException wrapping a SQLException.
     *
     * @param sqlException Original SQL exception.
     */
    protected DBException(SQLException sqlException) {
        super(sqlException);
    }

    /**
     * Factory to build the correct subclass of DBException from a SQL exception.
     *
     * @param sqlException Original SQL exception.
     * @return DBException.
     */
    public static DBException factory(SQLException sqlException) {
        switch (sqlException.getSQLState()) {
            case NOT_NULL_VIOLATION_CODE:
                return new NotNullViolationException(sqlException);
            case FOREIGN_KEY_VIOLATION_CODE:
                return new ForeignKeyException(sqlException);
            case UNIQUE_VIOLATION_CODE:
                return new UniqueViolationException(sqlException);
            default:
                return new DBException(sqlException);
        }
    }

}
