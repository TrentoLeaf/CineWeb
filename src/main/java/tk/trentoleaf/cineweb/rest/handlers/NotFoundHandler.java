package tk.trentoleaf.cineweb.rest.handlers;

import tk.trentoleaf.cineweb.rest.entities.ErrorResponse;
import tk.trentoleaf.cineweb.rest.exceptions.NotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundHandler implements ExceptionMapper<NotFoundException> {

    public Response toResponse(NotFoundException ex) {
        return Response.status(404).entity(new ErrorResponse(ex.getMessage())).type(MediaType.APPLICATION_JSON).build();
    }

}
