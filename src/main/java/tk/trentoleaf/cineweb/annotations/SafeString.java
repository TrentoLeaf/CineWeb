package tk.trentoleaf.cineweb.annotations;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotNull
@NotEmpty
@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
public @interface SafeString {

    String message() default "The String cannot be null nor contain HTML tags";

    @SuppressWarnings("unused")
    Class<?>[] groups() default {};

    @SuppressWarnings("unused")
    Class<? extends Payload>[] payload() default {};
}
