package tk.trentoleaf.cineweb.filters;

import tk.trentoleaf.cineweb.utils.CsrfUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

            // create session if not exists
            final HttpSession session = request.getSession(true);

            // if session new -> add csrf cookie
            if (session.isNew()) {

                // generate CSRF nonce...
                final String csrfValue = CsrfUtils.randomString();

                // if no CSRF cookie
                final Cookie csrfCookie = new Cookie(CsrfUtils.COOKIE, csrfValue);
                csrfCookie.setPath("/");
                response.addCookie(csrfCookie);

                // save value in session
                session.setAttribute(CsrfUtils.SESSION, csrfValue);
            }
        }

        // TODO: on post, put, delete -> change csrf value...

        // process request
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
