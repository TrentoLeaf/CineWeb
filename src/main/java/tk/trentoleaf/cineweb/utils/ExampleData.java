package tk.trentoleaf.cineweb.utils;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.beans.model.*;
import tk.trentoleaf.cineweb.db.*;
import tk.trentoleaf.cineweb.exceptions.db.AnotherFilmScheduledException;
import tk.trentoleaf.cineweb.exceptions.db.BadRoomException;

import java.util.*;
import java.util.logging.Logger;

/**
 * The purpose of this class is to load examples data in the database.
 */
public final class ExampleData {

    // dbs
    private static final UsersDB usersDB = UsersDB.instance();
    private static final FilmsDB filmsDB = FilmsDB.instance();
    private static final PlaysDB playsDB = PlaysDB.instance();
    private static final RoomsDB roomsDB = RoomsDB.instance();
    private static final BookingsDB bookingsDB = BookingsDB.instance();
    private static final PricesDB pricesDB = PricesDB.instance();

    // random generator
    private static final Random random = new Random();

    // logger
    private static final Logger logger = Logger.getLogger(ExampleData.class.getSimpleName());

    /**
     * Generate and loads example data
     *
     * @param duration Duration of each play
     * @throws Exception if any error
     */
    public static void loadExampleData(int duration) throws Exception {

        // create users
        List<User> users = createUsers();

        // create films
        List<Film> films = createFilms(duration);

        // create rooms
        List<Room> rooms = createRooms();

        // create plays
        List<Play> plays = createPlays(duration, films, rooms);

        // create tickets
        createTickets(users, rooms, plays);
    }

    // create some demo users
    private static List<User> createUsers() {

        // list
        final List<User> users = new ArrayList<>();

        // create admins
        users.add(new User(true, Role.ADMIN, "davide.pedranz@gmail.com", "c1n3w3b", "Davide", "Pedranz", 10000.0));
        users.add(new User(true, Role.ADMIN, "teo@teos.com", "c1n3w3b", "Matteo", "Zeni", 10000.0));
        users.add(new User(true, Role.ADMIN, "willo@willo.com", "c1n3w3b", "Williams", "Rizzi", 10000.0));
        users.add(new User(true, Role.ADMIN, "davide@pippo.com", "c1n3w3b", "Andrea", "Zorzi", 10000.0));
        users.add(new User(true, Role.ADMIN, "davidexxx@pippo.com", "c1n3w3b", "Samuel", "Giacomelli", 10000.0));

        // create 10 clients
        users.add(new User(true, Role.CLIENT, "davide.pedranz+1@gmail.com", "usr", "Giannino", "Toniazzi"));
        users.add(new User(true, Role.CLIENT, "davide.pedranz+2@gmail.com", "usr", "Truce", "Baldazzi"));
        users.add(new User(true, Role.CLIENT, "davide.pedranz+3@gmail.com", "usr", "Jian", "Toscolino"));
        users.add(new User(true, Role.CLIENT, "davide.pedranz+4@gmail.com", "usr", "Mario", "Rossi"));
        users.add(new User(true, Role.CLIENT, "davide.pedranz+5@gmail.com", "usr", "Geronimo", "Figo"));
        users.add(new User(true, Role.CLIENT, "davide.pedranz+6@gmail.com", "usr", "Nico", "Ustino"));
        users.add(new User(true, Role.CLIENT, "davide.pedranz+7@gmail.com", "usr", "Mandrino", "Asti"));
        users.add(new User(true, Role.CLIENT, "davide.pedranz+8@gmail.com", "usr", "Pacchio", "Romolo"));
        users.add(new User(true, Role.CLIENT, "davide.pedranz+9@gmail.com", "usr", "Destio", "Rummi"));
        users.add(new User(true, Role.CLIENT, "davide.pedranz+10@gmail.com", "usr", "Carlina", "Busti"));

        // add users
        for (User u : users) {
            usersDB.createUser(u);
        }

        return users;
    }

