package tk.trentoleaf.cineweb.filters.all;

import tk.trentoleaf.cineweb.utils.CSRFUtils;
import tk.trentoleaf.cineweb.utils.Utils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This filter implements a protection layer against CSRF attacks. A CSRF token is added to HTTP GET responses (as a
 * cookie, when the session is created). On POST, PUT, DELETE requests, the same token is expected in a custom HTTP
 * header and is validated against the HTTP Session.
 *
 * @see <a href="http://seclab.stanford.edu/websec/csrf/csrf.pdf">Robust Defenses for Cross-Site Request Forgery</a>
 */
@WebFilter(urlPatterns = "/*")
public class CSRFFilter implements Filter {
    private Logger logger = Logger.getLogger(CSRFFilter.class.getSimpleName());

    // methods not to filter
    private static Set<String> methodsWhiteList;

    static {
        methodsWhiteList = new HashSet<>(3);
        methodsWhiteList.add("GET");
        methodsWhiteList.add("HEAD");
        methodsWhiteList.add("OPTIONS");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // get request, response
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        // get session and associated uid
        final HttpSession session = request.getSession();
        final Integer uid = (session != null) ? (Integer) session.getAttribute(Utils.UID) : null;

        // get method
        final String method = request.getMethod();

        // if GET, add the CSRF token
        if (method.equals("GET")) {

            // add CSRF token
            CSRFUtils.addCSRFToken(request, response);
        }

        // if POST, PUT, DELETE -> validate the CSRF token
        if (!methodsWhiteList.contains(method)) {

            // get cookies
            final Cookie[] cookies = request.getCookies();
            String csrfCookie = null;

            // get CSRF cookie
            for (Cookie c : cookies) {
                if (c.getName().equals(CSRFUtils.COOKIE)) {
                    csrfCookie = c.getValue();
                    break;
                }
            }

            // csrf header
            final String csrfHeader = request.getHeader(CSRFUtils.HEADER);

            // expected value
            final String expected = (session != null) ? (String) session.getAttribute(CSRFUtils.SESSION) : null;

            // check CSRF
            if (csrfCookie == null || csrfHeader == null || expected == null ||
                    !csrfCookie.equals(csrfHeader) || !csrfHeader.equals(expected)) {

                // log
                logger.info("[CSRF REJECT] (uid = " + uid + ") {cookie: " + csrfCookie + ", header: " + csrfHeader +
                        ", expected: " + expected + "}: " + request.getRequestURI());

                // delete the cookie if present...
                if (csrfCookie != null) {
                    CSRFUtils.deleteCSRFToken(request, response);
                }

                // abort
                response.setStatus(401);
                return;
            }
        }

        // process request
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
