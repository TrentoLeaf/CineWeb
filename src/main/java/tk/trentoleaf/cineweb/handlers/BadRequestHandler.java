package tk.trentoleaf.cineweb.handlers;

import tk.trentoleaf.cineweb.beans.rest.ErrorResponse;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;

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
