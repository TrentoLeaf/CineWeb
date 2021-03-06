package tk.trentoleaf.cineweb.annotations.rest;

import tk.trentoleaf.cineweb.filters.rest.UsersFilter;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a Jersey custom annotation used to mark the REST methods restricted to an
 * authenticated user (client or admin). The validation is done in the AdminFilter class.
 *
 * @see UsersFilter
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface UserArea {
}