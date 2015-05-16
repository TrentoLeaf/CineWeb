package tk.trentoleaf.cineweb;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.exceptions.WrongCodeException;
import tk.trentoleaf.cineweb.exceptions.WrongPasswordException;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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

        // save users
        db.createUser(u1);
        db.createUser(u2);

        // expected users
        final List<User> expected = new ArrayList<>();
        expected.add(u2);
        expected.add(u1);

        // current users
        final List<User> current = db.getUsers();

        // test
        assertEquals(2, current.size());
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void deleteUser() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        db.createUser(u1);
        db.createUser(u2);

        // delete user 1
        db.deleteUser(u1.getId());

        // expected users
        final List<User> expected = new ArrayList<>();
        expected.add(u2);

        // current users
        final List<User> current = db.getUsers();

        // test
        assertEquals(1, current.size());
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void authentication() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        db.createUser(u1);
        db.createUser(u2);

        // try to authenticate
        assertTrue(db.authenticate("teo@teo.com", "teo"));
        assertFalse(db.authenticate("teo@teo.com", "pippo"));
        assertFalse(db.authenticate("home", "teo"));
        assertFalse(db.authenticate("davide@pippo.com", "teo"));
    }

    @Test
    public void changePasswordSuccess() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u1);

        // change password
        db.changePasswordWithOldPassword("teo@teo.com", "teo", "pippo");

        // check
        assertFalse(db.authenticate("teo@teo.com", "teo"));
        assertTrue(db.authenticate("teo@teo.com", "pippo"));
    }

    @Test(expected = WrongPasswordException.class)
    public void changePasswordFail() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u1);

        // change password
        db.changePasswordWithOldPassword("teo@teo.com", "wrong_password", "new_password");
    }

    @Test
    public void resetPasswordOk() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u1);

        // get code
        String code = db.requestResetPassword(u1.getId());
        assertTrue(db.checkPasswordReset(u1.getId(), code));
        assertTrue(db.checkPasswordReset(u1.getEmail(), code));

        // reset password
        db.changePasswordWithCode("teo@teo.com", code, "pippo");

        // check
        assertFalse(db.authenticate("teo@teo.com", "teo"));
        assertTrue(db.authenticate("teo@teo.com", "pippo"));
    }

    @Test(expected = WrongCodeException.class)
    public void resetPasswordFail() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u1);

        // random code
        String code = "random_code";
        assertFalse(db.checkPasswordReset(u1.getId(), code));
        assertFalse(db.checkPasswordReset(u1.getEmail(), code));

        // change password
        db.changePasswordWithCode("teo@teo.com", code, "new_password");
    }

}