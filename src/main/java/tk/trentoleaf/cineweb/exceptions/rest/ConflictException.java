package tk.trentoleaf.cineweb.exceptions.rest;

public class ConflictException extends RestException {

    public static final ConflictException EMAIL_IN_USE = new ConflictException(". Email already in use");
    public static final ConflictException ANOTHER_FILM = new ConflictException("Another film already scheduled");

    public ConflictException(String message) {
        super(message);
    }
}
