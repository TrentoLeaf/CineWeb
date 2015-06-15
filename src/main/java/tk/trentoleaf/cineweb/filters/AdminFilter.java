package tk.trentoleaf.cineweb.filters;

import tk.trentoleaf.cineweb.annotations.AdminArea;
import tk.trentoleaf.cineweb.db.DB;
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

@AdminArea
@Provider
public class AdminFilter implements ContainerRequestFilter {
    private Logger logger = Logger.getLogger(AdminFilter.class.getSimpleName());

    // database
    private DB db = DB.instance();

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
            role = (uid != null) ? db.getUserRoleIfEnabled(uid) : null;
        } catch (SQLException e) {
            logger.severe(e.toString());
        }

        // if not admin -> drop request
        if (role != Role.ADMIN) {

            // log
            logger.info("REJECTED (must be admin) - REQUEST at " + new Date() + " (uid = " + uid + "): " + requestContext.getMethod() + " " + requestContext.getUriInfo().getRequestUri());

            // return HTTP 401
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
