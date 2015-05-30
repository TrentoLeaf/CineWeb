package tk.trentoleaf.cineweb.rest.exceptions;

public class BadRequestException extends RestException {

    public static final BadRequestException BAD_USER = new BadRequestException("User not valid");

    public BadRequestException(String message) {
        super(message);
    }
}
