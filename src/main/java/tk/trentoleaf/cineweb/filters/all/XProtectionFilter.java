package tk.trentoleaf.cineweb.filters.all;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter add some security header to every HTTP response.
 *
 * @see <a href="https://www.owasp.org/index.php/List_of_useful_HTTP_headers">OWASP - List of useful headers</a>
 */
@WebFilter(urlPatterns = "/*")
public class XProtectionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        // get response
        final HttpServletResponse response = (HttpServletResponse) res;

        // add header
        response.addHeader("X-Frame-Options", "DENY");
        response.addHeader("X-Content-Type-Options", "nosniff");
        response.addHeader("X-XSS-Protection", "1; mode=block");

        // process request
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
