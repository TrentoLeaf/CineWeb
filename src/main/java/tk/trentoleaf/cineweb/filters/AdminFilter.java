package tk.trentoleaf.cineweb.filters;

import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.annotations.AdminArea;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.util.Date;
import java.util.logging.Logger;

@AdminArea
@Provider
public class AdminFilter implements ContainerRequestFilter {
    private Logger logger = Logger.getLogger(AdminFilter.class.getSimpleName());

    @Context
    private HttpServletRequest httpRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        // check if a logged user
        final HttpSession session = httpRequest.getSession(false);
        final tk.trentoleaf.cineweb.model.User user = (session != null) ? (tk.trentoleaf.cineweb.model.User) session.getAttribute("user") : null;

        // check the role and uid
        final Role role = (user != null) ? user.getRole() : null;
        final Integer uid = (user != null) ? user.getUid() : null;

        // if no role -> drop request
        if (role == null) {

            // log
            logger.info("REJECTED (must be admin) - REQUEST at " + new Date() + " (uid = " + uid + "): " + requestContext.getMethod() + " " + requestContext.getUriInfo().getRequestUri());

            // TODO: enable
            // return HTTP 401
            // requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }

    }
}
