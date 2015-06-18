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

    // NB: heroku compatibility handled in Jetty configuration (heroku/Main.java)

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

        // check if to force https
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

        // add Strict-Transport-Security header
        if (enabled) {
            response.addHeader("Strict-Transport-Security", "max-age=31536000");
        }

        // if already https -> process request
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
