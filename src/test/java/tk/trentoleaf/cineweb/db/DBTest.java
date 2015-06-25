package tk.trentoleaf.cineweb.db;

import org.junit.After;
import org.junit.Before;

import java.net.URISyntaxException;
import java.sql.SQLException;

public class DBTest {

    private DB db;
    protected final UsersDB usersDB = UsersDB.instance();
    protected final FilmsDB filmsDB = FilmsDB.instance();
    protected final PlaysDB playsDB = PlaysDB.instance();
    protected final RoomsDB roomsDB = RoomsDB.instance();
    protected final BookingsDB bookingsDB = BookingsDB.instance();
    protected final PricesDB pricesDB = PricesDB.instance();

    @Before
    public void before() throws SQLException, URISyntaxException, ClassNotFoundException {
        db = DB.instance();
        db.open();
        db.reset();
        db.init();
    }

    @After
    public void after() throws SQLException {
        if (db != null) {
            db.close();
        }
    }

}