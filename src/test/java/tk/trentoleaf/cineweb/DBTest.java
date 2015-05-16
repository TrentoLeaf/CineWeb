package tk.trentoleaf.cineweb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.exceptions.UserNotFoundException;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DBTest {

    private DB db;

    @Before
    public void before() throws SQLException, URISyntaxException, ClassNotFoundException {
        db = new DB();
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

    @Test
    public void createUser() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");
        db.createUser(u1);
        db.createUser(u2);

        // check users in db
        final List<User> users = db.getUsers();
        assertEquals(2, users.size());

        // try to authenticate
        assertTrue(db.authenticate("teo@teo.com", "teo"));
        assertFalse(db.authenticate("teo@teo.com", "pippo"));
        assertFalse(db.authenticate("home", "teo"));
        assertFalse(db.authenticate("davide@pippo.com", "teo"));

        // change password
        db.changePassword("teo@teo.com", "pippo");

        // check
        assertFalse(db.authenticate("teo@teo.com", "teo"));
        assertTrue(db.authenticate("teo@teo.com", "pippo"));

    }
}