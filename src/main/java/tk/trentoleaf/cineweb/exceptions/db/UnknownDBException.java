package tk.trentoleaf.cineweb.exceptions.db;

import java.sql.SQLException;

public class UnknownDBException extends DBException {

    public UnknownDBException(SQLException sqlException) {
        super(sqlException);
    }

}
