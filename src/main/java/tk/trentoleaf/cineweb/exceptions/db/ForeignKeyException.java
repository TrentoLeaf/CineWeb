package tk.trentoleaf.cineweb.exceptions.db;

import java.sql.SQLException;

/**
 * Exception thrown when an operation in the database fails because of
 * a foreign key constraint violation.
 */
public class ForeignKeyException extends DBException {

    /**
     * Construct a new ForeignKeyException.
     *
     * @param sqlException Original SQLException.
     */
    public ForeignKeyException(SQLException sqlException) {
        super(sqlException);
    }

}
