package tk.trentoleaf.cineweb.filters.all;

import tk.trentoleaf.cineweb.beans.model.Role;
import tk.trentoleaf.cineweb.db.UsersDB;
import tk.trentoleaf.cineweb.utils.Utils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to serve html admin pages only to admin users.
 */
@WebFilter(urlPatterns = "/partials/admin/*")
public class AdminPartialsFilter implements Filter {

    // database
    private UsersDB usersDB = UsersDB.instance();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        // get request and response
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        // check if a logged user
        final HttpSession session = request.getSession(false);
        final Integer uid = (session != null) ? (Integer) session.getAttribute(Utils.UID) : null;

        // check the role
        final Role role = (uid != null) ? usersDB.getUserRoleIfEnabled(uid) : null;

        // if not admin -> drop request
        if (role != Role.ADMIN) {

            // return HTTP 401
            response.reset();
            response.setStatus(401);
            return;
        }

        // if here -> admin logged... do nothing
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
