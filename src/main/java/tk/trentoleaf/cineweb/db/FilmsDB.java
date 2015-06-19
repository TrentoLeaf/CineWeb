package tk.trentoleaf.cineweb.db;

import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.model.Film;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FilmsDB {
    private final Logger logger = Logger.getLogger(FilmsDB.class.getSimpleName());

    // get a DB instance
    protected DB db = DB.instance();

    // instance -> singleton pattern
    private static FilmsDB instance;

    // instance -> singleton pattern
    public static FilmsDB instance() {
        if (instance == null) {
            instance = new FilmsDB();
        }
        return instance;
    }

    // force to use the singleton
    private FilmsDB() {
    }

    // insert a new film
    public void createFilm(Film film) throws SQLException {
        final String query = "INSERT INTO films (fid, title, genre, trailer, playbill, plot, duration) VALUES " +
                "(DEFAULT, ?, ?, ?, ?, ?, ?) RETURNING fid";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, film.getTitle());
            stm.setString(2, film.getGenre());
            stm.setString(3, film.getTrailer());
            stm.setString(4, film.getPlaybill());
            stm.setString(5, film.getPlot());
            stm.setInt(6, film.getDuration());
            ResultSet rs = stm.executeQuery();
            rs.next();
            film.setFid(rs.getInt("fid"));
        }
    }

    // list of users
    public Film getFilm(int fid) throws SQLException, EntryNotFoundException {
        final String query = "SELECT title, genre, trailer, playbill, plot, duration FROM films WHERE fid = ?;";
        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, fid);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                Film f = new Film();
                f.setFid(fid);
                f.setTitle(rs.getString("title"));
                f.setGenre(rs.getString("genre"));
                f.setTrailer(rs.getString("trailer"));
                f.setPlaybill(rs.getString("playbill"));
                f.setPlot(rs.getString("plot"));
                f.setDuration(rs.getInt("duration"));
                return f;
            }

            // no such film found
            throw new EntryNotFoundException();
        }
    }

    // list of films
    public List<Film> getFilms() throws SQLException {
        List<Film> films = new ArrayList<>();

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT fid, title, genre, trailer, playbill, plot, duration FROM films;");

            while (rs.next()) {
                Film f = new Film();
                f.setFid(rs.getInt("fid"));
                f.setTitle(rs.getString("title"));
                f.setGenre(rs.getString("genre"));
                f.setTrailer(rs.getString("trailer"));
                f.setPlaybill(rs.getString("playbill"));
                f.setPlot(rs.getString("plot"));
                f.setDuration(rs.getInt("duration"));
                films.add(f);
            }
        }

        return films;
    }

    // edit film
    public void updateFilm(Film film) throws SQLException, EntryNotFoundException {
        final String query = "UPDATE films SET title = ?, genre = ?, trailer = ?, playbill = ?, plot = ?, duration = ? WHERE fid = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, film.getTitle());
            stm.setString(2, film.getGenre());
            stm.setString(3, film.getTrailer());
            stm.setString(4, film.getPlaybill());
            stm.setString(5, film.getPlot());
            stm.setInt(6, film.getDuration());
            stm.setInt(7, film.getFid());

            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }
        }
    }

    // delete film
    public void deleteFilm(Film film) throws SQLException, EntryNotFoundException {
        deleteFilm(film.getFid());
    }

    // delete film
    public void deleteFilm(int id) throws SQLException, EntryNotFoundException {
        final String query = "DELETE FROM films WHERE fid = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, id);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }
        }
    }

}