    // generate films
    private static List<Film> createFilms(int duration) {

        // create 30 film
        final List<Film> films = new ArrayList<>();
        films.add(new Film("King Kong", "adventure", "https://www.youtube.com/watch?v=AYaTCPbYGdk", duration, "In 1933 New York, an overly ambitious movie producer coerces his cast and hired ship crew to travel to mysterious Skull Island, where they encounter Kong, a giant ape who is immediately smitten with leading lady Ann Darrow.", "http://ia.media-imdb.com/images/M/MV5BMTgzODQwNjAwOV5BMl5BanBnXkFtZTcwMDA4MDA0MQ@@._V1_SY317_CR0,0,214,317_AL_.jpg"));
        films.add(new Film("Cars", "fantasy", "https://www.youtube.com/watch?v=SbXIj2T-_uk", duration, "A hot-shot race-car named Lightning McQueen gets waylaid in Radiator Springs, where he finds the true meaning of friendship and family.", "http://ia.media-imdb.com/images/M/MV5BMTg5NzY0MzA2MV5BMl5BanBnXkFtZTYwNDc3NTc2._V1_SX214_AL_.jpg"));
        films.add(new Film("Jurassic World", "horror", "https://www.youtube.com/watch?v=RFinNxS5KN4", duration, "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond. After 10 years of operation and visitor rates declining, in order to fulfill a corporate mandate, a new attraction is created to re-spark visitors'' interest, which backfires horribly.", "http://ia.media-imdb.com/images/M/MV5BMTQ5MTE0MTk3Nl5BMl5BanBnXkFtZTgwMjczMzk2NTE@._V1_SX214_AL_.jpg"));
        films.add(new Film("American Sniper", "action", "https://www.youtube.com/watch?v=99k3u9ay1gs", duration, "Navy SEAL sniper Chris Kyle''s pinpoint accuracy saves countless lives on the battlefield and turns him into a legend. Back home to his wife and kids after four tours of duty, however, Chris finds that it is the war he can''t leave behind.", "http://ia.media-imdb.com/images/M/MV5BMTkxNzI3ODI4Nl5BMl5BanBnXkFtZTgwMjkwMjY4MjE@._V1_SX214_AL_.jpg"));
        films.add(new Film("The Hangover", "action", "https://www.youtube.com/watch?v=tcdUhdOlz9M", duration, "Three buddies wake up from a bachelor party in Las Vegas, with no memory of the previous night and the bachelor missing. They make their way around the city in order to find their friend before his wedding.", "http://ia.media-imdb.com/images/M/MV5BMTU1MDA1MTYwMF5BMl5BanBnXkFtZTcwMDcxMzA1Mg@@._V1_SX214_AL_.jpg"));
        films.add(new Film("The Godfather", "action", "https://www.youtube.com/watch?v=sY1S34973zA", duration, "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.", "http://ia.media-imdb.com/images/M/MV5BMjEyMjcyNDI4MF5BMl5BanBnXkFtZTcwMDA5Mzg3OA@@._V1_SX214_AL_.jpg"));
        films.add(new Film("Django Unchained", "action", "https://www.youtube.com/watch?v=eUdM9vrCbow", duration, "With the help of a German bounty hunter, a freed slave sets out to rescue his wife from a brutal Mississippi plantation owner.", "http://ia.media-imdb.com/images/M/MV5BMjIyNTQ5NjQ1OV5BMl5BanBnXkFtZTcwODg1MDU4OA@@._V1_SX214_AL_.jpg"));
        films.add(new Film("Shining", "thriller", "https://www.youtube.com/watch?v=S014oGZiSdI", duration, "A family heads to an isolated hotel for the winter where an evil and spiritual presence influences the father into violence, while his psychic son sees horrific forebodings from the past and of the future.", "http://ia.media-imdb.com/images/M/MV5BODMxMjE3NTA4Ml5BMl5BanBnXkFtZTgwNDc0NTIxMDE@._V1_SY317_CR1,0,214,317_AL_.jpg"));
        films.add(new Film("Grease", "musical", "https://www.youtube.com/watch?v=wzWmxjYNfz4", duration, "Good girl Sandy and greaser Danny fell in love over the summer. But when they unexpectedly discover they're now in the same high school, will they be able to rekindle their romance?", "http://ia.media-imdb.com/images/M/MV5BMTcyMTA5MTY3MF5BMl5BanBnXkFtZTgwMTMwNzAxMDE@._V1_SX214_AL_.jpg"));
        films.add(new Film("Avatar", "fantasy", "https://www.youtube.com/watch?v=d1_JBMrrYw8", duration, "A Paraplegic Marine dispatched to the moon Pandora on a unique mission becomes torn between following his orders and protecting the world he feels is his home..", "http://ia.media-imdb.com/images/M/MV5BMTYwOTEwNjAzMl5BMl5BanBnXkFtZTcwODc5MTUwMw@@._V1_SY317_CR0,0,214,317_AL_.jpg"));
        films.add(new Film("Fight Club", "action", "https://www.youtube.com/watch?v=SUXWAEX2jlg", duration, "An insomniac office worker looking for a way to change his life crosses paths with a devil-may-care soap maker and they form an underground fight club that evolves into something much, much more...", "http://ia.media-imdb.com/images/M/MV5BMjIwNTYzMzE1M15BMl5BanBnXkFtZTcwOTE5Mzg3OA@@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Inception", "action", "https://www.youtube.com/watch?v=8hP9D6kZseM", duration, "A thief who steals corporate secrets through use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.", "http://ia.media-imdb.com/images/M/MV5BMTMyMzYxMDQ3NV5BMl5BanBnXkFtZTcwNTA1NTcwMw@@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Matrix", "action", "https://www.youtube.com/watch?v=q_tuIcqX5-g", duration, "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.", "http://ia.media-imdb.com/images/M/MV5BMTkxNDYxOTA4M15BMl5BanBnXkFtZTgwNTk0NzQxMTE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Interstellar", "action", "https://www.youtube.com/watch?v=0vxOhd4qlnA", duration, "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.", "http://ia.media-imdb.com/images/M/MV5BMjIxNTU4MzY4MF5BMl5BanBnXkFtZTgwMzM4ODI3MjE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Edge of Tomorrow", "action", "https://www.youtube.com/watch?v=fLe_qO4AE-M", duration, "A military officer is brought into an alien war against an extraterrestrial enemy who can reset the day and know the future. When this officer is enabled with the same power, he teams up with a Special Forces warrior to try and end the war.", "http://ia.media-imdb.com/images/M/MV5BMTc5OTk4MTM3M15BMl5BanBnXkFtZTgwODcxNjg3MDE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("The Imitation Game", "action", "https://www.youtube.com/watch?v=Sn-7SNrQWMo", duration, "During World War II, mathematician Alan Turing tries to crack the enigma code with help from fellow mathematicians.", "http://ia.media-imdb.com/images/M/MV5BNDkwNTEyMzkzNl5BMl5BanBnXkFtZTgwNTAwNzk3MjE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Guardians of the Galaxy", "action", "https://www.youtube.com/watch?v=89yh3vYs-zo", duration, "A group of intergalactic criminals are forced to work together to stop a fanatical warrior from taking control of the universe.", "http://ia.media-imdb.com/images/M/MV5BMTAwMjU5OTgxNjZeQTJeQWpwZ15BbWU4MDUxNDYxODEx._V1__SX629_SY905_.jpg"));
        films.add(new Film("The Hunger Games: Mockingjay - Part 1", "action", "https://www.youtube.com/watch?v=3PkkHsuMrho", duration, "Katniss Everdeen is in District 13 after she shatters the games forever. Under the leadership of President Coin and the advice of her trusted friends, Katniss spreads her wings as she fights to save Peeta and a nation moved by her courage.", "http://ia.media-imdb.com/images/M/MV5BMTcxNDI2NDAzNl5BMl5BanBnXkFtZTgwODM3MTc2MjE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Captain America: The Winter Soldier", "action", "https://www.youtube.com/watch?v=tbayiPxkUMM", duration, "As Steve Rogers struggles to embrace his role in the modern world, he teams up with another super soldier, the black widow, to battle a new threat from old history: an assassin known as the Winter Soldier.", "http://ia.media-imdb.com/images/M/MV5BMzA2NDkwODAwM15BMl5BanBnXkFtZTgwODk5MTgzMTE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("The Lego Movie", "fantasy", "https://www.youtube.com/watch?v=fZ_JOBCLF-I", duration, "An ordinary Lego construction worker, thought to be the prophesied 'Special', is recruited to join a quest to stop an evil tyrant from gluing the Lego universe into eternal stasis.", "http://ia.media-imdb.com/images/M/MV5BMTg4MDk1ODExN15BMl5BanBnXkFtZTgwNzIyNjg3MDE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Maleficent", "fantasy", "https://www.youtube.com/watch?v=w-XO4XiRop0", duration, "A vengeful fairy is driven to curse an infant princess, only to discover that the child may be the one person who can restore peace to their troubled land.", "http://ia.media-imdb.com/images/M/MV5BMTQ1NDk3NTk0MV5BMl5BanBnXkFtZTgwMTk3MDcxMzE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("X-Men: Days of Future Past", "fantasy", "https://www.youtube.com/watch?v=pK2zYHWDZKo", duration, "The X-Men send Wolverine to the past in a desperate effort to change history and prevent an event that results in doom for both humans and mutants.", "http://ia.media-imdb.com/images/M/MV5BMjEwMDk2NzY4MF5BMl5BanBnXkFtZTgwNTY2OTcwMDE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Dawn of the Planet of the Apes", "action", "https://www.youtube.com/watch?v=DpSaTrW4leg", duration, "A growing nation of genetically evolved apes led by Caesar is threatened by a band of human survivors of the devastating virus unleashed a decade earlier.", "http://ia.media-imdb.com/images/M/MV5BMTgwODk3NDc1N15BMl5BanBnXkFtZTgwNTc1NjQwMjE@._V1__SX1626_SY828_.jpg"));
        films.add(new Film("Intouchables", "action", "https://www.youtube.com/watch?v=34WIbmXkewU", duration, "After he becomes a quadriplegic from a paragliding accident, an aristocrat hires a young man from the projects to be his caregiver.", "http://ia.media-imdb.com/images/M/MV5BMTYxNDA3MDQwNl5BMl5BanBnXkFtZTcwNTU4Mzc1Nw@@._V1__SX629_SY905_.jpg"));
        films.add(new Film("The Wolf of Wall Street", "action", "https://www.youtube.com/watch?v=pabEtIERlic", duration, "Based on the true story of Jordan Belfort, from his rise to a wealthy stock-broker living the high life to his fall involving crime, corruption and the federal government.", "http://ia.media-imdb.com/images/M/MV5BMjIxMjgxNTk0MF5BMl5BanBnXkFtZTgwNjIyOTg2MDE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Spirited Away", "action", "https://www.youtube.com/watch?v=ByXuk9QqQkk", duration, "During her family's move to the suburbs, a sullen 10-year-old girl wanders into a world ruled by gods, witches, and spirits, and where humans are changed into beasts.", "http://ia.media-imdb.com/images/M/MV5BMjYxMDcyMzIzNl5BMl5BanBnXkFtZTYwNDg2MDU3._V1__SX629_SY905_.jpg"));
        films.add(new Film("How to Train Your Dragon 2", "fantasy", "https://www.youtube.com/watch?v=Z9a4PvzlqoQ", duration, "When Hiccup and Toothless discover an ice cave that is home to hundreds of new wild dragons and the mysterious Dragon Rider, the two friends find themselves at the center of a battle to protect the peace.", "http://ia.media-imdb.com/images/M/MV5BMzMwMTAwODczN15BMl5BanBnXkFtZTgwMDk2NDA4MTE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Gone Girl", "action", "https://www.youtube.com/watch?v=esGn-xKFZdU", duration, "With his wife's disappearance having become the focus of an intense media circus, a man sees the spotlight turned on him when it's suspected that he may not be innocent.", "http://ia.media-imdb.com/images/M/MV5BMTk0MDQ3MzAzOV5BMl5BanBnXkFtZTgwNzU1NzE3MjE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("Neighbors", "action", "https://www.youtube.com/watch?v=kL5c2szf3E4", duration, "After they are forced to live next to a fraternity house, a couple with a newborn baby do whatever they can to take them down.", "http://ia.media-imdb.com/images/M/MV5BOTQ0OTkzODgyNF5BMl5BanBnXkFtZTgwOTA3OTE4MDE@._V1__SX629_SY905_.jpg"));
        films.add(new Film("The Fault in Our Stars", "action", "https://www.youtube.com/watch?v=9ItBvH5J6ss", duration, "Two teens, both who have different cancer conditions, fall in love after meeting at a cancer support group.", "http://ia.media-imdb.com/images/M/MV5BMjA4NzkxNzc5Ml5BMl5BanBnXkFtZTgwNzQ3OTMxMTE@._V1__SX629_SY905_.jpg"));

        // add films
        for (Film film : films) {
            filmsDB.createFilm(film);
        }

        return films;
    }

    // create rooms
    private static List<Room> createRooms() {
        Room r1 = roomsDB.createRoom(15, 22);
        Room r2 = roomsDB.createRoom(12, 18);
        Room r3 = roomsDB.createRoom(10, 20, Arrays.asList(new Seat(1, 0), new Seat(0, 1), new Seat(0, 4), new Seat(10, 18), new Seat(9, 8)));
        Room r4 = roomsDB.createRoom(15, 20);

        final int o = SeatCode.AVAILABLE.getValue();
        final int x = SeatCode.MISSING.getValue();

        final int[][] seats = {
                {x, x, x, o, x, x, x},
                {x, x, o, o, o, x, x},
                {x, o, o, o, o, o, x},
                {o, o, o, o, o, o, o},
                {x, o, o, o, o, o, x},
                {x, x, o, o, o, x, x},
                {x, x, x, o, x, x, x}
        };
        Room r5;
        try {
            r5 = roomsDB.createRoom(new RoomStatus(7, 7, seats));
        } catch (BadRoomException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(r1, r2, r3, r4, r5);
    }

    // create plays
    private static List<Play> createPlays(int x, List<Film> films, List<Room> rooms) throws AnotherFilmScheduledException {

        int randomNumber;
        int j = 0; // used for change day

        for (int i = 0; i < films.size() - 1 && (films.size() % 6) == 0; i++) {
            randomNumber = random.nextInt(10);
            //create 3 play with one film on the first room
            playsDB.createPlay(new Play(films.get(i), rooms.get(0), DateTime.now().plusMinutes(randomNumber).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(0), DateTime.now().plusMinutes(randomNumber + (x)).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(0), DateTime.now().plusMinutes(randomNumber + (x * 2)).plusDays(j), random.nextBoolean()));
            i++;
            //create another 3 play with one film on the first room
            playsDB.createPlay(new Play(films.get(i), rooms.get(0), DateTime.now().plusMinutes(randomNumber + (x * 3)).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(0), DateTime.now().plusMinutes(randomNumber + (x * 4)).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(0), DateTime.now().plusMinutes(randomNumber + (x * 5)).plusDays(j), random.nextBoolean()));
            i++;
            randomNumber = random.nextInt(10);
            //create 3 play with one film on the second room
            playsDB.createPlay(new Play(films.get(i), rooms.get(1), DateTime.now().plusMinutes(randomNumber).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(1), DateTime.now().plusMinutes(randomNumber + (x)).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(1), DateTime.now().plusMinutes(randomNumber + (x * 2)).plusDays(j), random.nextBoolean()));
            i++;
            //create another 3 play with one film on the second room
            playsDB.createPlay(new Play(films.get(i), rooms.get(1), DateTime.now().plusMinutes(randomNumber + (x * 3)).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(1), DateTime.now().plusMinutes(randomNumber + (x * 4)).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(1), DateTime.now().plusMinutes(randomNumber + (x * 5)).plusDays(j), random.nextBoolean()));
            i++;
            randomNumber = random.nextInt(10);
            //create 3 play with one film on the third room
            playsDB.createPlay(new Play(films.get(i), rooms.get(2), DateTime.now().plusMinutes(randomNumber).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(4), DateTime.now().plusMinutes(randomNumber + (x)).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(2), DateTime.now().plusMinutes(randomNumber + (x * 2)).plusDays(j), random.nextBoolean()));
            i++;
            randomNumber = random.nextInt(10);
            //create 3 play with one film on the fourth room
            playsDB.createPlay(new Play(films.get(i), rooms.get(3), DateTime.now().plusMinutes(randomNumber).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(3), DateTime.now().plusMinutes(randomNumber + (x)).plusDays(j), random.nextBoolean()));
            playsDB.createPlay(new Play(films.get(i), rooms.get(3), DateTime.now().plusMinutes(randomNumber + (x * 2)).plusDays(j), random.nextBoolean()));

            j++;
        }
        return playsDB.getPlays();
    }

    // create tickets
    private static void createTickets(List<User> users, List<Room> rooms, List<Play> plays) throws Exception {

        Map<Integer, Room> roomsMap = new HashMap<>();
        for (Room r : rooms) {
            roomsMap.put(r.getRid(), r);
        }

        int i = 0;
        while (i < 400) {
            try {
                logger.info("Trying to add the " + i + " booking...");
                List<Ticket> tickets = randomTickets(roomsMap, plays);
                bookingsDB.createBooking(randomUser(users), tickets);
                i++;
            } catch (Exception e) {
                logger.warning("Failed to add the " + i + " booking... SKIP");
            }
        }

    }

    private static User randomUser(List<User> users) {
        int i = random.nextInt(users.size());
        return users.get(i);
    }

    private static Play randomPlay(List<Play> plays) {
        int i = random.nextInt(plays.size());
        return plays.get(i);
    }

    private static Seat randomSeat(Room room) {
        int i = random.nextInt(room.getSeats().size());
        return room.getSeats().get(i);
    }

    private static Price randomPrice() {
        List<Price> prices = pricesDB.getPrices();
        int i = random.nextInt(prices.size());
        return prices.get(i);
    }

    private static List<Ticket> randomTickets(Map<Integer, Room> rooms, List<Play> plays) {
        int m = random.nextInt(2) + 1;
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            int n = random.nextInt(13) + 1;
            Play play = randomPlay(plays);
            Room room = rooms.get(play.getRid());
            for (int j = 0; j < n; j++) {
                Seat seat = randomSeat(room);
                Price price = randomPrice();
                tickets.add(new Ticket(play, seat.getX(), seat.getY(), price.getPrice(), price.getType()));
            }
        }
        return tickets;
    }

}