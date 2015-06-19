package tk.trentoleaf.cineweb.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.model.Role;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is the base class for the Database. It handles the Connections Pool, load the needed Postgres
 * module, create the tables and indexes, drop tables (used for tests).
 */
public class DB {
    private final Logger logger = Logger.getLogger(DB.class.getSimpleName());

    // instance -> singleton pattern
    private static DB instance;

    // instance -> singleton pattern
    public static DB instance() {
        if (instance == null) {
            instance = new DB();
        }
        return instance;
    }

    // constructor -> load the drivers
    // singleton pattern
    private DB() {
        try {
            // load PostgresSQL drivers
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // in case of error -> throw a runtime exception
            throw new RuntimeException(e);
        }
    }

    // the connection pool to the database
    private BasicDataSource connectionPool;

    // return a connection pool for the database reading the environment variables
    private BasicDataSource getConnectionPool() throws URISyntaxException {
        final URI dbUri = new URI(System.getenv("DATABASE_URL"));

        final String username = dbUri.getUserInfo().split(":")[0];
        final String password = dbUri.getUserInfo().split(":")[1];
        final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath() + "?stringtype=unspecified";

        final BasicDataSource connectionPool = new BasicDataSource();
        connectionPool.setUsername(username);
        connectionPool.setPassword(password);
        connectionPool.setDriverClassName("org.postgresql.Driver");
        connectionPool.setUrl(dbUrl);
        connectionPool.setInitialSize(1);
        connectionPool.setMaxTotal(16);

        return connectionPool;
    }

    // return a connection object from the pool of available connections
    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    // open the connection & initialize the database
    public final void open() throws SQLException, URISyntaxException {

        // saves the connection pool
        if (connectionPool == null) {
            connectionPool = getConnectionPool();
        }

        // initialize the TodoDB
        init();
    }

    // close the connection
    public final void close() throws SQLException {
        if (connectionPool != null) {
            connectionPool.close();
            connectionPool = null;
        }
    }

    // initialize the TodoDB -> create all the tables if not exist
    public final void init() throws SQLException {

        // load module crypt
        prepareCrypto();

        // load module citext
        prepareCitext();

        // initialize the database
        createTableRoles();
        createTableUsers();
        createTableRegistrationCodes();
        createTablePasswordResets();
        createTableRooms();
        createTableSeats();
        createTableFilms();
        createTablePlays();
        createTableBookings();
    }

    // destroy the TodoDB -> useful for Unit tests
    public void reset() throws SQLException {

        // drop tables
        dropTableBookings();
        dropTablePlays();
        dropTableFilms();
        dropTableSeats();
        dropTableRooms();
        dropTablePasswordResets();
        dropTableRegistrationCodes();
        dropTableUsers();
        dropTableRoles();

        // unload module citext
        removeCitext();

        // unload module crypto
        removeCrypto();
    }

