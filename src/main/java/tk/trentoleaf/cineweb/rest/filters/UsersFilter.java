package tk.trentoleaf.cineweb.rest.filters;

import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.rest.annotations.UserArea;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

@UserArea
@Provider
public class UsersFilter implements ContainerRequestFilter {
    private Logger logger = Logger.getLogger(UsersFilter.class.getSimpleName());

    @Context
    private HttpServletRequest httpRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // check if a logged user
        final HttpSession session = httpRequest.getSession(false);
        final tk.trentoleaf.cineweb.model.User user = (session != null) ? (tk.trentoleaf.cineweb.model.User) session.getAttribute("user") : null;

        // check the role
        final Role role = (user != null) ? user.getRole() : null;

        // if no role -> drop request
        if (role == null) {

            // log
            logger.info("REJECTED - REQUEST at " + new Date() + ": " + requestContext.getMethod() + " " + requestContext.getUriInfo().getRequestUri());

            // TODO: enable
            // return HTTP 401
            // requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }

    }
}
