package tk.trentoleaf.cineweb.db;

import tk.trentoleaf.cineweb.beans.model.Play;
import tk.trentoleaf.cineweb.beans.model.SeatStatus;
import tk.trentoleaf.cineweb.beans.model.Room;
import tk.trentoleaf.cineweb.beans.model.Seat;
import tk.trentoleaf.cineweb.exceptions.db.DBException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class handles the rooms and seats storage. It uses the connections provided by the DB class.
 *
 * @see tk.trentoleaf.cineweb.db.DB
 */
public class RoomsDB {

    // get a DB instance
    protected DB db = DB.instance();

    // instance -> singleton pattern
    private static RoomsDB instance;

    // instance -> singleton pattern
    public static RoomsDB instance() {
        if (instance == null) {
            instance = new RoomsDB();
        }
        return instance;
    }

    // force to use the singleton
    private RoomsDB() {
    }

    // create a Room with all the seats
    public Room createRoom(int rows, int cols) throws DBException {
        return createRoom(rows, cols, new ArrayList<Seat>());
    }

    // create a new Room
    public Room createRoom(int rows, int cols, List<Seat> missingSeats) throws DBException {
        try (Connection connection = db.getConnection()) {

            // create a transaction to ensure TodoDB consistency
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
                throw DBException.factory(e);
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // get the room list
    public List<Room> getRooms(boolean withPlaces) throws DBException {
        List<Room> rooms = new ArrayList<>();

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT rid, rows, cols FROM rooms;");

            while (rs.next()) {
                Room r = new Room();
                r.setRid(rs.getInt("rid"));
                r.setRows(rs.getInt("rows"));
                r.setColumns(rs.getInt("cols"));
                r.setSeats(withPlaces ? getSeatsByRoom(r.getRid()) : new ArrayList<Seat>());
                rooms.add(r);
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return rooms;
    }

    // delete room
    // NB: throw an exception if the room is referenced in any table
    public void deleteRoom(int rid) throws DBException, EntryNotFoundException {
        try (Connection connection = db.getConnection()) {

            // create a transaction to ensure TodoDB consistency
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
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // get the existing seats for a given room
    public List<Seat> getSeatsByRoom(int rid) throws DBException {
        final List<Seat> seats = new ArrayList<>();
        final String query = "SELECT x, y FROM seats WHERE rid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, rid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                seats.add(new Seat(rid, x, y));
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return seats;
    }

    // get a list of all seats reserved by a Play
    public List<Seat> getSeatsReservedByPlay(Play play) throws DBException {
        return getSeatsReservedByPlay(play.getPid());
    }

    //get a list of all seats reserved by a Play.getPid()
    public List<Seat> getSeatsReservedByPlay(int pid) throws DBException {
        final List<Seat> seatsReserved = new ArrayList<>();

        final String query = "SELECT rid, x, y FROM bookings WHERE pid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, pid);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                int rid = rs.getInt("rid");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                seatsReserved.add(new Seat(rid, x, y));
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return seatsReserved;
    }

    // get a list of all seats by a Play
    public List<SeatStatus> getSeatsByPlay(Play play) throws SQLException {
        return getSeatsByPlay(play.getPid());
    }

    // get a list of all seats by a Play
    public List<SeatStatus> getSeatsByPlay(int pid) throws SQLException {

        // list of results
        final List<SeatStatus> seats = new ArrayList<>();

        // query
        final String query = "WITH tmp AS (SELECT * FROM bookings WHERE pid = ?) " +
                "SELECT rid, x, y, (bid IS NOT NULL) AS reserved FROM seats NATURAL LEFT JOIN tmp;";

        // execute query
        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, pid);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                seats.add(new SeatStatus(rs.getInt("rid"), rs.getInt("x"), rs.getInt("y"), rs.getBoolean("reserved")));
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        // return seats
        return seats;
    }

}
