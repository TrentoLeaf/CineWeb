package tk.trentoleaf.cineweb.filters;

import java.math.BigInteger;
import java.security.SecureRandom;

public class CsrfUtils {

    public static final String COOKIE = "XSRF-TOKEN";
    public static final String HEADER = "X-XSRF-TOKEN";
    public static final String SESSION = "csrf";

    private static SecureRandom random = new SecureRandom();

    public static String randomString() {
        return new BigInteger(150, random).toString(32);
    }


}