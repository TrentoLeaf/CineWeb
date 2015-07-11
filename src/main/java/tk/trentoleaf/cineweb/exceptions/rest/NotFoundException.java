package tk.trentoleaf.cineweb.exceptions.rest;

/**
 * Exception that represent a REST error "Not found".
 */
public class NotFoundException extends RestException {

    /**
     * Construct a new NotFoundException with the default error message.
     */
    public NotFoundException() {
        super("Entity not found");
    }

    /**
     * Construct a new NotFoundException with a custom error message.
     *
     * @param message custom error message
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Factory to build a wrong credentials exception.
     *
     * @return NotFoundException with the right error message
     */
    public static NotFoundException wrongCredentials() {
        return new NotFoundException("Wrong email or password");
    }

}