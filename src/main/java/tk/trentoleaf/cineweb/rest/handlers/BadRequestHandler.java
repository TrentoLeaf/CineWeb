package tk.trentoleaf.cineweb.rest.handlers;

import tk.trentoleaf.cineweb.rest.entities.ErrorResponse;
import tk.trentoleaf.cineweb.rest.exceptions.BadRequestException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestHandler implements ExceptionMapper<BadRequestException> {

    public Response toResponse(BadRequestException ex) {
        return Response.status(400).entity(new ErrorResponse(ex.getMessage())).type(MediaType.APPLICATION_JSON).build();
    }

}
