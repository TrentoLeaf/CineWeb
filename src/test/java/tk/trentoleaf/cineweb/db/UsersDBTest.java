package tk.trentoleaf.cineweb.db;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import tk.trentoleaf.cineweb.exceptions.db.*;
import tk.trentoleaf.cineweb.beans.model.Role;
import tk.trentoleaf.cineweb.beans.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UsersDBTest extends DBTest {

    @Test
    public void createUserSuccess() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        // expected users
        final List<User> expected = new ArrayList<>();
        expected.add(u2);
        expected.add(u1);

        // current users
        final List<User> current = usersDB.getUsers();

        // test
        assertEquals(2, current.size());
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void testEmailCase1() throws Exception {

        // email
        final String email = "T342eO@ddAAbb.com";

        // create users
        final User expected = new User(true, Role.ADMIN, email, "teo", "Matteo", "Zeni");

        // save users
        usersDB.createUser(expected);

        // from usersDB
        final User current = usersDB.getUser(email.toLowerCase());

        // test
        assertEquals(expected, current);
    }

    @Test
    public void testEmailCase2() throws Exception {

        // email
        final String email = "TeO@ddAAbb.com";

        // create users
        final User u1 = new User(true, Role.ADMIN, email, "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);
        u1.setEmail(email.toUpperCase());

        // expected
        final List<User> expected = new ArrayList<>();
        expected.add(u1);

        // from usersDB
        final List<User> current = usersDB.getUsers();

        // test
        assertTrue(CollectionUtils.isEqualCollection(current, expected));
    }


    @Test(expected = ConstrainException.class)
    public void createUserFail() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(true, Role.CLIENT, "Teo@teo.com", "dada", "Davide", "Pedranz");

        // save users
        usersDB.createUser(u1);
        usersDB.createUser(u2);
    }

    @Test
    public void createUserAdmin() throws Exception {

        // create admin
        usersDB.createAdminUser();   // ok
        usersDB.createAdminUser();   // fails -> no exception

        // expected
        final List<User> expected = new ArrayList<>();
        expected.add(User.FIRST_ADMIN);

        // current
        final List<User> current = usersDB.getUsers();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFail() throws Exception {
        usersDB.getUser("no_email");
    }

    @Test
    public void updateUserSuccess() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        // edit user 2
        u2.setSecondName("Rossi");
        u2.setRole(Role.ADMIN);
        u2.setEmail("davide@tk.org");
        u2.setCredit(34.23);

        // update user
        usersDB.updateUser(u2);

        // current
        User retrieved = usersDB.getUser("davide@tk.org");

        // test
        assertEquals(u2, retrieved);
    }

    @Test(expected = UserNotFoundException.class)
    public void updateUserFail1() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "aaaaaaaaaa", "bbbbbbbb", "cccccc", "ddddddd");

        // update user
        usersDB.updateUser(u1);
    }

    @Test(expected = ConstrainException.class)
    public void updateUserFail2() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "aaa", "aaa", "aaa", "aaa");
        final User u2 = new User(true, Role.ADMIN, "bbb", "bbb", "bbb", "bbb");
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        // update user
        u1.setEmail(u2.getEmail().toUpperCase());
        usersDB.updateUser(u1);
    }

    @Test
    public void deleteUserSuccess1() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        // delete user 1
        usersDB.deleteUser(u1.getUid());

        // expected users
        final List<User> expected = new ArrayList<>();
        expected.add(u2);

        // current users
        final List<User> current = usersDB.getUsers();

        // test
        assertEquals(1, current.size());
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void deleteUserSuccess2() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        // delete user 1
        usersDB.deleteUser(u2);

        // expected users
        final List<User> expected = new ArrayList<>();
        expected.add(u1);

        // current users
        final List<User> current = usersDB.getUsers();

        // test
        assertEquals(1, current.size());
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteUserFail() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");

        // delete user 1
        usersDB.deleteUser(u1.getUid());
    }

    @Test
    public void authentication() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        // try to authenticate
        assertTrue(usersDB.authenticate("teo@teo.com", "teo"));
        assertFalse(usersDB.authenticate("teo@teo.com", "pippo"));
        assertFalse(usersDB.authenticate("home", "teo"));
        assertFalse(usersDB.authenticate("davide@pippo.com", "teo"));
    }

    @Test
    public void changeUserStatusSuccess() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // change status
        usersDB.changeUserStatus(u1.getUid(), false);
        u1.setEnabled(false);

        // expected
        final List<User> expected = new ArrayList<>();
        expected.add(u1);

        // current
        final List<User> current = usersDB.getUsers();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));

        // check auth fail
        assertFalse(usersDB.authenticate("teo@teo.com", "teo"));
    }

    @Test(expected = UserNotFoundException.class)
    public void changeUserStatusFail() throws Exception {

        // change status
        usersDB.changeUserStatus(2345, false);
    }

    @Test
    public void confirmUserSuccess() throws Exception {

        // create user
        final User u1 = new User(false, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // existing
        final String code = usersDB.requestConfirmationCode(u1.getUid());
        usersDB.confirmUser(code);
    }

    @Test(expected = UserAlreadyActivatedException.class)
    public void confirmUserFail1() throws Exception {

        // create user
        final User u1 = new User(false, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // existing
        final String code = usersDB.requestConfirmationCode(u1.getUid());
        usersDB.confirmUser(code);
        usersDB.confirmUser(code);
    }

    @Test(expected = UserNotFoundException.class)
    public void confirmUserFail2() throws Exception {

        // create user
        final User u1 = new User(false, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // existing
        usersDB.requestConfirmationCode(u1.getUid());
        usersDB.confirmUser("sdfsdfsd");
    }

    @Test
    public void changePasswordSuccess() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // change password
        usersDB.changePasswordWithOldPassword("teo@teo.com", "teo", "pippo");

        // check
        assertFalse(usersDB.authenticate("teo@teo.com", "teo"));
        assertTrue(usersDB.authenticate("teo@teo.com", "pippo"));
    }

    @Test(expected = WrongPasswordException.class)
    public void changePasswordFail1() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // change password
        usersDB.changePasswordWithOldPassword("teo@teo.com", "wrong_password", "new_password");
    }

    @Test(expected = WrongPasswordException.class)
    public void changePasswordFail2() throws Exception {

        // change password
        usersDB.changePasswordWithOldPassword("not_existing_user", "wrong_password", "new_password");
    }

    @Test(expected = UserNotFoundException.class)
    public void requestResetPasswordFail() throws Exception {
        usersDB.requestResetPassword(1234534);
    }

    @Test
    public void resetPasswordOk() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // get code
        String code = usersDB.requestResetPassword(u1.getUid());
        assertTrue(usersDB.checkPasswordReset(u1.getUid(), code));
        assertTrue(usersDB.checkPasswordReset(u1.getEmail(), code));

        // reset password
        usersDB.changePasswordWithCode("teo@teo.com", code, "pippo");

        // check
        assertFalse(usersDB.authenticate("teo@teo.com", "teo"));
        assertTrue(usersDB.authenticate("teo@teo.com", "pippo"));
    }

    @Test(expected = WrongCodeException.class)
    public void resetPasswordFail() throws Exception {

        // create users
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // random code
        String code = "random_code";
        assertFalse(usersDB.checkPasswordReset(u1.getUid(), code));
        assertFalse(usersDB.checkPasswordReset(u1.getEmail(), code));

        // change password
        usersDB.changePasswordWithCode("teo@teo.com", code, "new_password");
    }

}