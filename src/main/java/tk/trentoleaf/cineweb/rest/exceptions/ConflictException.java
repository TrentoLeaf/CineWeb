package tk.trentoleaf.cineweb.rest.exceptions;

public class ConflictException extends RestException {

    public static final ConflictException EMAIL_IN_USE = new ConflictException("Email already in use");

    public ConflictException(String message) {
        super(message);
    }
}
