package tk.trentoleaf.cineweb.filters.rest;

import tk.trentoleaf.cineweb.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * This filter logs every request to the REST APIs.
 */
@Provider
@PreMatching
public class LoggerFilter implements ContainerRequestFilter {
    private Logger logger = Logger.getLogger(LoggerFilter.class.getSimpleName());

    @Context
    private HttpServletRequest httpRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // check if a logged user
        final HttpSession session = httpRequest.getSession(false);
        final User user = (session != null) ? (User) session.getAttribute("user") : null;
        final Integer uid = (user != null) ? user.getUid() : null;

        // log the request
        logger.info("REQUEST (uid = " + uid + "): " + requestContext.getMethod() + " " +
                requestContext.getUriInfo().getRequestUri());
    }
}
