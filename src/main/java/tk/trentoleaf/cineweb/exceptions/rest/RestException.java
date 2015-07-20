package tk.trentoleaf.cineweb.exceptions.rest;

import tk.trentoleaf.cineweb.handlers.RestExceptionHandler;

/**
 * Base class for exceptions thrown by REST methods. RestException are caught by a specific handler.
 *
 * @see RestExceptionHandler
 */
public class RestException extends RuntimeException {

    /**
     * Construct a new RestException with a custom error message.
     *
     * @param message custom error message
     */
    public RestException(String message) {
        super(message);
    }

}