package tk.trentoleaf.cineweb.exceptions.db;

/**
 * Exception thrown when trying to insert a new play and there is already a film scheduled
 * for that room and time.
 */
public class AnotherFilmScheduledException extends Exception {
}
