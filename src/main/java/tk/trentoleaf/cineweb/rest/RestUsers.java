package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.exceptions.ConstrainException;
import tk.trentoleaf.cineweb.exceptions.UserNotFoundException;
import tk.trentoleaf.cineweb.exceptions.WrongPasswordException;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.rest.entities.Auth;
import tk.trentoleaf.cineweb.rest.entities.ChangePassword;
import tk.trentoleaf.cineweb.rest.exceptions.AuthFailedException;
import tk.trentoleaf.cineweb.rest.exceptions.BadRequestException;
import tk.trentoleaf.cineweb.rest.exceptions.ConflictException;
import tk.trentoleaf.cineweb.rest.exceptions.NotFoundException;

import javax.servlet.ServletContext;
import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/users")
public class RestUsers {

    // db singleton
    private DB db = DB.instance();

    private void removeCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    // rest function to do a login
    @POST
    @Path("/login")
    public Response login(@Context HttpServletRequest request, Auth auth) throws SQLException {

        // check payload
        if (auth == null || !auth.isValid()) {
            throw new BadRequestException("Missing email or password");
        }

        // try authentication
        boolean success = db.authenticate(auth.getEmail(), auth.getPassword());

        // login ok, get user
        if (success) {
            try {
                final User user = db.getUser(auth.getEmail());

                // login ok, create session
                final HttpSession session = request.getSession();
                session.setAttribute("user", user);

                return Response.ok().build();

            } catch (UserNotFoundException e) {
                throw new AuthFailedException();
            }
        }

        // login failed
        else {
            throw new AuthFailedException();
        }
    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request, @Context HttpServletResponse response) {

        // invalidate the session
        final HttpSession session = request.getSession();
        session.invalidate();

        // remove cookie popcorn and JSESSIONID
        removeCookie(response, "JSESSIONID");
        removeCookie(response, "popcorn");

        return Response.ok().build();
    }

    @POST
    @Path("/change-password")
    public Response changePassword(ChangePassword change) throws SQLException {

        if (change == null) {
            throw new BadRequestException("Missing email, oldPassword or newPassword");
        }

        // try to change the password
        try {
            db.changePasswordWithOldPassword(change.getEmail(), change.getOldPassword(), change.getNewPassword());
            return Response.ok().build();
        } catch (UserNotFoundException e1) {
            throw NotFoundException.USER_NOT_FOUND;
        } catch (WrongPasswordException e2) {
            throw new AuthFailedException();
        }
    }

    @GET
    @Path("/{id}")
    public Object getUser(@PathParam("id") int id) {
        // TODO -> waiting for DB api
        return id;
    }

    @GET
    public List<User> getUsers() throws SQLException {
        return db.getUsers();
    }

    @POST
    public User createUser(@Context HttpServletRequest request, User user) throws SQLException {

        // check if user is valid
        if (user == null || !user.isValidWithPassword()) {
            throw BadRequestException.BAD_USER;
        }

        // create user
        try {
            db.createUser(user);
            user.removePassword();
            return user;
        } catch (ConstrainException e) {
            throw ConflictException.EMAIL_IN_USE;
        }

    }

    @PUT
    @Path("/{id}")
    public User updateUser(@PathParam("id") int id, User user) throws SQLException {

        // check if user is valid
        if (user == null || !user.isValid()) {
            throw BadRequestException.BAD_USER;
        }

        // create user
        try {
            user.setUid(id);
            db.updateUser(user);
            return user;
        } catch (ConstrainException e) {
            throw ConflictException.EMAIL_IN_USE;
        } catch (UserNotFoundException ex) {
            throw NotFoundException.USER_NOT_FOUND;
        }

    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") int id) throws SQLException {

        try {
            db.deleteUser(id);
            return Response.ok().build();
        } catch (UserNotFoundException e) {
            throw NotFoundException.USER_NOT_FOUND;
        }

    }

}
