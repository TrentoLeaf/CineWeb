package tk.trentoleaf.cineweb.exceptions.rest;

public class BadRequestException extends RestException {

    public BadRequestException(String message) {
        super(message);
    }
}
