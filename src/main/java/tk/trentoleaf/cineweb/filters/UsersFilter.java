package tk.trentoleaf.cineweb.filters;

import tk.trentoleaf.cineweb.annotations.rest.UserArea;
import tk.trentoleaf.cineweb.db.UsersDB;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Logger;

@UserArea
@Provider
public class UsersFilter implements ContainerRequestFilter {
    private Logger logger = Logger.getLogger(UsersFilter.class.getSimpleName());

    // database
    private UsersDB usersDB = UsersDB.instance();

    @Context
    private HttpServletRequest httpRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        // check if a logged user
        final HttpSession session = httpRequest.getSession(false);
        final Integer uid = (session != null) ? (Integer) session.getAttribute(Utils.UID) : null;

        // check the role
        Role role = null;
        try {
            role = (uid != null) ? usersDB.getUserRoleIfEnabled(uid) : null;
        } catch (SQLException e) {
            logger.severe(e.toString());
        }

        // if no role -> drop request
        if (role != Role.CLIENT && role != Role.ADMIN) {

            // log
            logger.info("REJECTED - REQUEST at " + new Date() + ": " + requestContext.getMethod() + " " + requestContext.getUriInfo().getRequestUri());

            // return HTTP 401
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
