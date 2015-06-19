package tk.trentoleaf.cineweb.handlers;

import tk.trentoleaf.cineweb.beans.rest.out.ErrorResponse;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Jersey Provider that handles NotFoundException. Return an HTTP 404 (not found) with a short explanation.
 *
 * @see tk.trentoleaf.cineweb.exceptions.rest.NotFoundException
 * @see <a href="http://www.restapitutorial.com/httpstatuscodes.html">REST tuturial</a>
 */
@Provider
public class NotFoundHandler implements ExceptionMapper<NotFoundException> {

    public Response toResponse(NotFoundException ex) {
        return Response.status(404).entity(new ErrorResponse(ex.getMessage())).type(MediaType.APPLICATION_JSON).build();
    }

}
