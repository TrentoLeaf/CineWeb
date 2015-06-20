package tk.trentoleaf.cineweb.annotations.rest;

import tk.trentoleaf.cineweb.filters.rest.GZIPInterceptor;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a Jersey custom annotation used to mark the REST methods to be compressed with GZIP.
 *
 * @see GZIPInterceptor
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Compress {
}