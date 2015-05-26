package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.exceptions.ConstrainException;
import tk.trentoleaf.cineweb.exceptions.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.UserNotFoundException;
import tk.trentoleaf.cineweb.exceptions.WrongCodeException;
import tk.trentoleaf.cineweb.exceptions.WrongPasswordException;
import tk.trentoleaf.cineweb.model.*;

import javax.ws.rs.POST;
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
    private Connection tConnection;

    // return a connection object reading the environment variables
    private static Connection getConnection() throws URISyntaxException, SQLException {
        final URI dbUri = new URI(System.getenv("DATABASE_URL"));

        final String username = dbUri.getUserInfo().split(":")[0];
        final String password = dbUri.getUserInfo().split(":")[1];
        final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath() + "?stringtype=unspecified";

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
        tConnection = getConnection();

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
    }

    // destroy the db
    public void reset() throws SQLException {

        // drop tables
        dropTablePasswordResets();
        dropTableUsers();
        dropTableRoles();
        dropTablePlays();
        dropTableFilms();
        dropTableSeats();
        dropTableRooms();
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

    // make sure the extension citext is loaded
    private void prepareCitext() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("CREATE EXTENSION IF NOT EXISTS citext;");
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
                    "email CITEXT UNIQUE NOT NULL," +
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
    public void createUser(User user) throws SQLException, ConstrainException {
        final String query = "INSERT INTO users (uid, roleid, email, pass, first_name, second_name)" +
                "VALUES (DEFAULT, ?, lower(?), crypt(?, gen_salt('bf')), ?, ?) RETURNING uid";
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
        } catch (PSQLException e) {
            throw new ConstrainException(e);
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
            ResultSet rs = stm.executeQuery("SELECT uid, roleid, lower(email) AS email, first_name, second_name, credit FROM users;");
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

    // create table films
    private void createTableFilms() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("CREATE TABLE IF NOT EXISTS films (" +
                    "fid SERIAL PRIMARY KEY," +
                    "title VARCHAR(100) NOT NULL," +
                    "genre VARCHAR(20)," +
                    "trailer VARCHAR(100)," +
                    "playbill VARCHAR(100)," +
                    "plot TEXT," +
                    "duration INTEGER);");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // drop table films
    private void dropTableFilms() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("DROP TABLE IF EXISTS films;");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // insert a new film
    public void insertFilm(Film film) throws SQLException {
        final String query = "INSERT INTO films (fid, title, genre, trailer, playbill, plot, duration) VALUES " +
                "(DEFAULT, ?, ?, ?, ?, ?, ?) RETURNING fid";
        PreparedStatement stm = connection.prepareStatement(query);
        try {
            stm.setString(1, film.getTitle());
            stm.setString(2, film.getGenre());
            stm.setString(3, film.getTrailer());
            stm.setString(4, film.getPlaybill());
            stm.setString(5, film.getPlot());
            stm.setInt(6, film.getDuration());
            ResultSet rs = stm.executeQuery();
            rs.next();
            film.setId(rs.getInt("fid"));
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // list of users
    public List<Film> getFilms() throws SQLException {
        List<Film> films = new ArrayList<>();
        Statement stm = connection.createStatement();
        try {
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
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return films;
    }

    // edit film
    public void updateFilm(Film film) throws SQLException, EntryNotFoundException {
        final String query = "UPDATE films SET title = ?, genre = ?, trailer = ?, playbill = ?, plot = ?, duration = ? WHERE fid = ?";
        PreparedStatement stm = connection.prepareStatement(query);
        try {
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
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // delete film
    public void deleteFilm(int id) throws SQLException, EntryNotFoundException {
        final String query = "DELETE FROM films WHERE fid = ?";
        PreparedStatement stm = connection.prepareStatement(query);
        try {
            stm.setInt(1, id);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // create table rooms
    private void createTableRooms() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                    "rid SERIAL PRIMARY KEY," +
                    "rows INTEGER NOT NULL," +
                    "cols INTEGER NOT NULL);");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // drop table rooms
    private void dropTableRooms() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("DROP TABLE IF EXISTS rooms;");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // create table seats
    private void createTableSeats() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("CREATE TABLE IF NOT EXISTS seats (" +
                    "rid INTEGER," +
                    "x INTEGER," +
                    "y INTEGER," +
                    "PRIMARY KEY (rid, x, y)," +
                    "FOREIGN KEY (rid) REFERENCES rooms(rid) ON DELETE CASCADE)");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // drop table seats
    private void dropTableSeats() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("DROP TABLE IF EXISTS seats;");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // create a Room with all the seats
    public Room createRoom(int rows, int cols) throws SQLException {
        return createRoom(rows, cols, new ArrayList<Seat>());
    }

    // create a new Room
    public Room createRoom(int rows, int cols, List<Seat> missingSeats) throws SQLException {

        // create a transaction to ensure DB consistency
        tConnection.setAutoCommit(false);

        // room object
        final Room room;

        // query to create a new room
        PreparedStatement roomStm = tConnection.prepareStatement("INSERT INTO rooms (rid, rows, cols) " +
                "VALUES (DEFAULT, ?, ?) RETURNING rid;");

        try {
            roomStm.setInt(1, rows);
            roomStm.setInt(2, cols);

            ResultSet rs = roomStm.executeQuery();
            rs.next();

            final int rid = rs.getInt("rid");
            room = new Room(rid, rows, cols);

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
                        PreparedStatement seatsStm = tConnection.prepareStatement("INSERT INTO seats (rid, x, y) VALUES (?, ?, ?);");

                        try {
                            seatsStm.setInt(1, rid);
                            seatsStm.setInt(2, x);
                            seatsStm.setInt(3, y);
                            seatsStm.execute();

                            // add to room
                            room.addSeats(new Seat(rid, x, y));

                        } finally {
                            if (seatsStm != null) {
                                seatsStm.close();
                            }
                        }
                    }

                }
            }

            // execute sql
            tConnection.commit();

        } catch (SQLException e) {
            tConnection.rollback();
            throw e;
        } finally {
            if (roomStm != null) {
                roomStm.close();
            }
        }

        return room;
    }

    // get the existing seats for a given room
    public List<Seat> getSeatsByRoom(int rid) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        PreparedStatement stm = connection.prepareStatement("SELECT x, y FROM seats WHERE rid = ?;");
        try {
            stm.setInt(1, rid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                seats.add(new Seat(rid, x, y));
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return seats;
    }

    // get the room list
    public List<Room> getRooms(boolean withPlaces) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        Statement stm = connection.createStatement();
        try {
            ResultSet rs = stm.executeQuery("SELECT rid, rows, cols FROM rooms;");
            while (rs.next()) {
                Room r = new Room();
                r.setRid(rs.getInt("rid"));
                r.setRows(rs.getInt("rows"));
                r.setColumns(rs.getInt("cols"));
                if (withPlaces) {
                    r.setSeats(getSeatsByRoom(r.getRid()));
                } else {
                    r.setSeats(new ArrayList<Seat>());
                }
                rooms.add(r);
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return rooms;
    }

    // delete room
    // NB: throw an exception if the room is referenced in any table
    public void deleteRoom(int rid) throws SQLException {

        // create a transaction to ensure DB consistency
        tConnection.setAutoCommit(false);

        // delete seats for this room
        final String seatsQuery = "DELETE FROM seats WHERE rid = ?;";
        PreparedStatement seatsStm = tConnection.prepareStatement(seatsQuery);

        try {
            seatsStm.setInt(1, rid);
            seatsStm.execute();

            // now remove the room
            final String roomQuery = "DELETE FROM rooms WHERE rid = ?;";
            PreparedStatement roomStm = tConnection.prepareStatement(roomQuery);

            try {
                roomStm.setInt(1, rid);
                roomStm.execute();
            } finally {
                if (roomStm != null) {
                    roomStm.close();
                }
            }

            // execute sql
            tConnection.commit();

        } catch (SQLException e) {
            tConnection.rollback();
            throw e;
        } finally {
            if (seatsStm != null) {
                seatsStm.close();
            }
        }
    }

    // create table plays
    public void createTablePlays() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("CREATE TABLE IF NOT EXISTS plays (" +
                    "pid SERIAL," +
                    "fid INTEGER," +
                    "rid INTEGER," +
                    "time TIMESTAMP NOT NULL," +
                    "_3d BOOLEAN NOT NULL," +
                    "PRIMARY KEY (pid)," +
                    "FOREIGN KEY (fid) REFERENCES films(fid)," +
                    "FOREIGN KEY (rid) REFERENCES rooms(rid));");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // drop table plays
    private void dropTablePlays() throws SQLException {
        Statement stm = connection.createStatement();
        try {
            stm.execute("DROP TABLE IF EXISTS plays;");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // create play
    public void createPlay(Play play) throws SQLException {

        // TODO: check if a film is already playing at this time in this room

        final String query = "INSERT INTO plays (pid, fid, rid, time, _3d) " +
                "VALUES (DEFAULT, ?, ?, ?, ?) RETURNING pid;";
        PreparedStatement stm = connection.prepareStatement(query);

        try {
            stm.setInt(1, play.getFid());
            stm.setInt(2, play.getRid());
            stm.setTimestamp(3, new Timestamp(play.getTime().toDate().getTime()));
            stm.setBoolean(4, play.is_3d());
            ResultSet rs = stm.executeQuery();

            // if here -> no error
            rs.next();
            play.setPid(rs.getInt("pid"));

        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // check play
    public boolean isAlreadyPlay(Room room, DateTime time) throws SQLException {
        return isAlreadyPlay(room.getRid(), time);
    }

    // check play
    public boolean isAlreadyPlay(int rid, DateTime time) throws SQLException {
        PreparedStatement stm = connection.prepareStatement("SELECT COUNT(*) FROM films f NATURAL JOIN plays p " +
                "WHERE p.rid = ? AND p.time <= ? AND ? <= p.time + (f.duration * INTERVAL '1 minute');");
        try {
            final Timestamp timestamp = new Timestamp(time.toDate().getTime());

            stm.setInt(1, rid);
            stm.setTimestamp(2, timestamp);
            stm.setTimestamp(3, timestamp);

            ResultSet rs = stm.executeQuery();
            rs.next();

            return rs.getInt(1) >= 1;

        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // get list of plays
    public List<Play> getPlays() throws SQLException {
        List<Play> plays = new ArrayList<>();
        Statement stm = connection.createStatement();
        try {
            ResultSet rs = stm.executeQuery("SELECT pid, fid, rid, time, _3d FROM plays;");
            while (rs.next()) {
                int pid = rs.getInt("pid");
                int fid = rs.getInt("fid");
                int rid = rs.getInt("rid");
                DateTime time = new DateTime(rs.getTimestamp("time").getTime());
                boolean _3d = rs.getBoolean("_3d");
                plays.add(new Play(pid, fid, rid, time, _3d));
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return plays;
    }

    public void deletePlay(Play p) throws SQLException, EntryNotFoundException {
        deletePlay(p.getPid());
    }

    // delete a play
    public void deletePlay(int pid) throws SQLException, EntryNotFoundException {
        final String query = "DELETE FROM plays WHERE pid = ?";
        PreparedStatement stm = connection.prepareStatement(query);
        try {
            stm.setInt(1, pid);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

}
