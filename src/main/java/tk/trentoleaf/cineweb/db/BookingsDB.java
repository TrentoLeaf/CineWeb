package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.db.FilmAlreadyGoneException;
import tk.trentoleaf.cineweb.exceptions.db.UserNotFoundException;
import tk.trentoleaf.cineweb.model.Booking;
import tk.trentoleaf.cineweb.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingsDB {

    // get a DB instance
    protected DB db = DB.instance();
    protected UsersDB usersDB = UsersDB.instance();
    protected PlaysDB playsDB = PlaysDB.instance();

    // instance -> singleton pattern
    private static BookingsDB instance;

    // instance -> singleton pattern
    public static BookingsDB instance() {
        if (instance == null) {
            instance = new BookingsDB();
        }
        return instance;
    }

    // force to use the singleton
    private BookingsDB() {
    }

    public Booking createBooking(int rid, int x, int y, int uid, int pid, double price) throws SQLException, FilmAlreadyGoneException {
        final String query = "INSERT INTO bookings (bid, uid, pid, rid, x, y, time_booking, price) VALUES " +
                "(DEFAULT, ?, ?, ?, ?, ?, ?, ?) RETURNING bid";


        final DateTime timeBooking = new DateTime(System.currentTimeMillis());
        if (playsDB.isOlderPlay(pid, timeBooking)) {
            throw new FilmAlreadyGoneException();
        }

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {

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

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
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

    public Booking getBooking(int bookingId) throws SQLException, EntryNotFoundException {
        final String query = "SELECT uid, pid, rid, x, y, time_booking, price FROM bookings WHERE bid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, bookingId);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                int uid = rs.getInt("uid");
                int pid = rs.getInt("pid");
                int rid = rs.getInt("rid");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                DateTime timeBooking = new DateTime(rs.getTimestamp("time_booking").getTime());
                double price = rs.getDouble("price");

                return new Booking(bookingId, rid, x, y, uid, pid, timeBooking, price);
            }

            // no such booking
            throw new EntryNotFoundException();
        }
    }

    // delete booking
    public void deleteBooking(Booking booking) throws SQLException, UserNotFoundException, EntryNotFoundException {

        final String query = "DELETE FROM bookings WHERE bid = ?";
        final User user;

        try (Connection connection = db.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement stm = connection.prepareStatement(query)) {
                stm.setInt(1, booking.getBid());

                int n = stm.executeUpdate();

                if (n == 0) {
                    throw new EntryNotFoundException();
                }

                double accredit = booking.getPrice() * 0.80;
                user = usersDB.getUser(booking.getUid());
                user.addCredit(accredit);

                final String queryUser = "UPDATE users SET credit = ? WHERE uid = ?";

                try (PreparedStatement stmUser = connection.prepareStatement(queryUser)) {
                    stmUser.setDouble(1, user.getCredit());
                    stmUser.setInt(2, user.getUid());
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
    public void deleteBooking(int bid) throws SQLException, EntryNotFoundException, UserNotFoundException {
        deleteBooking(getBooking(bid));
    }

}
