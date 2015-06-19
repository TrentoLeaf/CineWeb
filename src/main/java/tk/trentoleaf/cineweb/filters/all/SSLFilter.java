package tk.trentoleaf.cineweb.filters.all;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * This filter forces the use of HTTPS on every request. This behaviour can be enabled through the
 * Environment Variable "PRODUCTION" set to true. To disable this filter, omit the Environment Variable
 * "PRODUCTION" or set it to "false".
 */
@WebFilter(urlPatterns = "/*")
public class SSLFilter implements Filter {
    private Logger logger = Logger.getLogger(SSLFilter.class.getSimpleName());

    // is this filter enabled?
    private boolean enabled = false;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        // check if the filter has been activated
        final String production = System.getenv("PRODUCTION");
        enabled = production != null && Boolean.parseBoolean(production);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        // get request and response
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        // add Strict-Transport-Security header
        // this instructs the browser not to send any data through plain HTTP (with no SSL)
        if (enabled) {
            response.addHeader("Strict-Transport-Security", "max-age=31536000");
        }

        // if request not secure -> redirect (HTTP 301) and set HTTPS
        if (enabled && !request.isSecure()) {

            // get requested url
            final String url = request.getRequestURL().toString()
                    .replaceFirst("http", "https")
                    .replaceFirst(":8080", ":8443");

            // log
            logger.info("FORCE SSL -> FROM: " + request.getRequestURL() + " TO: " + url);

            // redirect
            response.reset();
            response.sendRedirect(url);
            return;
        }

        // if already https -> process request
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
