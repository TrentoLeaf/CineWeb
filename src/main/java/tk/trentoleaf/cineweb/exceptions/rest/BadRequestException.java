package tk.trentoleaf.cineweb.exceptions.rest;

/**
 * Exception that represent a REST error "Bad request".
 */
public class BadRequestException extends RestException {

    /**
     * Construct a new BadRequestException with a custom error message.
     */
    public BadRequestException(String message) {
        super(message);
    }

}