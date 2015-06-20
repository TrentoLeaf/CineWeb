package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.exceptions.db.*;
import tk.trentoleaf.cineweb.beans.model.Role;
import tk.trentoleaf.cineweb.beans.model.User;

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
    public void createUser(User user) throws SQLException, ConstrainException {
        final String query = "INSERT INTO users (uid, enabled, roleid, email, pass, first_name, second_name)" +
                "VALUES (DEFAULT, ?, ?, lower(?), crypt(?, gen_salt('bf')), ?, ?) RETURNING uid";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setBoolean(1, user.isEnabled());
            stm.setString(2, user.getRole().getRoleID());
            stm.setString(3, user.getEmail());
            stm.setString(4, user.getPassword());
            stm.setString(5, user.getFirstName());
            stm.setString(6, user.getSecondName());
            ResultSet rs = stm.executeQuery();
            rs.next();
            user.setUid(rs.getInt("uid"));
        } catch (PSQLException e) {
            throw new ConstrainException(e);
        }
    }

    // get a single user
    public User getUser(int uid) throws SQLException, UserNotFoundException {
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
        }
    }

    // get a single user
    public User getUser(String email) throws SQLException, UserNotFoundException {
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
        }
    }

    // list of users
    public List<User> getUsers() throws SQLException {
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
        }

        return users;
    }

    // update a user -> NB: does not change the password
    public void updateUser(User user) throws SQLException, UserNotFoundException, ConstrainException {
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
        } catch (PSQLException e) {
            throw new ConstrainException(e);
        }
    }

    // delete an user
    public void deleteUser(User user) throws SQLException, UserNotFoundException {
        deleteUser(user.getUid());
    }

    // delete an user
    // NB: cascade
    public void deleteUser(int id) throws SQLException, UserNotFoundException {
        final String query = "DELETE FROM users WHERE uid = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, id);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }
        }
    }

    // create user admin
    public void createAdminUser() throws SQLException {
        // create first user
        try {
            createUser(User.FIRST_ADMIN);
        } catch (ConstrainException e) {
            logger.warning("User FIRST_ADMIN already exixsts");
        }
    }

    // get the role of a given (enabled) user
    public Role getUserRoleIfEnabled(int uid) throws SQLException {
        final String query = "SELECT roleid FROM users WHERE uid = ? AND enabled = TRUE;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, uid);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return Role.fromID(rs.getString("roleid"));
            } else {
                return null;
            }
        }
    }

    // get the reset password code for a given user -> TEST PORPOISE ONLY
    public String getResetPasswordCode(String email) throws SQLException {
        final String query = "SELECT r.code FROM resets r NATURAL JOIN users u WHERE u.email = ? LIMIT 1;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, email);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }
        }
    }

    // get the confirmation code for a given user -> TEST PORPOISE ONLY
    public String getConfirmationCode(String email) throws SQLException {
        final String query = "SELECT r.code FROM registration_codes r NATURAL JOIN users u WHERE u.email = ? LIMIT 1;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, email);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }
        }
    }

    // exists user
    public boolean existsUser(int userID) throws SQLException {
        final String query = "SELECT COUNT(uid) FROM users WHERE uid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, userID);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1) == 1;
        }
    }

    // exists user & enabled
    public boolean existsAndEnabledUser(int userID) throws SQLException {
        final String query = "SELECT COUNT(uid) FROM users WHERE enabled = TRUE AND uid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, userID);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1) == 1;
        }
    }

    // authenticate a user
    public boolean authenticate(String email, String password) throws SQLException {
        final String query = "SELECT COUNT(email) FROM users WHERE enabled = TRUE AND email = ? AND pass = crypt(?, pass);";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, email);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1) == 1;
        }
    }

    // check password reset
    public boolean checkPasswordReset(int userID, String code) throws SQLException {
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
        }
    }

    // check password reset
    public boolean checkPasswordReset(String email, String code) throws SQLException {
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
        }
    }

    // enable or disable a user
    public void changeUserStatus(int uid, boolean enable) throws SQLException, UserNotFoundException {
        final String query = "UPDATE users SET enabled = ? WHERE uid = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setBoolean(1, enable);
            stm.setInt(2, uid);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }
        }
    }

    // change password
    private void changePassword(String email, String newPassword) throws SQLException, UserNotFoundException {
        final String query = "UPDATE users SET pass = crypt(?, gen_salt('bf')) WHERE enabled = TRUE AND email = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, newPassword);
            stm.setString(2, email);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }
        }
    }

    // change password given the old one
    public void changePasswordWithOldPassword(String email, String oldPassword, String newPassword) throws UserNotFoundException, WrongPasswordException, SQLException {

        // check old password
        if (!authenticate(email, oldPassword)) {
            throw new WrongPasswordException();
        }

        // change password
        changePassword(email, newPassword);
    }

    // change password given a code
    public void changePasswordWithCode(String email, String code, String newPassword) throws SQLException, WrongCodeException, UserNotFoundException {

        // check code
        if (!checkPasswordReset(email, code)) {
            throw new WrongCodeException();
        }

        // change password
        changePassword(email, newPassword);
    }

    // request a confirmation code
    public String requestConfirmationCode(int userID) throws UserNotFoundException, SQLException {

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
        } catch (PSQLException e) {
            logger.warning("Cannot create a confirmation code for the user with uid: " + userID + " -> " + e.toString());
            throw e;
        }

        return code;
    }

    // check a confirmation code
    public void confirmUser(String code) throws SQLException, UserNotFoundException, UserAlreadyActivatedException {
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
        }
    }

    // request a password recovery
    public String requestResetPassword(int userID) throws UserNotFoundException, SQLException {
        return requestResetPassword(userID, 15);
    }

    // request a password recovery
    public String requestResetPassword(int userID, int expireInMinutes) throws UserNotFoundException, SQLException {

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

            } catch (PSQLException e) {
                logger.warning("Cannot create a password recovery code for user uid: " + userID + " -> " + e.toString());
            }
        }
        throw new RuntimeException("Cannot create a password recovery code for this user: " + userID);
    }

}
