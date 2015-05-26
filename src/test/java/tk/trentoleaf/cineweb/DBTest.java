package tk.trentoleaf.cineweb;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.exceptions.*;
import tk.trentoleaf.cineweb.model.*;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class DBTest {

    private DB db;

    @Before
    public void before() throws SQLException, URISyntaxException, ClassNotFoundException {
        db = new DB();
        db.open();
        db.reset();
        db.init();
    }

    @After
    public void after() throws SQLException {
        if (db != null) {
            db.close();
        }
    }

    @Test
    public void createUserSuccess() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        db.createUser(u1);
        db.createUser(u2);

        // expected users
        final List<User> expected = new ArrayList<>();
        expected.add(u2);
        expected.add(u1);

        // current users
        final List<User> current = db.getUsers();

        // test
        assertEquals(2, current.size());
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void testEmailCase1() throws Exception {

        // email
        final String email = "T342eO@ddAAbb.com";

        // create users
        final User expected = new User(Role.ADMIN, email, "teo", "Matteo", "Zeni");

        // save users
        db.createUser(expected);

        // from db
        final User current = db.getUser(email.toLowerCase());

        // test
        assertEquals(expected, current);
    }

    @Test
    public void testEmailCase2() throws Exception {

        // email
        final String email = "TeO@ddAAbb.com";

        // create users
        final User u1 = new User(Role.ADMIN, email, "teo", "Matteo", "Zeni");
        db.createUser(u1);
        u1.setEmail(email.toUpperCase());

        // expected
        final List<User> expected = new ArrayList<>();
        expected.add(u1);

        // from db
        final List<User> current = db.getUsers();

        // test
        assertTrue(CollectionUtils.isEqualCollection(current, expected));
    }


    @Test(expected = ConstrainException.class)
    public void createUserFail() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(Role.CLIENT, "Teo@teo.com", "dada", "Davide", "Pedranz");

        // save users
        db.createUser(u1);
        db.createUser(u2);
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFail() throws Exception {
        db.getUser("no_email");
    }

    @Test
    public void updateUserSuccess() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        db.createUser(u1);
        db.createUser(u2);

        // edit user 2
        u2.setSecondName("Rossi");
        u2.setRole(Role.ADMIN);
        u2.setEmail("davide@tk.org");
        u2.setCredit(34.23);

        // update user
        db.updateUser(u2);

        // current
        User retrieved = db.getUser("davide@tk.org");

        // test
        assertEquals(u2, retrieved);
    }

    @Test(expected = UserNotFoundException.class)
    public void updateUserFail1() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "aaaaaaaaaa", "bbbbbbbb", "cccccc", "ddddddd");

        // update user
        db.updateUser(u1);
    }

    @Test(expected = ConstrainException.class)
    public void updateUserFail2() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "aaa", "aaa", "aaa", "aaa");
        final User u2 = new User(Role.ADMIN, "bbb", "bbb", "bbb", "bbb");
        db.createUser(u1);
        db.createUser(u2);

        // update user
        u1.setEmail(u2.getEmail().toUpperCase());
        db.updateUser(u1);
    }

    @Test
    public void deleteUserSuccess() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        db.createUser(u1);
        db.createUser(u2);

        // delete user 1
        db.deleteUser(u1.getId());

        // expected users
        final List<User> expected = new ArrayList<>();
        expected.add(u2);

        // current users
        final List<User> current = db.getUsers();

        // test
        assertEquals(1, current.size());
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }


    @Test(expected = UserNotFoundException.class)
    public void deleteUserFail() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");

        // delete user 1
        db.deleteUser(u1.getId());
    }

    @Test
    public void authentication() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");

        // save users
        db.createUser(u1);
        db.createUser(u2);

        // try to authenticate
        assertTrue(db.authenticate("teo@teo.com", "teo"));
        assertFalse(db.authenticate("teo@teo.com", "pippo"));
        assertFalse(db.authenticate("home", "teo"));
        assertFalse(db.authenticate("davide@pippo.com", "teo"));
    }

    @Test
    public void changePasswordSuccess() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u1);

        // change password
        db.changePasswordWithOldPassword("teo@teo.com", "teo", "pippo");

        // check
        assertFalse(db.authenticate("teo@teo.com", "teo"));
        assertTrue(db.authenticate("teo@teo.com", "pippo"));
    }

    @Test(expected = WrongPasswordException.class)
    public void changePasswordFail1() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u1);

        // change password
        db.changePasswordWithOldPassword("teo@teo.com", "wrong_password", "new_password");
    }

    @Test(expected = WrongPasswordException.class)
    public void changePasswordFail2() throws Exception {

        // change password
        db.changePasswordWithOldPassword("not_existing_user", "wrong_password", "new_password");
    }

    @Test(expected = UserNotFoundException.class)
    public void requestResetPasswordFail() throws Exception {
        db.requestResetPassword(1234534);
    }

    @Test
    public void resetPasswordOk() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u1);

        // get code
        String code = db.requestResetPassword(u1.getId());
        assertTrue(db.checkPasswordReset(u1.getId(), code));
        assertTrue(db.checkPasswordReset(u1.getEmail(), code));

        // reset password
        db.changePasswordWithCode("teo@teo.com", code, "pippo");

        // check
        assertFalse(db.authenticate("teo@teo.com", "teo"));
        assertTrue(db.authenticate("teo@teo.com", "pippo"));
    }

    @Test(expected = WrongCodeException.class)
    public void resetPasswordFail() throws Exception {

        // create users
        final User u1 = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u1);

        // random code
        String code = "random_code";
        assertFalse(db.checkPasswordReset(u1.getId(), code));
        assertFalse(db.checkPasswordReset(u1.getEmail(), code));

        // change password
        db.changePasswordWithCode("teo@teo.com", code, "new_password");
    }

    @Test
    public void insertFilmSuccess() throws Exception {

        // films
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);

        // save films
        db.insertFilm(f1);
        db.insertFilm(f2);

        // expected
        List<Film> expected = new ArrayList<>();
        expected.add(f1);
        expected.add(f2);

        // current
        List<Film> current = db.getFilms();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void deleteFilmSuccess() throws Exception {

        // save films
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        db.insertFilm(f1);
        db.insertFilm(f2);

        // delete
        db.deleteFilm(f1.getId());

        // expected
        List<Film> expected = new ArrayList<>();
        expected.add(f2);

        // current
        List<Film> current = db.getFilms();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = EntryNotFoundException.class)
    public void deleteFilmFail() throws Exception {

        // delete
        db.deleteFilm(43543543);
    }

    // TODO: delete fails for presents plays

    @Test
    public void updateFilmSuccess() throws Exception {

        // save films
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        db.insertFilm(f1);
        db.insertFilm(f2);

        // edit film 2
        f2.setTitle("New title");
        f2.setGenre("Wowo");
        f2.setTrailer(null);
        f2.setPlaybill("http:////");
        f2.setPlot(null);
        f2.setDuration(33);

        // update
        db.updateFilm(f2);

        // expected
        List<Film> expected = new ArrayList<>();
        expected.add(f2);
        expected.add(f1);

        // current
        List<Film> current = db.getFilms();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = EntryNotFoundException.class)
    public void updateFilmFail() throws Exception {

        // film
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // edit
        db.updateFilm(f1);
    }

    @Test
    public void insertRoomSuccess() throws Exception {

        // rows, cols
        final int rows = 7;
        final int cols = 5;

        // missing seats
        final List<Seat> missing = new ArrayList<>();
        missing.add(new Seat(0, 0));
        missing.add(new Seat(4, 1));
        missing.add(new Seat(2, 2));
        missing.add(new Seat(5, 2));
        missing.add(new Seat(6, 2));
        missing.add(new Seat(5, 3));
        missing.add(new Seat(6, 3));
        missing.add(new Seat(0, 4));

        // present seats
        final List<Seat> present = new ArrayList<>();
        present.add(new Seat(1, 0));
        present.add(new Seat(2, 0));
        present.add(new Seat(3, 0));
        present.add(new Seat(4, 0));
        present.add(new Seat(5, 0));
        present.add(new Seat(6, 0));
        present.add(new Seat(0, 1));
        present.add(new Seat(1, 1));
        present.add(new Seat(2, 1));
        present.add(new Seat(3, 1));
        present.add(new Seat(5, 1));
        present.add(new Seat(6, 1));
        present.add(new Seat(0, 2));
        present.add(new Seat(1, 2));
        present.add(new Seat(3, 2));
        present.add(new Seat(4, 2));
        present.add(new Seat(0, 3));
        present.add(new Seat(1, 3));
        present.add(new Seat(2, 3));
        present.add(new Seat(3, 3));
        present.add(new Seat(4, 3));
        present.add(new Seat(1, 4));
        present.add(new Seat(2, 4));
        present.add(new Seat(3, 4));
        present.add(new Seat(4, 4));
        present.add(new Seat(5, 4));
        present.add(new Seat(6, 4));

        // current room
        final Room current = db.createRoom(rows, cols, missing);

        // expected
        for (Seat s : present) {
            s.setRid(current.getRid());
        }
        final Room expected = new Room(current.getRid(), rows, cols, present);

        // test
        assertEquals(expected, current);
    }

    @Test
    public void insertRandomRooms() throws Exception {
        final Random random = new Random();

        // try 3 random rooms
        for (int c = 0; c < 3; c++) {

            final int rows = random.nextInt(20) + 1;
            final int cols = random.nextInt(30) + 1;

            final List<Seat> allSeats = new ArrayList<>(rows * cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    allSeats.add(new Seat(i, j));
                }
            }

            final List<Seat> missing = new ArrayList<>();
            final List<Seat> present = new ArrayList<>();

            final int nMissing = random.nextInt(rows * cols);
            for (int i = 0; i < nMissing; i++) {
                int index = random.nextInt(allSeats.size());
                Seat seat = allSeats.get(index);
                allSeats.remove(index);
                missing.add(seat);
            }
            present.addAll(allSeats);

            // current room
            final Room current = db.createRoom(rows, cols, missing);

            // expected
            for (Seat s : present) {
                s.setRid(current.getRid());
            }
            final Room expected = new Room(current.getRid(), rows, cols, present);

            // test
            assertEquals(expected, current);
        }
    }

    private Room createCompleteRoomByDimen(int rid, int rows, int cols) {

        final List<Seat> allSeats = new ArrayList<>(rows * cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                allSeats.add(new Seat(rid, i, j));
            }
        }

        return new Room(rid, rows, cols, allSeats);
    }

    @Test
    public void createCompleteRoom() throws Exception {

        final Random random = new Random();

        final int rows = random.nextInt(15) + 1;
        final int cols = random.nextInt(15) + 1;

        // current room
        final Room current = db.createRoom(rows, cols);

        // expected
        final Room expected = createCompleteRoomByDimen(current.getRid(), rows, cols);

        // test
        assertEquals(expected, current);
    }

    @Test
    public void getRoomsWithSeats() throws Exception {

        // rooms in db
        final Room r1 = db.createRoom(1, 3);
        final Room r2 = db.createRoom(2, 1);
        final Room r3 = db.createRoom(2, 2);

        // expected
        final List<Room> expected = new ArrayList<>(3);
        expected.add(r1);
        expected.add(r2);
        expected.add(r3);

        // current
        final List<Room> current = db.getRooms(true);

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void getRoomsWithoutSeats() throws Exception {

        // rooms in db
        final Room r1 = db.createRoom(23, 3);
        final Room r2 = db.createRoom(10, 3);
        final Room r3 = db.createRoom(4, 2);

        // remove seats
        r1.setSeats(new ArrayList<Seat>());
        r3.setSeats(new ArrayList<Seat>());
        r2.setSeats(new ArrayList<Seat>());

        // expected
        final List<Room> expected = new ArrayList<>(3);
        expected.add(r3);
        expected.add(r2);
        expected.add(r1);

        // current
        final List<Room> current = db.getRooms(false);

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void deleteRoomSuccess() throws Exception {

        // save some rooms
        final Room r1 = db.createRoom(23, 3);
        final Room r2 = db.createRoom(10, 3);
        final Room r3 = db.createRoom(4, 2);

        // remove room 2
        db.deleteRoom(r2.getRid());

        // expected
        final List<Room> expected = new ArrayList<>(3);
        expected.add(r3);
        expected.add(r1);

        // current
        final List<Room> current = db.getRooms(true);

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    // TODO: delete not existing rooms -> exception EntryNotFound

    @Test(expected = PSQLException.class)
    public void deleteRoomFail() throws Exception {

        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        db.insertFilm(f1);
        final Room r1 = db.createRoom(23, 12);

        final Play p1 = new Play(f1, r1, DateTime.now(), true);
        db.createPlay(p1);

        // test delete
        db.deleteRoom(r1.getRid());
    }

    @Test
    public void createPlaySuccess() throws Exception {

        // create films, rooms
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        db.insertFilm(f1);
        db.insertFilm(f2);
        final Room r1 = db.createRoom(23, 12);
        final Room r2 = db.createRoom(2, 5);

        // plays
        final Play p1 = new Play(f1, r2, DateTime.now(), true);
        final Play p2 = new Play(f2, r2, DateTime.now().plusMinutes(34), false);
        final Play p3 = new Play(f1, r1, DateTime.now().plusMinutes(2), false);
        final Play p4 = new Play(f2, r1, DateTime.now().plusMinutes(8), false);

        // insert
        db.createPlay(p1);
        db.createPlay(p2);
        db.createPlay(p3);
        db.createPlay(p4);

        // expected
        final List<Play> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);
        expected.add(p4);

        // current
        final List<Play> current = db.getPlays();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = PSQLException.class)
    public void createPlayFailure1() throws Exception {
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        db.insertFilm(f1);

        final Play p1 = new Play(f1.getId(), 223, DateTime.now(), true);
        db.createPlay(p1);
    }

    @Test(expected = PSQLException.class)
    public void createPlayFailure2() throws Exception {
        final Room r1 = db.createRoom(23, 12);

        final Play p1 = new Play(23434, r1.getRid(), DateTime.now(), true);
        db.createPlay(p1);
    }

    @Test
    public void deletePlaySuccess() throws Exception {

        // create films, rooms
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        db.insertFilm(f1);
        db.insertFilm(f2);
        final Room r1 = db.createRoom(23, 12);
        final Room r2 = db.createRoom(2, 5);

        // plays
        final Play p1 = new Play(f1, r2, DateTime.now(), true);
        final Play p2 = new Play(f2, r2, DateTime.now().plusMinutes(34), false);
        final Play p3 = new Play(f1, r1, DateTime.now().plusMinutes(2), false);
        final Play p4 = new Play(f2, r1, DateTime.now().plusMinutes(8), false);

        // insert
        db.createPlay(p1);
        db.createPlay(p2);
        db.createPlay(p3);
        db.createPlay(p4);

        // remove 2, 4
        db.deletePlay(p2);
        db.deletePlay(p4);

        // expected
        final List<Play> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p3);

        // current
        final List<Play> current = db.getPlays();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = EntryNotFoundException.class)
    public void deletePlayFailure() throws Exception {
        db.deletePlay(24543);
    }

    @Test
    public void checkFilms() throws Exception {

        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        final Film f2 = new Film("Marco", "horror", "http://bbb.com", "http://bbb.org", "trama", 30);
        db.insertFilm(f1);
        db.insertFilm(f2);

        final Room r1 = db.createRoom(4, 5);
        final Room r2 = db.createRoom(2, 3);

        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/05/2015 12:00:00"), true);
        final Play p2 = new Play(f1, r1, ff.parseDateTime("20/05/2015 13:00:00"), true);
        db.createPlay(p1);
        db.createPlay(p2);

        assertFalse(db.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 11:00:00")));
        assertFalse(db.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 11:59:59")));
        assertFalse(db.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 14:00:01")));
        assertFalse(db.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 15:00:00")));
        assertTrue(db.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 12:00:00")));
        assertTrue(db.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 12:01:00")));
        assertTrue(db.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 12:10:00")));
        assertTrue(db.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 13:00:00")));
        assertTrue(db.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 14:00:00")));

        assertFalse(db.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 11:00:00")));
        assertFalse(db.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 11:59:59")));
        assertFalse(db.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 14:00:01")));
        assertFalse(db.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 15:00:00")));
        assertFalse(db.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 12:00:00")));
        assertFalse(db.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 12:01:00")));
        assertFalse(db.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 12:10:00")));
        assertFalse(db.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 13:00:00")));
        assertFalse(db.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 14:00:00")));
    }

}