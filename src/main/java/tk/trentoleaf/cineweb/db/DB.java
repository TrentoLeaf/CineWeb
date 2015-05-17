package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.exceptions.ConstrainException;
import tk.trentoleaf.cineweb.exceptions.UserNotFoundException;
import tk.trentoleaf.cineweb.exceptions.WrongCodeException;
import tk.trentoleaf.cineweb.exceptions.WrongPasswordException;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

public class DB {
    private final Logger logger = Logger.getLogger(DB.class.getSimpleName());

    // the connection to the database
    private Connection connection;

    // return a connection object reading the environment variables
    private static Connection getConnection() throws URISyntaxException, SQLException {
        final URI dbUri = new URI(System.getenv("DATABASE_URL"));

        final String username = dbUri.getUserInfo().split(":")[0];
        final String password = dbUri.getUserInfo().split(":")[1];
        final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }

    // constructor
    public DB() throws ClassNotFoundException {
        // load PostgresSQL drivers
        Class.forName("org.postgresql.Driver");
    }

    // open the connection & initialize the database
    public void open() throws SQLException, URISyntaxException {

        // saves the connection object
        connection = getConnection();

        // initialize the DB
        init();
    }

    // close the connection
    public void close() throws SQLException {
        connection.close();
    }

    // initialize the DB
    public void init() throws SQLException {

        // use module crypt
        prepareCrypto();

        // initialize the database
        createTableRoles();
        createTableUsers();
        createTablePasswordResets();
    }

    // destroy the db
    public void reset() throws SQLException {

        // drop tables
        dropTablePasswordResets();
        dropTableUsers();
        dropTableRoles();
    }

    // make sure the extension crypto is loaded
    private void prepareCrypto() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("CREATE EXTENSION IF NOT EXISTS pgcrypto;");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // create table roles & insert roles (if not exists)
    private void createTableRoles() throws SQLException {

        // create table roles
        Statement createStm = connection.createStatement();
        try {
            createStm.execute("CREATE TABLE IF NOT EXISTS roles (" +
                    "roleid CHAR(8) PRIMARY KEY," +
                    "description VARCHAR(200));");
        } finally {
            if (createStm != null) {
                createStm.close();
            }
        }

        // insert roles
        final List<Role> roles = Role.getRoles();
        final String insertRole = "INSERT INTO roles (roleid, description) VALUES (?, ?);";
        for (Role r : roles) {
            PreparedStatement insertStm = connection.prepareStatement(insertRole);
            try {
                insertStm.setString(1, r.getRoleID());
                insertStm.setString(2, r.getDescription());
                insertStm.execute();
            } catch (PSQLException e) {
                logger.warning("Role " + r.getRoleID() + " already exists... SKIP");
            } finally {
                if (insertStm != null) {
                    insertStm.close();
                }
            }
        }
    }

