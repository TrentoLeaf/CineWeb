package tk.trentoleaf.cineweb.filters;

import tk.trentoleaf.cineweb.utils.GZIPResponseWrapper;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@WebFilter(urlPatterns = {"*.html", "*.css", "*.js", "*.svg", "*.eot", "*.ttf"})
public class GZIPFilter implements Filter {

    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        // get request and response
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        // get supported encodings
        final String encodings = request.getHeader("accept-encoding");

        // gzip supported... compress
        if (encodings != null && encodings.contains("gzip")) {

            // wrap response
            GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response);

            // filter chain
            chain.doFilter(req, wrappedResponse);
            wrappedResponse.finishResponse();

            return;
        }

        // gzip not supported... do nothing
        chain.doFilter(req, res);
    }

    public void destroy() {
    }
}
