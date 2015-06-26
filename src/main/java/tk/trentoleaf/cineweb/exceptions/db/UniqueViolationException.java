package tk.trentoleaf.cineweb.exceptions.db;

import java.sql.SQLException;

public class UniqueViolationException extends DBException {

    public UniqueViolationException(SQLException sqlException) {
        super(sqlException);
    }

}
