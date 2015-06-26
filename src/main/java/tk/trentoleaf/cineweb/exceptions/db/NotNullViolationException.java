package tk.trentoleaf.cineweb.exceptions.db;

import java.sql.SQLException;

public class NotNullViolationException extends DBException {

    public NotNullViolationException(SQLException sqlException) {
        super(sqlException);
    }

}
