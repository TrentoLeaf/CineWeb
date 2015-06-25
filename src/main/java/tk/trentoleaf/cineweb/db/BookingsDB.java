package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.beans.model.*;
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

    // create a new booking
    public void createBooking(Booking booking) throws DBException, FilmAlreadyGoneException {

        // TODO: check if some play is already gone... for each film

        // create connection
        try (Connection connection = db.getConnection()) {

            // start transaction
            connection.setAutoCommit(false);

            // insert booking query
            final String bookingQuery = "INSERT INTO bookings (bid, uid, booking_time, payed_with_credit) " +
                    "VALUES (DEFAULT, ?, ?, ?) RETURNING bid;";

            // insert booking
            try (PreparedStatement bookingStm = connection.prepareStatement(bookingQuery)) {
                bookingStm.setInt(1, booking.getUid());
                bookingStm.setTimestamp(2, new Timestamp(booking.getTime().toDate().getTime()));
                bookingStm.setDouble(3, booking.getPayedWithCredit());

                final ResultSet bookingRs = bookingStm.executeQuery();
                bookingRs.next();
                booking.setBid(bookingRs.getInt("bid"));

                // query to insert a new ticket
                final String ticketQuery = "INSERT INTO tickets (tid, bid, pid, rid, x, y, price, type, deleted) " +
                        "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, FALSE) RETURNING tid";

                // insert every ticket
                for (Ticket ticket : booking.getTickets()) {

                    // try to insert the current ticket
                    try (PreparedStatement ticketStm = connection.prepareStatement(ticketQuery)) {
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

                // do transaction
                connection.commit();

            } catch (SQLException e) {

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
    public List<Booking> getBookings(boolean withTickets) throws DBException {
        final List<Booking> bookings = new ArrayList<>();

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT bid, uid, booking_time, payed_with_credit FROM bookings;");

            while (rs.next()) {
                Booking b = new Booking();
                b.setBid(rs.getInt("bid"));
                b.setUid(rs.getInt("uid"));
                b.setTime(new DateTime(rs.getTimestamp("booking_time").getTime()));
                b.setPayedWithCredit(rs.getDouble("played_with_credit"));
                bookings.add(b);
            }

            // retrieve tickets if asked
            if (withTickets) {

                // query
                final String query = "SELECT tid, pid, rid, x, y, price, type, deleted FROM tickets WHERE bid = ?;";

                // iterate over bookings
                for (Booking b : bookings) {

                    // create tickets array
                    final List<Ticket> tickets = new ArrayList<>();
                    b.setTickets(tickets);

                    // get the tickets
                    try (PreparedStatement pstm = connection.prepareStatement(query)) {
                        pstm.setInt(1, b.getBid());

                        ResultSet prs = pstm.executeQuery();
                        while (prs.next()) {
                            Ticket t = new Ticket();
                            t.setTid(prs.getInt("tid"));
                            t.setBid(b.getBid());
                            t.setPid(prs.getInt("pid"));
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
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return bookings;
    }

    // delete ticket ==> set deleted
    // and update the credit of the user
    public void deleteTicket(Ticket ticket) throws DBException, TicketAlreadyDeletedException {
        deleteTicket(ticket.getTid());
    }

    // delete ticket ==> set deleted
    // and update the credit of the user
    public void deleteTicket(int tid) throws DBException, TicketAlreadyDeletedException {

        // open the connection
        try (Connection connection = db.getConnection()) {

            // begin transaction
            connection.setAutoCommit(false);

            // query to mark the ticket as deleted
            final String query = "UPDATE tickets SET deleted = TRUE WHERE tid = ? AND deleted = FALSE;";

            // esecute query
            try (PreparedStatement stm = connection.prepareStatement(query)) {
                stm.setInt(1, tid);

                // check result -> must be exactly one
                int n = stm.executeUpdate();
                if (n != 1) {
                    throw new TicketAlreadyDeletedException();
                }

                // query to update the buyer user
                final String queryUser = "WITH tmp AS (SELECT uid, price FROM tickets NATURAL JOIN bookings WHERE tid = ?)" +
                        "UPDATE users SET credit = credit + (SELECT price FROM tmp) WHERE uid = (SELECT uid FROM tmp);";

                // update user balance
                try (PreparedStatement stmUser = connection.prepareStatement(queryUser)) {
                    stmUser.setInt(1, tid);

                    // check result -> must be exactly one
                    int m = stmUser.executeUpdate();
                    if (m != 1) {
                        throw new TicketAlreadyDeletedException();
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
