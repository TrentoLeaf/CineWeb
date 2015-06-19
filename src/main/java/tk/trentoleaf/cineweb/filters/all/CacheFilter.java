package tk.trentoleaf.cineweb.filters.all;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter controls the HTTP cache. Instructs the browser to re-validate every HTTP request.
 *
 * @see <a href="https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching">HTTP caching - Google Guide</a>
 */
@WebFilter(urlPatterns = "/*")
public class CacheFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        // get response
        final HttpServletResponse response = (HttpServletResponse) res;

        // add header
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Pragma", "no-cache");

        // process request
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
