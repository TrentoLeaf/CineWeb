package tk.trentoleaf.cineweb.exceptions.rest;

public class RestException extends RuntimeException {

    public RestException() {
    }

    public RestException(String message) {
        super(message);
    }
}