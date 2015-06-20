package tk.trentoleaf.cineweb.handlers;

import tk.trentoleaf.cineweb.beans.rest.out.ErrorResponse;
import tk.trentoleaf.cineweb.exceptions.rest.ConflictException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Jersey Provider that handles ConflictException. Return an HTTP 409 (conflict) with a short explanation.
 *
 * @see tk.trentoleaf.cineweb.exceptions.rest.ConflictException
 * @see <a href="http://www.restapitutorial.com/httpstatuscodes.html">REST tuturial</a>
 */
@Provider
public class ConflictHandler implements ExceptionMapper<ConflictException> {

    public Response toResponse(ConflictException ex) {
        return Response.status(409).entity(new ErrorResponse(ex.getMessage())).type(MediaType.APPLICATION_JSON).build();
    }

}
