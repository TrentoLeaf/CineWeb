package tk.trentoleaf.cineweb.db;

import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.db.WrongCodeException;
import tk.trentoleaf.cineweb.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class RoomsDB {
    private final Logger logger = Logger.getLogger(RoomsDB.class.getSimpleName());

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
    public Room createRoom(int rows, int cols) throws SQLException {
        return createRoom(rows, cols, new ArrayList<Seat>());
    }

    // create a new Room
    public Room createRoom(int rows, int cols, List<Seat> missingSeats) throws SQLException {
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
                throw e;
            }
        }
    }

    // get the room list
    public List<Room> getRooms(boolean withPlaces) throws SQLException {
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
        }

        return rooms;
    }

    // delete room
    // NB: throw an exception if the room is referenced in any table
    public void deleteRoom(int rid) throws SQLException, EntryNotFoundException {
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
        }
    }

    // get the existing seats for a given room
    public List<Seat> getSeatsByRoom(int rid) throws SQLException {
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
        }

        return seats;
    }

    // get a list of all seats reserved by a Play
    public List<SeatReserved> getSeatsReservedByPlay(Play play) throws SQLException, WrongCodeException {
        return getSeatsReservedByPlay(play.getPid());
    }

    //get a list of all seats reserved by a Play.getPid()
    public List<SeatReserved> getSeatsReservedByPlay(int pid) throws SQLException, WrongCodeException {
        final List<SeatReserved> seatsReserved = new ArrayList<>();

        final String query = "SELECT rid, x, y FROM bookings WHERE pid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, pid);
            ResultSet rs = stm.executeQuery();

            int ridCheck = -1;
            while (rs.next()) {
                int rid = rs.getInt("rid");

                // check if all seats find are part of the same room
                if (ridCheck == rid || ridCheck == -1) {
                    ridCheck = rid;
                } else
                    throw new WrongCodeException();

                int x = rs.getInt("x");
                int y = rs.getInt("y");
                seatsReserved.add(new SeatReserved(rid, x, y, true));
            }
        }

        return seatsReserved;
    }

    // get a list of all seats by a Play
    public List<SeatReserved> getSeatsByPlay(Play play) throws SQLException, WrongCodeException {
        return getSeatsByPlay(play.getPid());
    }

    // get a list of all seats by a Play.getPid()
    public List<SeatReserved> getSeatsByPlay(int pid) throws SQLException, WrongCodeException {
        //list of seat reserved
        final List<SeatReserved> seatReserved = getSeatsReservedByPlay(pid);

        //list of all seat in the room
        final List<Seat> allSeat = getSeatsByRoom(seatReserved.get(1).getRid());

        //list of all seatReserved in the room
        final List<SeatReserved> seat = new ArrayList<>();

        for (int i = 0; i < allSeat.size(); i++) {
            if (seatReserved.contains(new SeatReserved(allSeat.get(i).getRid(), allSeat.get(i).getX(), allSeat.get(i).getY(), true))) {
                seat.add(new SeatReserved(allSeat.get(i).getRid(), allSeat.get(i).getX(), allSeat.get(i).getY(), true));
            } else {
                seat.add(new SeatReserved(allSeat.get(i).getRid(), allSeat.get(i).getX(), allSeat.get(i).getY(), false));
            }
        }

        return seat;
    }

}
