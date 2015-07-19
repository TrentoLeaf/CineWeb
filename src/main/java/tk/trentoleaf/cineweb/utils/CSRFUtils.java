package tk.trentoleaf.cineweb.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * This class contains some utilities used to set a random CSRF token and store it in the current session
 * for the following checks.
 *
 * @see tk.trentoleaf.cineweb.filters.all.CSRFFilter
 */
public class CSRFUtils {

    // constants
    public static final String COOKIE = "XSRF-TOKEN";
    public static final String HEADER = "X-XSRF-TOKEN";
    public static final String SESSION = "csrf";

    // random number generator, used to generate the token value
    private static SecureRandom random = new SecureRandom();

    // generate a random string
    public static String randomString() {
        return new BigInteger(150, random).toString(32);
    }

    // check if there is a CSRF token
    private static boolean isCSRFTokenPresent(HttpServletRequest request) {

        // get cookies
        final Cookie[] cookies = request.getCookies();

        // look for the correct cookie
        for (Cookie c : cookies) {
            if (c.getName().equals(COOKIE)) {
                return true;
            }
        }

        // if here -> no token found
        return false;
    }

    // generate and store a random CSRF token on new sessions
    public static void addCSRFToken(HttpServletRequest request, HttpServletResponse response) {

        // create session if not exists
        final HttpSession session = request.getSession(true);

        // if session new or missing token -> add csrf cookie
        if (session.isNew() || !isCSRFTokenPresent(request)) {

            // generate CSRF nonce...
            final String csrfValue = CSRFUtils.randomString();

            // if no CSRF cookie
            final Cookie csrfCookie = new Cookie(COOKIE, csrfValue);
            csrfCookie.setPath("/");

            // check if request over https
            // and secure the cookie
            if (request.isSecure()) {
                csrfCookie.setSecure(true);
            }

            // add the cookie to the response
            response.addCookie(csrfCookie);

            // save the token value in the current session
            session.setAttribute(SESSION, csrfValue);
        }
    }

    // delete the CSRF token if present
    public static void deleteCSRFToken(HttpServletRequest request, HttpServletResponse response) {

        // get the session if exists
        final HttpSession session = request.getSession(false);

        // delete the saved value in the session
        if (session != null) {
            session.setAttribute(SESSION, null);
        }

        // delete the cookie if present
        final Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals(COOKIE)) {
                c.setMaxAge(0);
                response.addCookie(c);
            }
        }
    }

}