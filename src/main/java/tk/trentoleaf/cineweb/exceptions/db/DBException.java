package tk.trentoleaf.cineweb.exceptions.db;

import java.sql.SQLException;

/**
 * Wrap a database error.
 */
public class DBException extends RuntimeException {

    // A collection of common SQL error states in PostgresSQL
    public static final String NOT_NULL_VIOLATION_CODE = "23502";
    public static final String FOREIGN_KEY_VIOLATION_CODE = "23503";
    public static final String UNIQUE_VIOLATION_CODE = "23505";

    // original sql exception
    private SQLException sqlException;

    // construct a new DBException wrapping a SQLException
    protected DBException(SQLException sqlException) {
        this.sqlException = sqlException;
    }

    // get the original cause
    @SuppressWarnings("unused")
    public SQLException getSqlException() {
        return sqlException;
    }

    // construct from SQLException
    public static DBException factory(SQLException sqlException) {
        switch (sqlException.getSQLState()) {
            case NOT_NULL_VIOLATION_CODE:
                return new NotNullViolationException(sqlException);
            case FOREIGN_KEY_VIOLATION_CODE:
                return new ForeignKeyException(sqlException);
            case UNIQUE_VIOLATION_CODE:
                return new UniqueViolationException(sqlException);
            default:
                return new UnknownDBException(sqlException);
        }
    }

}
