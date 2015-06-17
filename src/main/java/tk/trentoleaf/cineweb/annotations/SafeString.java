package tk.trentoleaf.cineweb.annotations;

import org.hibernate.validator.constraints.SafeHtml;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@NotNull
@SafeHtml
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface SafeString {

    String message() default "The String cannot be null nor contain XML tags";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
