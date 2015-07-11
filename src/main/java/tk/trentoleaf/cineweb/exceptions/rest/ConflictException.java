package tk.trentoleaf.cineweb.exceptions.rest;

/**
 * Exception that represent a REST error "Conflict". It is used in case it is not possible to
 * create or delete some entity because of some constraint violation.
 */
public class ConflictException extends RestException {

    /**
     * Construct a new ConflictException with a custom error message.
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * Factory to build a ConflictException in case the email used is already in use.
     *
     * @return ConflictException with the right error message
     */
    public static ConflictException emailInUse() {
        return new ConflictException("Email already in use");
    }
}
