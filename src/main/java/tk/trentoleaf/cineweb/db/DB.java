package tk.trentoleaf.cineweb.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.beans.model.Role;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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

        // initialize the DB -> create tables, indexes if not exists
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
        createTableTickets();
        createTableDeletedTickets();
        createTablePrices();
    }

    // destroy the TodoDB -> useful for Unit tests
    public void reset() throws SQLException {

        // drop tables
        dropTablePrices();
        dropTableDeletedTickets();
        dropTableTickets();
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
                        "roleid CITEXT PRIMARY KEY," +
                        "description TEXT NOT NULL);");
            }

            // insert roles
            final String insertRole = "INSERT INTO roles (roleid, description) VALUES (?, ?);";
            for (Role r : Role.values()) {
                try (PreparedStatement insertStm = connection.prepareStatement(insertRole)) {
                    insertStm.setString(1, r.getRoleID());
                    insertStm.setString(2, r.getDescription());
                    insertStm.execute();
                } catch (PSQLException e) {
                    logger.info("Role " + r.getRoleID() + " already exists... SKIP");
                }
            }
        }
    }

    // create table user
    private void createTableUsers() throws SQLException {
        try (Connection connection = getConnection()) {
            try (Statement stm = connection.createStatement()) {
                stm.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "uid SERIAL," +
                        "enabled BOOLEAN NOT NULL," +
                        "roleid CITEXT NOT NULL," +
                        "email CITEXT NOT NULL," +
                        "pass VARCHAR(60) NOT NULL," +
                        "first_name VARCHAR(100) NOT NULL," +
                        "second_name VARCHAR(100) NOT NULL," +
                        "credit DOUBLE PRECISION NOT NULL DEFAULT 0," +
                        "PRIMARY KEY(uid)," +
                        "FOREIGN KEY(roleid) REFERENCES roles(roleid)," +
                        "UNIQUE(email));");
            }
            // index on email already exists -> unique constraint
            try (Statement stm = connection.createStatement()) {
                stm.execute("CREATE INDEX users_role ON users (uid, roleid);");
            } catch (SQLException e) {
                // do nothing -> index already exists
                logger.info("Cannot create index users_role -> already exists");
            }
        }
    }

    // create table registration codes
    private void createTableRegistrationCodes() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS registration_codes (" +
                    "uid INTEGER," +
                    "code CHAR(64) NOT NULL," +
                    "PRIMARY KEY(uid)," +
                    "FOREIGN KEY(uid) REFERENCES users(uid) ON DELETE CASCADE);");
        }
    }

    // create table password resets
    private void createTablePasswordResets() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS resets (" +
                    "code CHAR(64)," +
                    "uid INTEGER NOT NULL," +
                    "expiration TIMESTAMP NOT NULL," +
                    "PRIMARY KEY(code)," +
                    "FOREIGN KEY(uid) REFERENCES users(uid) ON DELETE CASCADE);");
        }
    }

    // create table rooms
    private void createTableRooms() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                    "rid SERIAL," +
                    "rows INTEGER NOT NULL," +
                    "cols INTEGER NOT NULL," +
                    "PRIMARY KEY(rid));");
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
                    "title TEXT NOT NULL," +
                    "genre VARCHAR(100) NOT NULL," +
                    "trailer TEXT NOT NULL," +
                    "playbill TEXT NOT NULL," +
                    "plot TEXT NOT NULL," +
                    "duration INTEGER NOT NULL," +
                    "PRIMARY KEY (fid));");
        }
    }

    // create table plays
    public void createTablePlays() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS plays (" +
                    "pid SERIAL," +
                    "fid INTEGER NOT NULL," +
                    "rid INTEGER NOT NULL," +
                    "time TIMESTAMP NOT NULL," +
                    "_3d BOOLEAN NOT NULL," +
                    "PRIMARY KEY (pid)," +
                    "FOREIGN KEY (fid) REFERENCES films(fid)," +
                    "FOREIGN KEY (rid) REFERENCES rooms(rid)," +
                    "UNIQUE (pid, rid));");
        }
    }

    // create table bookings
    private void createTableBookings() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                    "bid SERIAL," +
                    "uid INTEGER NOT NULL," +
                    "booking_time TIMESTAMP NOT NULL," +
                    "payed_with_credit DOUBLE PRECISION NOT NULL," +
                    "PRIMARY KEY (bid)," +
                    "FOREIGN KEY (uid) REFERENCES users(uid) ON DELETE RESTRICT);");
        }
    }

    // create table tickets
    private void createTableTickets() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS tickets (" +
                    "tid SERIAL," +
                    "bid INTEGER NOT NULL," +
                    "pid INTEGER NOT NULL," +
                    "rid INTEGER NOT NULL," +
                    "x INTEGER NOT NULL," +
                    "y INTEGER NOT NULL," +
                    "price DOUBLE PRECISION NOT NULL," +
                    "type TEXT NOT NULL," +
                    "PRIMARY KEY (tid)," +
                    "FOREIGN KEY (bid) REFERENCES bookings(bid)," +
                    "FOREIGN KEY (pid) REFERENCES plays(pid), " +
                    "FOREIGN KEY (pid, rid) REFERENCES plays(pid, rid)," +
                    "FOREIGN KEY (rid, x, y) REFERENCES seats(rid, x, y)," +
                    "UNIQUE(pid, rid, x, y));");
        }
    }

    // create table deleted tickets
    private void createTableDeletedTickets() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS deleted_tickets (" +
                    "tid INTEGER NOT NULL," +
                    "bid INTEGER NOT NULL," +
                    "pid INTEGER NOT NULL," +
                    "rid INTEGER NOT NULL," +
                    "x INTEGER NOT NULL," +
                    "y INTEGER NOT NULL," +
                    "price DOUBLE PRECISION NOT NULL," +
                    "type TEXT NOT NULL);");
        }
    }

    // create table prices
    private void createTablePrices() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS prices (" +
                    "type CITEXT PRIMARY KEY," +
                    "price DOUBLE PRECISION NOT NULL);");
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

    // drop table tickets
    private void dropTableTickets() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS tickets;");
        }
    }

    // drop table deleted tickets
    private void dropTableDeletedTickets() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS deleted_tickets;");
        }
    }

    // drop table prices
    private void dropTablePrices() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS prices;");
        }
    }

}
