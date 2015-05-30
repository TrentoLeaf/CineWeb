package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.exceptions.*;
import tk.trentoleaf.cineweb.model.*;

import org.apache.commons.dbcp2.BasicDataSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

public class DB {
    private final Logger logger = Logger.getLogger(DB.class.getSimpleName());

    // the connection to the database
    private BasicDataSource connectionPool;

    // return a connection pool for the database reading the environment variables
    private static BasicDataSource getConnectionPool() throws URISyntaxException {
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
    private Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    // constructor
    public DB() throws ClassNotFoundException {
        // load PostgresSQL drivers
        Class.forName("org.postgresql.Driver");
    }

    // open the connection & initialize the database
    public void open() throws SQLException, URISyntaxException {

        // saves the connection pool
        connectionPool = getConnectionPool();

        // initialize the DB
        init();
    }

    // close the connection
    public void close() throws SQLException {
        connectionPool.close();
    }

    // initialize the DB
    public void init() throws SQLException {

        // use module crypt
        prepareCrypto();

        // load citext
        prepareCitext();

        // initialize the database
        createTableRoles();
        createTableUsers();
        createTablePasswordResets();
        createTableFilms();
        createTableRooms();
        createTableSeats();
        createTablePlays();
        createTableBookings();
    }

    // destroy the db
    public void reset() throws SQLException {

        // drop tables
        dropTablePasswordResets();
        dropTableBookings();
        dropTableUsers();
        dropTableRoles();
        dropTablePlays();
        dropTableFilms();
        dropTableSeats();
        dropTableRooms();
    }

    // make sure the extension crypto is loaded
    private void prepareCrypto() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE EXTENSION IF NOT EXISTS pgcrypto;");
        }
    }

    // make sure the extension citext is loaded
    private void prepareCitext() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE EXTENSION IF NOT EXISTS citext;");
        }
    }

    // create table roles & insert roles (if not exists)
    private void createTableRoles() throws SQLException {

        // obtain a connection
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

    // drop table roles
    private void dropTableRoles() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS roles;");
        }
    }

    // create table user
    private void createTableUsers() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "uid SERIAL PRIMARY KEY," +
                    "roleid CHAR(8) REFERENCES roles(roleid)," +
                    "email CITEXT UNIQUE NOT NULL," +
                    "pass CHAR(60) NOT NULL," +
                    "first_name VARCHAR(100)," +
                    "second_name VARCHAR(100)," +
                    "credit DOUBLE PRECISION DEFAULT 0);");
            stm.execute("CREATE INDEX ON users (email);");
        }
    }

    // drop table users
    private void dropTableUsers() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS users;");
        }
    }

    // create table password resets
    private void createTablePasswordResets() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS resets (" +
                    "code CHAR(64) PRIMARY KEY," +
                    "uid INTEGER REFERENCES users(uid) ON DELETE CASCADE," +
                    "expiration TIMESTAMP NOT NULL);");
        }
    }

    // drop table password resets
    private void dropTablePasswordResets() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS resets;");
        }
    }

    // create user
    public void createUser(User user) throws SQLException, ConstrainException {
        final String query = "INSERT INTO users (uid, roleid, email, pass, first_name, second_name)" +
                "VALUES (DEFAULT, ?, lower(?), crypt(?, gen_salt('bf')), ?, ?) RETURNING uid";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, user.getRole().getRoleID());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getPassword());
            stm.setString(4, user.getFirstName());
            stm.setString(5, user.getSecondName());
            ResultSet rs = stm.executeQuery();
            rs.next();
            user.setUid(rs.getInt("uid"));
        } catch (PSQLException e) {
            throw new ConstrainException(e);
        }
    }

    // authenticate a user
    public boolean authenticate(String email, String password) throws SQLException {
        final String query = "SELECT COUNT(email) FROM users WHERE email = ? AND pass = crypt(?, pass);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, email);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1) == 1;
        }
    }

    // change password
    private void changePassword(String email, String newPassword) throws SQLException, UserNotFoundException {
        final String query = "UPDATE users SET pass = crypt(?, gen_salt('bf')) WHERE email = ?";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
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

    // update a user -> NB: does not change the password
    public void updateUser(User user) throws SQLException, UserNotFoundException, ConstrainException {
        final String query = "UPDATE users SET roleid = ?, email = ?, first_name = ?, second_name = ?, credit = ? WHERE uid = ?";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, user.getRole().getRoleID());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getFirstName());
            stm.setString(4, user.getSecondName());
            stm.setDouble(5, user.getCredit());
            stm.setInt(6, user.getUid());
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

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, id);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new UserNotFoundException();
            }
        }
    }

    // list of users
    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT uid, roleid, lower(email) AS email, first_name, second_name, credit FROM users;");

            while (rs.next()) {
                User u = new User();
                u.setUid(rs.getInt("uid"));
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

    // get a single user
    public User getUser(String email) throws SQLException, UserNotFoundException {
        final String query = "SELECT uid, roleid, first_name, second_name, credit FROM users WHERE email = ?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, email);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUid(rs.getInt("uid"));
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

    // get a single user
    public User getUser(int uid) throws SQLException, UserNotFoundException {
        final String query = "SELECT email, roleid, first_name, second_name, credit FROM users WHERE uid = ?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, uid);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUid(uid);
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

    // exists user
    private boolean existsUser(int userID) throws SQLException {
        final String query = "SELECT COUNT(uid) FROM users WHERE uid = ?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, userID);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1) == 1;
        }
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

            final String code = (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "");
            final String query = "INSERT INTO resets (code, uid, expiration) VALUES (?, ?, ?);";

            try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
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

    // check password reset
    public boolean checkPasswordReset(int userID, String code) throws SQLException {
        final String query = "SELECT expiration FROM resets WHERE code = ? AND uid = ?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, code);
            stm.setInt(2, userID);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Timestamp expire = rs.getTimestamp("expiration");
                return expire.after(new Date());
            } else {
                return false;
            }
        }
    }

    // check password reset
    public boolean checkPasswordReset(String email, String code) throws SQLException {
        final String query = "SELECT r.expiration FROM resets r NATURAL JOIN users u WHERE r.code = ? AND u.email = ?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, code);
            stm.setString(2, email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Timestamp expire = rs.getTimestamp("expiration");
                return expire.after(new Date());
            } else {
                return false;
            }
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

    // drop table films
    private void dropTableFilms() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS films;");
        }
    }

    // insert a new film
    public void createFilm(Film film) throws SQLException {
        final String query = "INSERT INTO films (fid, title, genre, trailer, playbill, plot, duration) VALUES " +
                "(DEFAULT, ?, ?, ?, ?, ?, ?) RETURNING fid";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, film.getTitle());
            stm.setString(2, film.getGenre());
            stm.setString(3, film.getTrailer());
            stm.setString(4, film.getPlaybill());
            stm.setString(5, film.getPlot());
            stm.setInt(6, film.getDuration());
            ResultSet rs = stm.executeQuery();
            rs.next();
            film.setId(rs.getInt("fid"));
        }
    }

    // list of users
    public List<Film> getFilms() throws SQLException {
        List<Film> films = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT fid, title, genre, trailer, playbill, plot, duration FROM films;");

            while (rs.next()) {
                Film f = new Film();
                f.setId(rs.getInt("fid"));
                f.setTitle(rs.getString("title"));
                f.setGenre(rs.getString("genre"));
                f.setTrailer(rs.getString("trailer"));
                f.setPlaybill(rs.getString("playbill"));
                f.setPlot(rs.getString("plot"));
                f.setDuration(rs.getInt("duration"));
                films.add(f);
            }
        }

        return films;
    }

    // edit film
    public void updateFilm(Film film) throws SQLException, EntryNotFoundException {
        final String query = "UPDATE films SET title = ?, genre = ?, trailer = ?, playbill = ?, plot = ?, duration = ? WHERE fid = ?";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, film.getTitle());
            stm.setString(2, film.getGenre());
            stm.setString(3, film.getTrailer());
            stm.setString(4, film.getPlaybill());
            stm.setString(5, film.getPlot());
            stm.setInt(6, film.getDuration());
            stm.setInt(7, film.getId());

            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }
        }
    }

    // delete film
    public void deleteFilm(Film film) throws SQLException, EntryNotFoundException {
        deleteFilm(film.getId());
    }

    // delete film
    public void deleteFilm(int id) throws SQLException, EntryNotFoundException {
        final String query = "DELETE FROM films WHERE fid = ?";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, id);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }
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

    // drop table rooms
    private void dropTableRooms() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS rooms;");
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

    // drop table seats
    private void dropTableSeats() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS seats;");
        }
    }

    // create a Room with all the seats
    public Room createRoom(int rows, int cols) throws SQLException {
        return createRoom(rows, cols, new ArrayList<Seat>());
    }

    // create a new Room
    public Room createRoom(int rows, int cols, List<Seat> missingSeats) throws SQLException {
        try (Connection connection = getConnection()) {

            // create a transaction to ensure DB consistency
            connection.setAutoCommit(false);

            // query
            final String insertRoom = "INSERT INTO rooms (rid, rows, cols) VALUES (DEFAULT, ?, ?) RETURNING rid;";

            // query to create a new room
            try (PreparedStatement roomStm = connection.prepareStatement(insertRoom)) {

                roomStm.setInt(1, rows);
                roomStm.setInt(2, cols);

                ResultSet rs = roomStm.executeQuery();
                rs.next();

                final int rid = rs.getInt("rid");
                final Room room = new Room(rid, rows, cols);

                // sort seats
                Collections.sort(missingSeats);

                // iterator
                Iterator<Seat> iterator = missingSeats.iterator();

                Seat current = null;
                if (iterator.hasNext()) {
                    current = iterator.next();
                }

                // insert seats
                for (int x = 0; x < rows; x++) {
                    for (int y = 0; y < cols; y++) {

                        // if reached the current seat
                        if (current != null && current.getX() == x && current.getY() == y) {

                            // retrieve next seat and skip the insertion
                            if (iterator.hasNext()) {
                                current = iterator.next();
                            } else {
                                current = null;
                            }
                        }

                        // this seat exists
                        else {

                            // insert this seat
                            final String insertSeat = "INSERT INTO seats (rid, x, y) VALUES (?, ?, ?);";

                            try (PreparedStatement seatsStm = connection.prepareStatement(insertSeat)) {
                                seatsStm.setInt(1, rid);
                                seatsStm.setInt(2, x);
                                seatsStm.setInt(3, y);

                                seatsStm.execute();

                                // add to room
                                room.addSeats(new Seat(rid, x, y));
                            }
                        }
                    }
                }

                // execute sql
                connection.commit();

                // return room
                return room;

            } catch (SQLException e) {

                // if errors -> rollback
                connection.rollback();

                // throw the exception
                throw e;
            }
        }
    }

    // get the existing seats for a given room
    public List<Seat> getSeatsByRoom(int rid) throws SQLException {
        final List<Seat> seats = new ArrayList<>();
        final String query = "SELECT x, y FROM seats WHERE rid = ?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, rid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                seats.add(new Seat(rid, x, y));
            }
        }

        return seats;
    }

    // get the room list
    public List<Room> getRooms(boolean withPlaces) throws SQLException {
        List<Room> rooms = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT rid, rows, cols FROM rooms;");

            while (rs.next()) {
                Room r = new Room();
                r.setRid(rs.getInt("rid"));
                r.setRows(rs.getInt("rows"));
                r.setColumns(rs.getInt("cols"));
                r.setSeats(withPlaces ? getSeatsByRoom(r.getRid()) : new ArrayList<Seat>());
                rooms.add(r);
            }
        }

        return rooms;
    }

    // delete room
    // NB: throw an exception if the room is referenced in any table
    public void deleteRoom(int rid) throws SQLException, EntryNotFoundException {
        try (Connection connection = getConnection()) {

            // create a transaction to ensure DB consistency
            connection.setAutoCommit(false);

            // delete seats for this room
            final String seatsQuery = "DELETE FROM seats WHERE rid = ?;";

            try (PreparedStatement seatsStm = connection.prepareStatement(seatsQuery)) {

                seatsStm.setInt(1, rid);
                seatsStm.execute();

                // now remove the room
                final String roomQuery = "DELETE FROM rooms WHERE rid = ?;";

                try (PreparedStatement roomStm = connection.prepareStatement(roomQuery)) {
                    roomStm.setInt(1, rid);
                    int rows = roomStm.executeUpdate();

                    if (rows != 1) {
                        throw new EntryNotFoundException();
                    }
                }

                // execute sql
                connection.commit();

            } catch (SQLException | EntryNotFoundException e) {

                // if error -> rollback
                connection.rollback();

                // throw the exception
                throw e;
            }
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

    // drop table plays
    private void dropTablePlays() throws SQLException {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            stm.execute("DROP TABLE IF EXISTS plays;");
        }
    }

    // create play
    public void createPlay(Play play) throws SQLException, AnotherFilmScheduledException {

        boolean another = isAlreadyPlay(play.getRid(), play.getTime());
        if (another) {
            throw new AnotherFilmScheduledException();
        }

        final String query = "INSERT INTO plays (pid, fid, rid, time, _3d) " +
                "VALUES (DEFAULT, ?, ?, ?, ?) RETURNING pid;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, play.getFid());
            stm.setInt(2, play.getRid());
            stm.setTimestamp(3, new Timestamp(play.getTime().toDate().getTime()));
            stm.setBoolean(4, play.is_3d());
            ResultSet rs = stm.executeQuery();

            // if here -> no error
            rs.next();
            play.setPid(rs.getInt("pid"));
        }
    }

    // check play
    public boolean isAlreadyPlay(Room room, DateTime time) throws SQLException {
        return isAlreadyPlay(room.getRid(), time);
    }

    // check play
    public boolean isAlreadyPlay(int rid, DateTime time) throws SQLException {
        final String query = "SELECT COUNT(*) FROM films f NATURAL JOIN plays p " +
                "WHERE p.rid = ? AND p.time <= ? AND ? <= p.time + (f.duration * INTERVAL '1 minute');";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            final Timestamp timestamp = new Timestamp(time.toDate().getTime());

            stm.setInt(1, rid);
            stm.setTimestamp(2, timestamp);
            stm.setTimestamp(3, timestamp);

            ResultSet rs = stm.executeQuery();
            rs.next();

            return rs.getInt(1) >= 1;
        }
    }

    // check if is older play
    public boolean isOlderPlay(int pid, DateTime time) throws SQLException {

        boolean isOlderPlay=false;
        final String query = "SELECT time FROM plays " +
                "WHERE pid = ? ;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {

            stm.setInt(1, pid);

            ResultSet rs = stm.executeQuery();
            rs.next();



            if(rs.getTimestamp(1).before(new Timestamp(time.toDate().getTime())))
            {
                isOlderPlay = true;
            }
        }
        return isOlderPlay;
    }

    // get list of plays
    public List<Play> getPlays() throws SQLException {
        final List<Play> plays = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT pid, fid, rid, time, _3d FROM plays;");

            while (rs.next()) {
                int pid = rs.getInt("pid");
                int fid = rs.getInt("fid");
                int rid = rs.getInt("rid");
                DateTime time = new DateTime(rs.getTimestamp("time").getTime());
                boolean _3d = rs.getBoolean("_3d");
                plays.add(new Play(pid, fid, rid, time, _3d));
            }
        }

        return plays;
    }

    // delete a play
    public void deletePlay(Play play) throws SQLException, EntryNotFoundException {
        deletePlay(play.getPid());
    }

    // delete a play
    public void deletePlay(int pid) throws SQLException, EntryNotFoundException {
        final String query = "DELETE FROM plays WHERE pid = ?";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, pid);

            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }
        }
    }


    // create table books
    private void createTableBookings() throws SQLException
    {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement())
        {
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
                    "FOREIGN KEY (rid,x,y) REFERENCES seats(rid,x,y)," +
                    "FOREIGN KEY (uid) REFERENCES users(uid)," +
                    "FOREIGN KEY (pid) REFERENCES plays(pid));");
            //stm.execute("CREATE INDEX ON bookings (pid);");
        }
    }

    // drop table booking
    private void dropTableBookings() throws SQLException
    {
        try (Connection connection = getConnection(); Statement stm = connection.createStatement())
        {
            stm.execute("DROP TABLE IF EXISTS bookings;");
        }
    }

    public Booking createBookings(int rid, int x, int y, int uid, int pid, double price)  throws SQLException, FilmAlreadyGoneException
    {
        final String query = "INSERT INTO bookings (bid, uid, pid, rid, x, y, time_booking, price) VALUES " +
                "(DEFAULT, ?, ?, ?, ?, ?, ?, ?) RETURNING bid";


        final DateTime timeBooking = new DateTime(System.currentTimeMillis());
        if(isOlderPlay( pid,  timeBooking))
        {
            throw  new FilmAlreadyGoneException();
        }

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query))
        {

            final Booking booking = new Booking(rid, x, y, uid, pid, timeBooking, price);

            stm.setInt(1, booking.getUid());
            stm.setInt(2, booking.getPid());
            stm.setInt(3, booking.getRid());
            stm.setInt(4, booking.getX());
            stm.setInt(5, booking.getY());
            stm.setTimestamp(6, new Timestamp(booking.getTimeBooking().toDate().getTime()));
            stm.setDouble(7, price);

            ResultSet rs = stm.executeQuery();
            rs.next();

            int bid = rs.getInt("bid");
            booking.setBid(bid);
            return booking;
        }
    }

    //list of all booking
    public List<Booking> getBookings() throws SQLException {
        final List<Booking> bookings = new ArrayList<>();

        try (Connection connection = getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT bid, uid, pid, rid, x, y, time_booking, price FROM bookings;");

            while (rs.next()) {
                int bid = rs.getInt("bid");
                int uid = rs.getInt("uid");
                int pid = rs.getInt("pid");
                int rid = rs.getInt("rid");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                DateTime timeBooking = new DateTime(rs.getTimestamp("time_booking").getTime());
                double price = rs.getDouble("price");
                bookings.add(new Booking(bid, rid, x, y, uid, pid, timeBooking, price));
            }
        }

        return bookings;
    }

    public Booking getBooking(int bookingId) throws SQLException {
        final Booking booking;
        final String query = "SELECT bid, uid, pid, rid, x, y, time_booking, price FROM bookings WHERE bid = ?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, bookingId);
            ResultSet rs = stm.executeQuery();

            rs.next();
            bookingId = rs.getInt("bid");
            int uid = rs.getInt("uid");
            int pid = rs.getInt("pid");
            int rid = rs.getInt("rid");
            int x = rs.getInt("x");
            int y = rs.getInt("y");
            DateTime timeBooking = new DateTime(rs.getTimestamp("time_booking").getTime());
            double price = rs.getDouble("price");

            booking = new Booking( bookingId, rid, x, y, uid, pid, timeBooking, price);
        }

        return booking;
    }

    //delete booking
    public void deleteBooking(Booking booking) throws SQLException, UserNotFoundException {

        final String query = "DELETE FROM bookings WHERE bid = ?";
        final User user;

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement stm = connection.prepareStatement(query)) {
                stm.setInt(1, booking.getBid());
                ResultSet rs = stm.executeQuery();
                rs.next();

                double accredit = booking.getPrice() * 0.80;
                user = getUser(booking.getUid());
                user.addCredit(accredit);

                final String queryUser = "UPDATE users SET credit = ? WHERE uid = ?";

                try (PreparedStatement stmUser = connection.prepareStatement(queryUser)) {
                    stmUser.setDouble(1, user.getCredit());
                    stmUser.execute();
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }

    }

    // delete a Booking
    public void deleteBooking(int bid) throws SQLException, UserNotFoundException {
        deleteBooking(getBooking(bid));
    }

}

