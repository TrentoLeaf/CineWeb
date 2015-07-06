package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.beans.model.Booking;
import tk.trentoleaf.cineweb.beans.model.Ticket;
import tk.trentoleaf.cineweb.beans.model.User;
import tk.trentoleaf.cineweb.exceptions.db.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the bookings storage. It uses the connections provided by the DB class.
 *
 * @see DB
 */
public class BookingsDB {

    // get a DB instance
    protected DB db = DB.instance();

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

    // create a new booking
    public Booking createBooking(User user, List<Ticket> tickets) throws DBException, UserNotFoundException, PlayGoneException {
        return createBooking(user.getUid(), tickets);
    }

    // create a new booking
    public Booking createBooking(int uid, List<Ticket> tickets) throws DBException, UserNotFoundException, PlayGoneException {

        // TODO: check if some play is already gone... for each film
        // booking in creation
        int bid;

        // create connection
        try (Connection connection = db.getConnection()) {
            try {

                // start transaction
                connection.setAutoCommit(false);

                // calculate total cost
                double cost = 0;
                for (Ticket t : tickets) {
                    cost += t.getPrice();
                }

                // get user credit
                double userCredit;

                // get user credit
                try (PreparedStatement userStm = connection.prepareStatement("SELECT credit FROM users WHERE uid = ?")) {
                    userStm.setInt(1, uid);
                    ResultSet userRs = userStm.executeQuery();

                    if (userRs.next()) {
                        userCredit = userRs.getDouble("credit");
                    } else {
                        throw new UserNotFoundException();
                    }
                }

                // calculate how much I pay with credit and my new credit
                double payedWithCredit;
                if (cost >= userCredit) {
                    payedWithCredit = userCredit;
                    userCredit = 0;
                } else {
                    payedWithCredit = cost;
                    userCredit -= cost;
                }

                // update the user credit
                try (PreparedStatement userStm = connection.prepareStatement("UPDATE users SET credit = ? WHERE uid = ?")) {
                    userStm.setDouble(1, userCredit);
                    userStm.setInt(2, uid);

                    int n = userStm.executeUpdate();
                    if (n != 1) {
                        throw new UserNotFoundException();
                    }
                }

                // get current time
                long now = System.currentTimeMillis();

                // insert booking query
                final String q3 = "INSERT INTO bookings (bid, uid, booking_time, payed_with_credit) " +
                        "VALUES (DEFAULT, ?, ?, ?) RETURNING bid;";

                // insert booking
                try (PreparedStatement bookingStm = connection.prepareStatement(q3)) {
                    bookingStm.setInt(1, uid);
                    bookingStm.setTimestamp(2, new Timestamp(now));
                    bookingStm.setDouble(3, payedWithCredit);

                    final ResultSet bookingRs = bookingStm.executeQuery();
                    bookingRs.next();
                    bid = bookingRs.getInt("bid");

                    // check if the play has already started
                    final String q4 = "SELECT COUNT(*) FROM plays WHERE pid = ? AND time >= now();";

                    // query to insert a new ticket
                    final String q5 = "INSERT INTO tickets (tid, bid, pid, rid, x, y, price, type) " +
                            "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?) RETURNING tid";

                    // insert every ticket
                    for (Ticket ticket : tickets) {
                        ticket.setBid(bid);

                        // check if this play has gone
                        try (PreparedStatement stm4 = connection.prepareStatement(q4)) {
                            stm4.setInt(1, ticket.getPid());

                            final ResultSet rs4 = stm4.executeQuery();
                            rs4.next();
                            if (rs4.getInt(1) != 1) {
                                throw new PlayGoneException();
                            }
                        }

                        // try to insert the current ticket
                        try (PreparedStatement ticketStm = connection.prepareStatement(q5)) {
                            ticketStm.setInt(1, ticket.getBid());
                            ticketStm.setInt(2, ticket.getPid());
                            ticketStm.setInt(3, ticket.getRid());
                            ticketStm.setInt(4, ticket.getX());
                            ticketStm.setInt(5, ticket.getY());
                            ticketStm.setDouble(6, ticket.getPrice());
                            ticketStm.setString(7, ticket.getType());

                            final ResultSet ticketRs = ticketStm.executeQuery();
                            ticketRs.next();
                            ticket.setTid(ticketRs.getInt("tid"));
                        }
                    }
                }

                // do transaction
                connection.commit();

                // return the booking
                final Booking booking = new Booking();
                booking.setBid(bid);
                booking.setUid(uid);
                booking.setTime(new DateTime(now));
                booking.setPayedWithCredit(payedWithCredit);
                booking.setTickets(tickets);

                return booking;

            } catch (SQLException | UserNotFoundException e) {

                // something went wrong -> rollback
                connection.rollback();

                // report the error
                throw e;
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // list of all booking
    public List<Booking> getBookings() throws DBException {
        return getBookingsByUser(null);
    }

    // list of all bookings for the given user
    public List<Booking> getBookingsByUser(Integer uid) throws DBException {
        final List<Booking> bookings = new ArrayList<>();

        // query for the bookings
        final String q1 = "SELECT bid, uid, booking_time, payed_with_credit FROM bookings"
                + (uid != null ? " WHERE uid = ?" : "") + ";";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(q1)) {
            if (uid != null) {
                stm.setInt(1, uid);
            }

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Booking b = new Booking();
                b.setBid(rs.getInt("bid"));
                b.setUid(rs.getInt("uid"));
                b.setTime(new DateTime(rs.getTimestamp("booking_time").getTime()));
                b.setPayedWithCredit(rs.getDouble("payed_with_credit"));
                bookings.add(b);
            }

            // q2
            final String q2 = "SELECT tid, pid, title, rid, x, y, price, type, FALSE AS deleted FROM tickets " +
                    "NATURAL JOIN plays NATURAL JOIN films WHERE bid = ?" +
                    "UNION SELECT tid, pid, title, rid, x, y, price, type, TRUE AS deleted FROM deleted_tickets " +
                    "NATURAL JOIN plays NATURAL JOIN films WHERE bid = ?;";

            // iterate over bookings
            for (Booking b : bookings) {

                // create tickets array
                final List<Ticket> tickets = new ArrayList<>();
                b.setTickets(tickets);

                // get the tickets
                try (PreparedStatement pstm = connection.prepareStatement(q2)) {
                    pstm.setInt(1, b.getBid());
                    pstm.setInt(2, b.getBid());

                    ResultSet prs = pstm.executeQuery();
                    while (prs.next()) {
                        Ticket t = new Ticket();
                        t.setTid(prs.getInt("tid"));
                        t.setBid(b.getBid());
                        t.setPid(prs.getInt("pid"));
                        t.setTitle(prs.getString("title"));
                        t.setRid(prs.getInt("rid"));
                        t.setX(prs.getInt("x"));
                        t.setY(prs.getInt("y"));
                        t.setPrice(prs.getDouble("price"));
                        t.setType(prs.getString("type"));
                        t.setDeleted(prs.getBoolean("deleted"));
                        tickets.add(t);
                    }
                }
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return bookings;
    }

    // delete ticket ==> set deleted
    // and update the credit of the user
    public void deleteTicket(Ticket ticket) throws DBException, EntryNotFoundException {
        deleteTicket(ticket.getTid());
    }

    // delete ticket ==> set deleted
    // and update the credit of the user
    public void deleteTicket(int tid) throws DBException, EntryNotFoundException {

        // open the connection
        try (Connection connection = db.getConnection()) {

            // begin transaction
            connection.setAutoCommit(false);

            // query to mark the ticket as deleted
            final String query = "WITH tt AS (DELETE FROM tickets WHERE tid = ? RETURNING *) " +
                    "INSERT INTO deleted_tickets SELECT * FROM tt;";

            // esecute query
            try (PreparedStatement stm = connection.prepareStatement(query)) {
                stm.setInt(1, tid);

                // check result -> must be exactly one
                int n = stm.executeUpdate();
                if (n != 1) {
                    throw new EntryNotFoundException();
                }

                // query to update the buyer user
                final String queryUser = "WITH tmp AS (SELECT uid, price FROM deleted_tickets NATURAL JOIN bookings WHERE tid = ?)" +
                        "UPDATE users SET credit = credit + (SELECT price * 0.8 FROM tmp) WHERE uid = (SELECT uid FROM tmp);";

                // update user balance
                try (PreparedStatement stmUser = connection.prepareStatement(queryUser)) {
                    stmUser.setInt(1, tid);

                    // check result -> must be exactly one
                    int m = stmUser.executeUpdate();
                    if (m != 1) {
                        throw new EntryNotFoundException();
                    }
                }

                // end transaction
                connection.commit();

            } catch (SQLException e) {

                // if any error -> rollback
                connection.rollback();

                // report the error
                throw e;
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

}
