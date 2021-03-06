package tk.trentoleaf.cineweb.handlers;

import tk.trentoleaf.cineweb.beans.rest.out.ErrorResponse;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Jersey Provider that handles failed Beans Validation (with Hibernate).
 * Return a HTTP 400 with an explanation of the wrong fields.
 *
 * @see tk.trentoleaf.cineweb.exceptions.rest.BadRequestException
 * @see <a href="http://www.restapitutorial.com/httpstatuscodes.html">REST tuturial</a>
 */
@Provider
public class BeanValidationHandler implements ExceptionMapper<ConstraintViolationException> {

    public Response toResponse(ConstraintViolationException ex) {

        // calculate message to display
        final StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            sb.append(v.getMessage());
            sb.append(", ");
        }
        final String message = sb.substring(0, sb.length() - 2);

        // return http 400 + explanation
        return Response.status(400).entity(new ErrorResponse(message)).type(MediaType.APPLICATION_JSON).build();
    }

}
