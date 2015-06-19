package tk.trentoleaf.cineweb.db;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.model.Film;
import tk.trentoleaf.cineweb.model.Play;
import tk.trentoleaf.cineweb.model.Room;
import tk.trentoleaf.cineweb.model.Seat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoomsDBTest extends DBTest {

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
        final Room current = roomsDB.createRoom(rows, cols, missing);

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
            final Room current = roomsDB.createRoom(rows, cols, missing);

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
        final Room current = roomsDB.createRoom(rows, cols);

        // expected
        final Room expected = createCompleteRoomByDimen(current.getRid(), rows, cols);

        // test
        assertEquals(expected, current);
    }

    @Test
    public void getRoomsWithSeats() throws Exception {

        // rooms in roomsDB
        final Room r1 = roomsDB.createRoom(1, 3);
        final Room r2 = roomsDB.createRoom(2, 1);
        final Room r3 = roomsDB.createRoom(2, 2);

        // expected
        final List<Room> expected = new ArrayList<>(3);
        expected.add(r1);
        expected.add(r2);
        expected.add(r3);

        // current
        final List<Room> current = roomsDB.getRooms(true);

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void getRoomsWithoutSeats() throws Exception {

        // rooms in roomsDB
        final Room r1 = roomsDB.createRoom(23, 3);
        final Room r2 = roomsDB.createRoom(10, 3);
        final Room r3 = roomsDB.createRoom(4, 2);

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
        final List<Room> current = roomsDB.getRooms(false);

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void deleteRoomSuccess() throws Exception {

        // save some rooms
        final Room r1 = roomsDB.createRoom(23, 3);
        final Room r2 = roomsDB.createRoom(10, 3);
        final Room r3 = roomsDB.createRoom(4, 2);

        // remove room 2
        roomsDB.deleteRoom(r2.getRid());

        // expected
        final List<Room> expected = new ArrayList<>(3);
        expected.add(r3);
        expected.add(r1);

        // current
        final List<Room> current = roomsDB.getRooms(true);

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = PSQLException.class)
    public void deleteRoomFail1() throws Exception {

        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room r1 = roomsDB.createRoom(23, 12);

        final Play p1 = new Play(f1, r1, DateTime.now(), true);
        playsDB.createPlay(p1);

        // test delete
        roomsDB.deleteRoom(r1.getRid());
    }

    @Test(expected = EntryNotFoundException.class)
    public void deleteRoomFail2() throws Exception {

        // test delete
        roomsDB.deleteRoom(234);
    }

}