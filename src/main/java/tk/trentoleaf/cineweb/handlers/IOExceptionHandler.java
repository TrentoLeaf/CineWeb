package tk.trentoleaf.cineweb.handlers;

import tk.trentoleaf.cineweb.beans.rest.out.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Jersey Provider that handles IOException (eg. bad JSON provided). Return an HTTP 400 (bad request).
 *
 * @see tk.trentoleaf.cineweb.utils.GsonJerseyProvider
 */
@Provider
public class IOExceptionHandler implements ExceptionMapper<IOException> {

    public Response toResponse(IOException ex) {
        return Response.status(400).entity(new ErrorResponse("Bad JSON")).type(MediaType.APPLICATION_JSON).build();
    }

}
