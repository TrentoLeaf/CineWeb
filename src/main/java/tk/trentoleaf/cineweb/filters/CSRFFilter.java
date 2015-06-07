package tk.trentoleaf.cineweb.filters;

import tk.trentoleaf.cineweb.utils.CsrfUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@WebFilter(urlPatterns = "/*")
public class CSRFFilter implements Filter {
    private Logger logger = Logger.getLogger(CSRFFilter.class.getSimpleName());

    // methods not to filter
    private static Set<String> methodsWhiteList;

    static {
        methodsWhiteList = new HashSet<>(3);
        methodsWhiteList.add("GET");
        methodsWhiteList.add("HEAD");
        methodsWhiteList.add("OPTIONS");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // get request, response
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession();

        // get method
        final String method = request.getMethod();

        // check method
        if (!methodsWhiteList.contains(method)) {

            // get cookies
            final Cookie[] cookies = request.getCookies();
            String csrfCookie = null;

            // get CSRF cookie
            for (Cookie c : cookies) {
                if (c.getName().equals(CsrfUtils.COOKIE)) {
                    csrfCookie = c.getValue();
                    break;
                }
            }

            // csrf header
            final String csrfHeader = request.getHeader(CsrfUtils.HEADER);

            // expected value
            final String expected = (String) session.getAttribute(CsrfUtils.SESSION);

            // check CSRF
            if (csrfCookie == null || csrfHeader == null || expected == null || !csrfCookie.equals(csrfHeader) || !csrfHeader.equals(expected)) {

                // abort
                logger.info("[CSRF REJECT] {cookie: " + csrfCookie + ", header: " + csrfHeader + ", expected: " + expected + "}: " + request.getRequestURI());

                // TODO: enable
                // response.reset();
                // response.setStatus(401);
                // return;
            }
        }

        // process request
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
