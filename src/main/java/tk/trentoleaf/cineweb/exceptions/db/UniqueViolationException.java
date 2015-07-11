package tk.trentoleaf.cineweb.exceptions.db;

import java.sql.SQLException;

/**
 * Exception thrown when an operation in the database fails because of
 * a unique constraint violation.
 */
public class UniqueViolationException extends DBException {

    /**
     * Construct a new UniqueViolationException.
     *
     * @param sqlException Original SQLException.
     */
    public UniqueViolationException(SQLException sqlException) {
        super(sqlException);
    }

}
