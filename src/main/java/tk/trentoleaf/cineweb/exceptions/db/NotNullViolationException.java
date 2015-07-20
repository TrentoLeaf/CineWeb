package tk.trentoleaf.cineweb.exceptions.db;

import java.sql.SQLException;

/**
 * Exception thrown when an operation in the database fails because of
 * a not null constraint violation.
 */
public class NotNullViolationException extends DBException {

    /**
     * Construct a new NotNullViolationException.
     *
     * @param sqlException Original SQLException.
     */
    public NotNullViolationException(SQLException sqlException) {
        super(sqlException);
    }

}
