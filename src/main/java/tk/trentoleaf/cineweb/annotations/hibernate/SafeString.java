package tk.trentoleaf.cineweb.annotations.hibernate;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This is an Hibernate Validator custom annotation used to validate strings.
 * A String marked as @SafeString must be not empty and cannot contain XML tags.
 */
@NotNull
@NotEmpty
@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
public @interface SafeString {

    String message() default "The String cannot be null nor contain HTML tags";

    @SuppressWarnings("unused") Class<?>[] groups() default {};

    @SuppressWarnings("unused") Class<? extends Payload>[] payload() default {};
}
