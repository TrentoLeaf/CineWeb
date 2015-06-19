package tk.trentoleaf.cineweb.utils;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * This class contains some utilities used in all the web application.
 */
public class Utils {

    /**
     * Name of the userID used in the HTTP session map to store the current user ID.
     */
    public static final String UID = "uid";

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    /**
     * Returns a singleton Hibernate Validator.
     *
     * @return The Hibernate Validator instance.
     */
    public static Validator getValidator() {
        if (validatorFactory == null) {
            validatorFactory = Validation.buildDefaultValidatorFactory();
        }
        if (validator == null) {
            validator = validatorFactory.getValidator();
        }
        return validator;
    }

    /**
     * Checks if an object is valid using the Hibernate Validator.
     *
     * @param object Object to validate
     * @return True if valid, False otherwise.
     */
    public static boolean isValid(Object object) {
        return getValidator().validate(object).isEmpty();
    }

    /**
     * Extract protocol and domain from a given URI.
     *
     * @param uri The URI from where to extract protocol and domain.
     * @return A string in this format: protocol://domain
     */
    public static String uriToRoot(URI uri) {
        try {
            final URL url = uri.toURL();
            final String complete = url.toString();
            final String path = url.getPath();

            return complete.replace(path, "");

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
