package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.beans.model.Role;
import tk.trentoleaf.cineweb.beans.model.User;
import tk.trentoleaf.cineweb.exceptions.db.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This class handles the users storage and authentication. It uses the connections provided by the DB class.
 *
 * @see tk.trentoleaf.cineweb.db.DB
 */
public class UsersDB {
    private final Logger logger = Logger.getLogger(UsersDB.class.getSimpleName());

    // get a DB instance
    protected DB db = DB.instance();

    // instance -> singleton pattern
    private static UsersDB instance;

    // instance -> singleton pattern
    public static UsersDB instance() {
        if (instance == null) {
            instance = new UsersDB();
        }
        return instance;
    }

    // force to use the singleton
    private UsersDB() {
    }

    // create user
    public void createUser(User user) throws DBException {
        final String query = "INSERT INTO users (uid, enabled, roleid, email, pass, first_name, second_name, credit)" +
                "VALUES (DEFAULT, ?, ?, lower(?), crypt(?, gen_salt('bf')), ?, ?, ?) RETURNING uid";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setBoolean(1, user.isEnabled());
            stm.setString(2, user.getRole().getRoleID());
            stm.setString(3, user.getEmail());
            stm.setString(4, user.getPassword());
            stm.setString(5, user.getFirstName());
            stm.setString(6, user.getSecondName());
            stm.setDouble(7, user.getCredit());
            ResultSet rs = stm.executeQuery();
            rs.next();
            user.setUid(rs.getInt("uid"));
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // get a single user
    public User getUser(int uid) throws DBException, UserNotFoundException {
        final String query = "SELECT enabled, email, roleid, first_name, second_name, credit FROM users WHERE uid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, uid);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUid(uid);
                u.setEnabled(rs.getBoolean("enabled"));
                u.setRole(Role.fromID(rs.getString("roleid")));
                u.setEmail(rs.getString("email"));
                u.setFirstName(rs.getString("first_name"));
                u.setSecondName(rs.getString("second_name"));
                u.setCredit(rs.getDouble("credit"));
                return u;
            }

            // if here -> no such user
            throw new UserNotFoundException();

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // get a single user
    public User getUser(String email) throws DBException, UserNotFoundException {
        final String query = "SELECT enabled, uid, roleid, first_name, second_name, credit FROM users WHERE email = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, email);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUid(rs.getInt("uid"));
                u.setEnabled(rs.getBoolean("enabled"));
                u.setRole(Role.fromID(rs.getString("roleid")));
                u.setEmail(email);
                u.setFirstName(rs.getString("first_name"));
                u.setSecondName(rs.getString("second_name"));
                u.setCredit(rs.getDouble("credit"));
                return u;
            }

            // if here -> no such user
            throw new UserNotFoundException();

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // list of users
    public List<User> getUsers() throws DBException {
        List<User> users = new ArrayList<>();

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT enabled, uid, roleid, lower(email) AS email, first_name, second_name, credit FROM users;");

            while (rs.next()) {
                User u = new User();
                u.setUid(rs.getInt("uid"));
                u.setEnabled(rs.getBoolean("enabled"));
                u.setRole(Role.fromID(rs.getString("roleid")));
                u.setEmail(rs.getString("email"));
                u.setFirstName(rs.getString("first_name"));
                u.setSecondName(rs.getString("second_name"));
                u.setCredit(rs.getDouble("credit"));
                users.add(u);
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return users;
    }

    // update a user -> NB: does not change the password
    public void updateUser(User user) throws DBException, UserNotFoundException {
        final String query = "UPDATE users SET enabled = ?, roleid = ?, email = ?, first_name = ?, second_name = ?, credit = ? WHERE uid = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setBoolean(1, user.isEnabled());
            stm.setString(2, user.getRole().getRoleID());
            stm.setString(3, user.getEmail());
            stm.setString(4, user.getFirstName());
            stm.setString(5, user.getSecondName());
            stm.setDouble(6, user.getCredit());
            stm.setInt(7, user.getUid());

            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // delete an user
    public void deleteUser(User user) throws DBException, UserNotFoundException {
        deleteUser(user.getUid());
    }

    // delete an user
    // NB: cascade
    public void deleteUser(int id) throws DBException, UserNotFoundException {
        final String query = "DELETE FROM users WHERE uid = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, id);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // create user admin
    public void createAdminUser() throws DBException {
        // create first user
        try {
            createUser(User.FIRST_ADMIN);
        } catch (UniqueViolationException e) {
            logger.warning("User FIRST_ADMIN already exixsts");
        }
    }

    // get the role of a given (enabled) user
    public Role getUserRoleIfEnabled(int uid) throws DBException {
        final String query = "SELECT roleid FROM users WHERE uid = ? AND enabled = TRUE;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, uid);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return Role.fromID(rs.getString("roleid"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // get the reset password code for a given user -> TEST PORPOISE ONLY
    public String getResetPasswordCode(String email) throws DBException {
        final String query = "SELECT r.code FROM resets r NATURAL JOIN users u WHERE u.email = ? LIMIT 1;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, email);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // get the confirmation code for a given user -> TEST PORPOISE ONLY
    public String getConfirmationCode(String email) throws DBException {
        final String query = "SELECT r.code FROM registration_codes r NATURAL JOIN users u WHERE u.email = ? LIMIT 1;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, email);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // exists user
    public boolean existsUser(int userID) throws DBException {
        final String query = "SELECT COUNT(uid) FROM users WHERE uid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, userID);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1) == 1;
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // exists user & enabled
    public boolean existsAndEnabledUser(int userID) throws DBException {
        final String query = "SELECT COUNT(uid) FROM users WHERE enabled = TRUE AND uid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, userID);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1) == 1;
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // authenticate a user
    public boolean authenticate(String email, String password) throws DBException {
        final String query = "SELECT COUNT(email) FROM users WHERE enabled = TRUE AND email = ? AND pass = crypt(?, pass);";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, email);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1) == 1;
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // check password reset
    public boolean checkPasswordReset(int userID, String code) throws DBException {
        final String query = "SELECT expiration FROM resets r NATURAL JOIN users u WHERE u.enabled = TRUE AND r.code = ? AND u.uid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, code);
            stm.setInt(2, userID);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Timestamp expire = rs.getTimestamp("expiration");
                return expire.after(new java.util.Date());
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // check password reset
    public boolean checkPasswordReset(String email, String code) throws DBException {
        final String query = "SELECT r.expiration FROM resets r NATURAL JOIN users u WHERE u.enabled = TRUE AND r.code = ? AND u.email = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, code);
            stm.setString(2, email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Timestamp expire = rs.getTimestamp("expiration");
                return expire.after(new java.util.Date());
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // enable or disable a user
    public void changeUserStatus(int uid, boolean enable) throws DBException, UserNotFoundException {
        final String query = "UPDATE users SET enabled = ? WHERE uid = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setBoolean(1, enable);
            stm.setInt(2, uid);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // change password
    private void changePassword(String email, String newPassword) throws DBException, UserNotFoundException {
        final String query = "UPDATE users SET pass = crypt(?, gen_salt('bf')) WHERE enabled = TRUE AND email = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, newPassword);
            stm.setString(2, email);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // change password given the old one
    public void changePasswordWithOldPassword(String email, String oldPassword, String newPassword) throws DBException, WrongCredentialsException {

        // check old password
        if (!authenticate(email, oldPassword)) {
            throw new WrongCredentialsException();
        }

        // change password
        try {
            changePassword(email, newPassword);
        } catch (UserNotFoundException e) {
            throw new WrongCredentialsException();
        }
    }

    // change password given a code
    public void changePasswordWithCode(String email, String code, String newPassword) throws DBException, WrongCredentialsException {

        // check code
        if (!checkPasswordReset(email, code)) {
            throw new WrongCredentialsException();
        }

        // change password
        try {
            changePassword(email, newPassword);
        } catch (UserNotFoundException e) {
            throw new WrongCredentialsException();
        }
    }

    // request a confirmation code
    public String requestConfirmationCode(int userID) throws DBException, UserNotFoundException {

        // check for userID
        if (!existsUser(userID)) {
            throw new UserNotFoundException();
        }

        // request confirmation code
        final String code = (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "");
        final String query = "INSERT INTO registration_codes (uid, code) VALUES (?, ?);";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, userID);
            stm.setString(2, code);
            stm.execute();
        } catch (SQLException e) {
            logger.warning("Cannot create a confirmation code for the user with uid: " + userID + " -> " + e.toString());
            throw DBException.factory(e);
        }

        return code;
    }

    // TODO: User already activated
    // check a confirmation code
    public void confirmUser(String code) throws DBException, UserNotFoundException, UserAlreadyActivatedException {
        final String queryFindUser = "SELECT uid FROM registration_codes WHERE code = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(queryFindUser)) {
            stm.setString(1, code);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                final int uid = rs.getInt(1);

                if (existsAndEnabledUser(uid)) {
                    throw new UserAlreadyActivatedException();
                }

                changeUserStatus(rs.getInt(1), true);

            } else {
                throw new UserNotFoundException();
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // request a password recovery
    public String requestResetPassword(int userID) throws DBException, UserNotFoundException {
        return requestResetPassword(userID, 15);
    }

    // request a password recovery
    public String requestResetPassword(int userID, int expireInMinutes) throws DBException, UserNotFoundException {

        // check for userID
        if (!existsAndEnabledUser(userID)) {
            throw new UserNotFoundException();
        }

        // request password reset
        final int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {

            final String code = (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "");
            final String query = "INSERT INTO resets (code, uid, expiration) VALUES (?, ?, ?);";

            try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
                stm.setString(1, code);
                stm.setInt(2, userID);
                stm.setTimestamp(3, new Timestamp(DateTime.now().plusMinutes(expireInMinutes).toDate().getTime()));
                stm.execute();

                return code;

            } catch (SQLException e) {
                logger.warning("Cannot create a password recovery code for user uid: " + userID + " -> " + e.toString());
            }
        }

        // unreachable -> very unprovable
        throw new RuntimeException("Cannot create a password recovery code for this user: " + userID);
    }

}