    // make sure the extension crypto is loaded
    // used to secure store passwords
    private void prepareCrypto() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE EXTENSION IF NOT EXISTS pgcrypto;");
        }
    }

    // make sure the extension citext is loaded
    // citext = text case insensitive -> used for emails
    private void prepareCitext() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE EXTENSION IF NOT EXISTS citext;");
        }
    }

    // unload extension citext
    private void removeCrypto() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP EXTENSION IF EXISTS pgcrypto;");
        }
    }

    // unload extension citext
    private void removeCitext() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP EXTENSION IF EXISTS citext;");
        }
    }

    // create table roles & insert roles (if not exist already)
    private void createTableRoles() throws SQLException {
        try (Connection connection = getConnection()) {

            // create table roles
            try (Statement createStm = connection.createStatement()) {
                createStm.execute("CREATE TABLE IF NOT EXISTS roles (" +
                        "roleid CHAR(8) PRIMARY KEY," +
                        "description VARCHAR(200));");
            }

            // insert roles
            final List<Role> roles = Role.getRoles();
            final String insertRole = "INSERT INTO roles (roleid, description) VALUES (?, ?);";
            for (Role r : roles) {
                try (PreparedStatement insertStm = connection.prepareStatement(insertRole)) {
                    insertStm.setString(1, r.getRoleID());
                    insertStm.setString(2, r.getDescription());
                    insertStm.execute();
                } catch (PSQLException e) {
                    logger.warning("Role " + r.getRoleID() + " already exists... SKIP");
                }
            }
        }
    }

    // create table user
    private void createTableUsers() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "uid SERIAL PRIMARY KEY," +
                    "enabled BOOLEAN," +
                    "roleid CHAR(8) REFERENCES roles(roleid)," +
                    "email CITEXT UNIQUE NOT NULL," +
                    "pass CHAR(60) NOT NULL," +
                    "first_name VARCHAR(100)," +
                    "second_name VARCHAR(100)," +
                    "credit DOUBLE PRECISION DEFAULT 0);");
            stm.execute("CREATE INDEX ON users (email);");
            stm.execute("CREATE INDEX ON users (uid, roleid);");
        }
    }

    // create table registration codes
    private void createTableRegistrationCodes() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS registration_codes (" +
                    "uid INTEGER," +
                    "code CHAR(64)," +
                    "PRIMARY KEY(uid)," +
                    "FOREIGN KEY(uid) REFERENCES users(uid) ON DELETE CASCADE);");
        }
    }

    // create table password resets
    private void createTablePasswordResets() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS resets (" +
                    "code CHAR(64)," +
                    "uid INTEGER," +
                    "expiration TIMESTAMP NOT NULL," +
                    "PRIMARY KEY(code)," +
                    "FOREIGN KEY(uid) REFERENCES users(uid) ON DELETE CASCADE);");
        }
    }

    // create table rooms
    private void createTableRooms() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                    "rid SERIAL PRIMARY KEY," +
                    "rows INTEGER NOT NULL," +
                    "cols INTEGER NOT NULL);");
        }
    }

    // create table seats
    private void createTableSeats() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS seats (" +
                    "rid INTEGER," +
                    "x INTEGER," +
                    "y INTEGER," +
                    "PRIMARY KEY (rid, x, y)," +
                    "FOREIGN KEY (rid) REFERENCES rooms(rid) ON DELETE CASCADE)");
        }
    }

    // create table films
    private void createTableFilms() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS films (" +
                    "fid SERIAL," +
                    "title VARCHAR(100) NOT NULL," +
                    "genre VARCHAR(20)," +
                    "trailer VARCHAR(100)," +
                    "playbill VARCHAR(100)," +
                    "plot TEXT," +
                    "duration INTEGER," +
                    "PRIMARY KEY (fid));");
        }
    }

    // create table plays
    public void createTablePlays() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS plays (" +
                    "pid SERIAL," +
                    "fid INTEGER," +
                    "rid INTEGER," +
                    "time TIMESTAMP NOT NULL," +
                    "_3d BOOLEAN NOT NULL," +
                    "PRIMARY KEY (pid)," +
                    "FOREIGN KEY (fid) REFERENCES films(fid)," +
                    "FOREIGN KEY (rid) REFERENCES rooms(rid));");
        }
    }

    // create table books
    private void createTableBookings() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                    "bid SERIAL," +
                    "uid INTEGER," +
                    "pid INTEGER," +
                    "rid INTEGER," +
                    "x INTEGER," +
                    "y INTEGER," +
                    "time_booking TIMESTAMP NOT NULL," +
                    "price DOUBLE PRECISION," +
                    "PRIMARY KEY (bid)," +
                    "FOREIGN KEY (rid, x, y) REFERENCES seats(rid, x, y)," +
                    "FOREIGN KEY (uid) REFERENCES users(uid)," +
                    "FOREIGN KEY (pid) REFERENCES plays(pid));");
        }
    }

    // drop table roles
    private void dropTableRoles() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS roles;");
        }
    }

    // drop table users
    private void dropTableUsers() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS users;");
        }
    }

    // drop table password resets
    private void dropTableRegistrationCodes() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS registration_codes;");
        }
    }

    // drop table password resets
    private void dropTablePasswordResets() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS resets;");
        }
    }

    // drop table rooms
    private void dropTableRooms() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS rooms;");
        }
    }

    // drop table seats
    private void dropTableSeats() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS seats;");
        }
    }

    // drop table films
    private void dropTableFilms() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS films;");
        }
    }

    // drop table plays
    private void dropTablePlays() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS plays;");
        }
    }

    // drop table booking
    private void dropTableBookings() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS bookings;");
        }
    }

}
