package tk.trentoleaf.cineweb.rest.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;

public class CsrfUtils {

    private static SecureRandom random = new SecureRandom();

    private static Cookie getCsrfCookie(String value) {
        final Cookie cookie = new Cookie("XSRF-TOKEN", value);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        return cookie;
    }

    private static String randomString() {
        return new BigInteger(150, random).toString(64);
    }

    public static void protect(HttpSession session, HttpServletResponse response) {
        final String value = randomString();
        response.addCookie(CsrfUtils.getCsrfCookie(value));
        session.setAttribute("csrf", value);
    }

}