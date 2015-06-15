package tk.trentoleaf.cineweb.filters;

import tk.trentoleaf.cineweb.utils.CSRFUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class AddCSRFCookieFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // get request, response, session
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        // check method
        final String method = request.getMethod();
        if (method.equals("GET")) {

            // add CSRF token
            CSRFUtils.addCSRFToken(request, response);
        }

        // TODO: on post, put, delete -> change csrf value...

        // process request
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
