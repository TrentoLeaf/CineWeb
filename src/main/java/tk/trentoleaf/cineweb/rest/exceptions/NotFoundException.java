package tk.trentoleaf.cineweb.rest.exceptions;

public class NotFoundException extends RestException {

    public static final NotFoundException GENERIC = new NotFoundException("Not found");
    public static final NotFoundException USER_NOT_FOUND = new NotFoundException("User not found");

    public NotFoundException(String message) {
        super(message);
    }
}