    // drop table roles
    private void dropTableRoles() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("DROP TABLE IF EXISTS roles;");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // create table user
    private void createTableUsers() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "uid SERIAL PRIMARY KEY," +
                    "roleid CHAR(8) REFERENCES roles(roleid)," +
                    "email VARCHAR(256) UNIQUE NOT NULL," +
                    "pass CHAR(60) NOT NULL," +
                    "first_name VARCHAR(100)," +
                    "second_name VARCHAR(100)," +
                    "credit DOUBLE PRECISION DEFAULT 0);");
            stm.execute("CREATE INDEX ON users (email);");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // drop table users
    private void dropTableUsers() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("DROP TABLE IF EXISTS users;");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // create table password resets
    private void createTablePasswordResets() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("CREATE TABLE IF NOT EXISTS resets (" +
                    "code CHAR(64) PRIMARY KEY," +
                    "uid INTEGER REFERENCES users(uid) ON DELETE CASCADE," +
                    "expiration TIMESTAMP NOT NULL);");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // drop table password resets
    private void dropTablePasswordResets() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("DROP TABLE IF EXISTS resets;");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // create user
    public void createUser(User user) throws SQLException {
        final String query = "INSERT INTO users (uid, roleid, email, pass, first_name, second_name)" +
                "VALUES (DEFAULT, ?, ?, crypt(?, gen_salt('bf')), ?, ?) RETURNING uid";
        PreparedStatement create = connection.prepareStatement(query);
        try {
            create.setString(1, user.getRole().getRoleID());
            create.setString(2, user.getEmail());
            create.setString(3, user.getPassword());
            create.setString(4, user.getFirstName());
            create.setString(5, user.getSecondName());
            ResultSet rs = create.executeQuery();
            rs.next();
            user.setId(rs.getInt("uid"));
        } finally {
            if (create != null) {
                create.close();
            }
        }
    }

    // authenticate a user
    public boolean authenticate(String email, String password) throws SQLException {
        final String query = "SELECT COUNT(email) FROM users " +
                "WHERE email = ? AND pass = crypt(?, pass);";
        PreparedStatement stm = connection.prepareStatement(query);
        boolean ok;
        try {
            stm.setString(1, email);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();
            rs.next();
            ok = (rs.getInt(1) == 1);
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return ok;
    }

    // change password
    private void changePassword(String email, String newPassword) throws SQLException, UserNotFoundException {
        final String query = "UPDATE users SET pass = crypt(?, gen_salt('bf')) WHERE email = ?";
        PreparedStatement stm = connection.prepareStatement(query);
        try {
            stm.setString(1, newPassword);
            stm.setString(2, email);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }
        } finally {
            if (stm != null) {
                stm.close();
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

    // TODO: update user?
    // update a user -> NB: does not change the password
    public void updateUser(User user) throws SQLException, UserNotFoundException, ConstrainException {
        final String query = "UPDATE users SET roleid = ?, email = ?, first_name = ?, second_name = ?, credit = ? WHERE uid = ?";
        PreparedStatement stm = connection.prepareStatement(query);
        try {
            stm.setString(1, user.getRole().getRoleID());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getFirstName());
            stm.setString(4, user.getSecondName());
            stm.setDouble(5, user.getCredit());
            stm.setInt(6, user.getId());
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }
        } catch (PSQLException e) {
            throw new ConstrainException(e);
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // delete an user
    // NB: cascade
    public void deleteUser(int id) throws SQLException, UserNotFoundException {
        final String query = "DELETE FROM users WHERE uid = ?";
        PreparedStatement stm = connection.prepareStatement(query);
        try {
            stm.setInt(1, id);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // list of users
    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        Statement stm = connection.createStatement();
        try {
            ResultSet rs = stm.executeQuery("SELECT uid, roleid, email, first_name, second_name, credit FROM users;");
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("uid"));
                u.setRole(Role.fromID(rs.getString("roleid")));
                u.setEmail(rs.getString("email"));
                u.setFirstName(rs.getString("first_name"));
                u.setSecondName(rs.getString("second_name"));
                u.setCredit(rs.getDouble("credit"));
                users.add(u);
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return users;
    }

    // get a single user
    public User getUser(String email) throws SQLException, UserNotFoundException {
        final String query = "SELECT uid, roleid, first_name, second_name, credit FROM users WHERE email = ?;";
        PreparedStatement stm = connection.prepareStatement(query);
        try {
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("uid"));
                u.setRole(Role.fromID(rs.getString("roleid")));
                u.setEmail(email);
                u.setFirstName(rs.getString("first_name"));
                u.setSecondName(rs.getString("second_name"));
                u.setCredit(rs.getDouble("credit"));
                return u;
            }
            // if here -> no such user
            throw new UserNotFoundException();
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // exists user
    private boolean existsUser(int userID) throws SQLException {
        final String query = "SELECT COUNT(uid) FROM users WHERE uid = ?;";
        PreparedStatement stm = connection.prepareStatement(query);
        boolean exists;
        try {
            stm.setInt(1, userID);
            ResultSet rs = stm.executeQuery();
            rs.next();
            exists = (rs.getInt(1) == 1);
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return exists;
    }

    // request a password recovery
    public String requestResetPassword(int userID) throws UserNotFoundException, SQLException {
        return requestResetPassword(userID, 15);
    }

    // request a password recovery
    public String requestResetPassword(int userID, int expireInMinutes) throws UserNotFoundException, SQLException {

        // check for userID
        if (!existsUser(userID)) {
            throw new UserNotFoundException();
        }

        // request password reset
        final int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            try {
                final String code = (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "");
                PreparedStatement stm = connection.prepareStatement("INSERT INTO resets (code, uid, expiration) VALUES (?, ?, ?);");
                try {
                    stm.setString(1, code);
                    stm.setInt(2, userID);
                    stm.setTimestamp(3, new Timestamp(DateTime.now().plusMinutes(expireInMinutes).toDate().getTime()));
                    stm.execute();
                } finally {
                    if (stm != null) {
                        stm.close();
                    }
                }
                return code;
            } catch (PSQLException e) {
                logger.warning("Cannot create a password recovery code for user uid: " + userID + " -> " + e.toString());
            }
        }
        throw new RuntimeException("Cannot create a password recovery code for this user: " + userID);
    }

    // check password reset
    public boolean checkPasswordReset(int userID, String code) throws SQLException {
        final String query = "SELECT expiration FROM resets WHERE code = ? AND uid = ?;";
        PreparedStatement stm = connection.prepareStatement(query);
        try {
            stm.setString(1, code);
            stm.setInt(2, userID);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Timestamp expire = rs.getTimestamp("expiration");
                return expire.after(new Date());
            } else {
                return false;
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // check password reset
    public boolean checkPasswordReset(String email, String code) throws SQLException {
        final String query = "SELECT r.expiration FROM resets r NATURAL JOIN users u WHERE r.code = ? AND u.email = ?;";
        PreparedStatement stm = connection.prepareStatement(query);
        try {
            stm.setString(1, code);
            stm.setString(2, email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Timestamp expire = rs.getTimestamp("expiration");
                return expire.after(new Date());
            } else {
                return false;
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

}
