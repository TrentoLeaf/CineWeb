package tk.trentoleaf.cineweb.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebFilter(urlPatterns = "/*")
public class SSLFilter implements Filter {
    private Logger logger = Logger.getLogger(SSLFilter.class.getSimpleName());

    // heroku compatibility
    // heroku router handles SSL, communication between heroku router and this app is always in HTTPS
    // heroku set this HTTP header to give the original used protocol
    private static final String X_FORWARDED_PROTO = "x-forwarded-proto";

    // is this filter enabled?
    private boolean enabled = false;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        final String production = System.getenv("PRODUCTION");
        enabled = production != null && Boolean.parseBoolean(production);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // get request and response
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        // check header
        final String protocol = request.getHeader(X_FORWARDED_PROTO);
        final boolean xForwarded = (protocol != null && protocol.contains("https"));

        // check if to force https
        if (enabled && (xForwarded || (protocol == null && !request.isSecure()))) {

            // get requested url
            final String url = request.getRequestURL().toString()
                    .replaceFirst("http", "https")
                    .replaceFirst(":8080", ":8443");

            // log
            logger.info("FORCE SSL FROM: " + request.getRequestURL() + " + TO: " + url);

            // redirect
            response.reset();
            response.sendRedirect(url);
            return;
        }

        // process request
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
