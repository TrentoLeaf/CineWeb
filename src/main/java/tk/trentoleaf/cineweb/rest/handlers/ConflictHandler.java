package tk.trentoleaf.cineweb.rest.handlers;

import tk.trentoleaf.cineweb.rest.entities.ErrorResponse;
import tk.trentoleaf.cineweb.rest.exceptions.ConflictException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConflictHandler implements ExceptionMapper<ConflictException> {

    public Response toResponse(ConflictException ex) {
        return Response.status(409).entity(new ErrorResponse(ex.getMessage())).type(MediaType.APPLICATION_JSON).build();
    }

}
