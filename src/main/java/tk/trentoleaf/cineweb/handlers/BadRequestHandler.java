package tk.trentoleaf.cineweb.handlers;

import tk.trentoleaf.cineweb.beans.rest.out.ErrorResponse;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Jersey Provider that handles BadRequestExceptions. Return an HTTP 400 (bad request) with a short explanation.
 *
 * @see tk.trentoleaf.cineweb.exceptions.rest.BadRequestException
 * @see <a href="http://www.restapitutorial.com/httpstatuscodes.html">REST tuturial</a>
 */
@Provider
public class BadRequestHandler implements ExceptionMapper<BadRequestException> {

    public Response toResponse(BadRequestException ex) {
        return Response.status(400).entity(new ErrorResponse(ex.getMessage())).type(MediaType.APPLICATION_JSON).build();
    }

}
