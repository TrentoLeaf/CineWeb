package tk.trentoleaf.cineweb.exceptions.db;

import java.sql.SQLException;

public class ForeignKeyException extends DBException {

    public ForeignKeyException(SQLException sqlException) {
        super(sqlException);
    }

}
