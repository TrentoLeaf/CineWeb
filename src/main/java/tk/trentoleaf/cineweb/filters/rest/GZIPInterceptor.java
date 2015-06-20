package tk.trentoleaf.cineweb.filters.rest;

import tk.trentoleaf.cineweb.annotations.rest.Compress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This Jersey Interceptor compress the HTTP responses of the methods marked with the @Compress annotation.
 * It uses GZIP to compress the responses.
 *
 * @see tk.trentoleaf.cineweb.annotations.rest.Compress
 */
@Compress
@Provider
public class GZIPInterceptor implements WriterInterceptor {

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {

        // get supported encodings
        final String encodings = request.getHeader("accept-encoding");

        // gzip supported... compress
        if (encodings != null && encodings.contains("gzip")) {

            // compress response
            final OutputStream outputStream = context.getOutputStream();
            context.setOutputStream(new GZIPOutputStream(outputStream));

            // add encoding type
            response.addHeader("Content-Encoding", "gzip");
            response.addHeader("Vary", "Accept-Encoding");
        }

        context.proceed();
    }
}