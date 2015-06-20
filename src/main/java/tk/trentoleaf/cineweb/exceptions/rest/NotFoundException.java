package tk.trentoleaf.cineweb.exceptions.rest;

public class NotFoundException extends RestException {

    public static final NotFoundException GENERIC = new NotFoundException("Not found");
    public static final NotFoundException USER_NOT_FOUND = new NotFoundException("User not found");
    public static final NotFoundException FILM_NOT_FOUND = new NotFoundException("Film not found");
    public static final NotFoundException PLAY_NOT_FOUND = new NotFoundException("Play not found");

    public NotFoundException(String message) {
        super(message);
    }
}
