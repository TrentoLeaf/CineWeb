package tk.trentoleaf.cineweb.handlers;

import tk.trentoleaf.cineweb.beans.rest.out.ErrorResponse;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;
import tk.trentoleaf.cineweb.exceptions.rest.ConflictException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;
import tk.trentoleaf.cineweb.exceptions.rest.RestException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Jersey Provider that handles RestExcretions. Returns the right HTTP error with a short explanation.
 *
 * @see tk.trentoleaf.cineweb.exceptions.rest.RestException
 * @see <a href="http://www.restapitutorial.com/httpstatuscodes.html">REST tuturial</a>
 */
@Provider
public class RestExceptionHandler implements ExceptionMapper<RestException> {

    public Response toResponse(RestException e) {

        // select the correct error code
        final int status;
        if (e instanceof BadRequestException) {
            status = 400;
        } else if (e instanceof NotFoundException) {
            status = 404;
        } else if (e instanceof ConflictException) {
            status = 409;
        } else {
            status = 500;
        }

        // return an error message
        return Response.status(status).entity(new ErrorResponse(e.getMessage())).type(MediaType.APPLICATION_JSON).build();
    }

}
