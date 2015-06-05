package tk.trentoleaf.cineweb.rest.filters;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Provider
@Priority(20)
@PreMatching
public class CsrfFilter implements ContainerRequestFilter {
    private Logger logger = Logger.getLogger(CsrfFilter.class.getSimpleName());

    // methods not to filter
    private static Set<String> methodsWhiteList;

    // pages not to filter
    private static Set<String> pathsWhiteList;

    // load white lists
    static {
        methodsWhiteList = new HashSet<>(3);
        methodsWhiteList.add("GET");
        methodsWhiteList.add("HEAD");
        methodsWhiteList.add("OPTIONS");

        pathsWhiteList = new HashSet<>();
        pathsWhiteList.add("users/login");
        pathsWhiteList.add("users/registration");
        pathsWhiteList.add("users/confirm");
        pathsWhiteList.add("users/forgot-password");
        pathsWhiteList.add("users/change-password-code");
    }

    @Context
    private HttpServletRequest httpRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        // check the path
        final String path = requestContext.getUriInfo().getPath();

        // check the method
        final String method = requestContext.getMethod();

        // check if path or method allowed (WHITE LIST)
        if (!pathsWhiteList.contains(path) && !methodsWhiteList.contains(method)) {

            // check if a logged user
            final HttpSession session = httpRequest.getSession(false);
            final String token = (session != null) ? (String) session.getAttribute("csrf") : null;

            final Cookie cookie = requestContext.getCookies().get("XSRF-TOKEN");
            final String cookieValue = (cookie != null) ? cookie.getValue() : null;
            final String headerValue = requestContext.getHeaderString("X-XSRF-TOKEN");

            // request not valid
            if (cookieValue == null || headerValue == null || token == null || !cookieValue.equals(headerValue) || !headerValue.equals(token)) {

                // log
                logger.info("REJECTED (CSRF) - REQUEST at " + new Date() + ": " + method + " " + requestContext.getUriInfo().getRequestUri());
                logger.info(" +--> cookie: " + cookieValue + ", header: " + headerValue + ", token: " + token);

                // TODO: enable
                // return HTTP 401
                // requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }

        }
    }
}
