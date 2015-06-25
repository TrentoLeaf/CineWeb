package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.beans.model.OldBooking;
import tk.trentoleaf.cineweb.beans.model.Play;
import tk.trentoleaf.cineweb.beans.model.Seat;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.db.FilmAlreadyGoneException;
import tk.trentoleaf.cineweb.exceptions.db.UserNotFoundException;
import tk.trentoleaf.cineweb.beans.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the bookings storage. It uses the connections provided by the DB class.
 *
 * @see tk.trentoleaf.cineweb.db.DB
 */
public class OLDBookingsDB {

    // get a DB instance
    protected DB db = DB.instance();
    protected UsersDB usersDB = UsersDB.instance();
    protected PlaysDB playsDB = PlaysDB.instance();

    // instance -> singleton pattern
    private static OLDBookingsDB instance;

    // instance -> singleton pattern
    public static OLDBookingsDB instance() {
        if (instance == null) {
            instance = new OLDBookingsDB();
        }
        return instance;
    }

    // force to use the singleton
    private OLDBookingsDB() {
    }


    // create a new booking
    public OldBooking createBooking(Seat seat, User user, Play play, double price) throws SQLException, FilmAlreadyGoneException {
        return createBooking(seat, user.getUid(), play.getPid(), price);
    }

    // create a new booking
    public OldBooking createBooking(Seat seat, int uid, int pid, double price) throws SQLException, FilmAlreadyGoneException {
        return createBooking(seat.getRid(), seat.getX(), seat.getY(), uid, pid, price);
    }

    // create a new booking
    public OldBooking createBooking(int rid, int x, int y, int uid, int pid, double price) throws SQLException, FilmAlreadyGoneException {
        final String query = "INSERT INTO bookings (bid, uid, pid, rid, x, y, time_booking, price) VALUES " +
                "(DEFAULT, ?, ?, ?, ?, ?, ?, ?) RETURNING bid";

        // check if the play has already started
        final DateTime timeBooking = new DateTime(System.currentTimeMillis());
        if (playsDB.isOlderPlay(pid, timeBooking)) {
            throw new FilmAlreadyGoneException();
        }

        // create booking object
        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            final OldBooking oldBooking = new OldBooking(rid, x, y, uid, pid, timeBooking, price);

            stm.setInt(1, oldBooking.getUid());
            stm.setInt(2, oldBooking.getPid());
            stm.setInt(3, oldBooking.getRid());
            stm.setInt(4, oldBooking.getX());
            stm.setInt(5, oldBooking.getY());
            stm.setTimestamp(6, new Timestamp(oldBooking.getTimeBooking().toDate().getTime()));
            stm.setDouble(7, price);

            ResultSet rs = stm.executeQuery();
            rs.next();

            // get id and return the oldBooking object
            int bid = rs.getInt("bid");
            oldBooking.setBid(bid);
            return oldBooking;
        }
    }

    // list of all booking
    public List<OldBooking> getBookings() throws SQLException {
        final List<OldBooking> oldBookings = new ArrayList<>();

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
                oldBookings.add(new OldBooking(bid, rid, x, y, uid, pid, timeBooking, price));
            }
        }

        return oldBookings;
    }

    public OldBooking getBooking(int bookingId) throws SQLException, EntryNotFoundException {
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

                return new OldBooking(bookingId, rid, x, y, uid, pid, timeBooking, price);
            }

            // no such booking
            throw new EntryNotFoundException();
        }
    }

    // delete oldBooking
    public void deleteBooking(OldBooking oldBooking) throws SQLException, UserNotFoundException, EntryNotFoundException {

        final String query = "DELETE FROM bookings WHERE bid = ?";
        final User user;

        try (Connection connection = db.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement stm = connection.prepareStatement(query)) {
                stm.setInt(1, oldBooking.getBid());

                int n = stm.executeUpdate();

                if (n == 0) {
                    throw new EntryNotFoundException();
                }

                double accredit = oldBooking.getPrice() * 0.80;
                user = usersDB.getUser(oldBooking.getUid());
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

    // delete a OldBooking
    public void deleteBooking(int bid) throws SQLException, EntryNotFoundException, UserNotFoundException {
        deleteBooking(getBooking(bid));
    }

}
