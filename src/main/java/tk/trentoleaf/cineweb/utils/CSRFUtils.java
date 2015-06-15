package tk.trentoleaf.cineweb.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;

public class CSRFUtils {

    public static final String COOKIE = "XSRF-TOKEN";
    public static final String HEADER = "X-XSRF-TOKEN";
    public static final String SESSION = "csrf";

    private static SecureRandom random = new SecureRandom();

    public static String randomString() {
        return new BigInteger(150, random).toString(32);
    }

    public static void addCSRFToken(HttpServletRequest request, HttpServletResponse response) {

        // create session if not exists
        final HttpSession session = request.getSession(true);

        // if session new -> add csrf cookie
        if (session.isNew()) {

            // generate CSRF nonce...
            final String csrfValue = CSRFUtils.randomString();

            // if no CSRF cookie
            final Cookie csrfCookie = new Cookie(CSRFUtils.COOKIE, csrfValue);
            csrfCookie.setPath("/");
            response.addCookie(csrfCookie);

            // save value in session
            session.setAttribute(CSRFUtils.SESSION, csrfValue);
        }
    }

}