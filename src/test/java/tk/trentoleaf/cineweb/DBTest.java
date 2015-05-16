package tk.trentoleaf.cineweb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tk.trentoleaf.cineweb.db.DB;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class DBTest {

    private DB db;

    @Before
    public void before() throws SQLException, URISyntaxException, ClassNotFoundException {
        db = new DB();
        db.open();
    }

    @After
    public void after() throws SQLException {
        if(db != null) {
            db.close();
        }
    }

    @Test
    public void createUser() {
        assertTrue(true);
    }
}