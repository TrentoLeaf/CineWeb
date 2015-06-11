package tk.trentoleaf.cineweb.rest;

import com.sendgrid.SendGridException;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.exceptions.db.*;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.annotations.AdminArea;
import tk.trentoleaf.cineweb.annotations.UserArea;
import tk.trentoleaf.cineweb.entities.*;
import tk.trentoleaf.cineweb.exceptions.rest.AuthFailedException;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;
import tk.trentoleaf.cineweb.exceptions.rest.ConflictException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@Path("/users")
public class RestUsers {
    private Logger logger = Logger.getLogger(RestUsers.class.getSimpleName());

    // db singleton
    private DB db = DB.instance();

    // remove a given cookie
    private void removeCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    // remove all session cookies
    private void removeSession(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        removeCookie(response, "JSESSIONID");
        removeCookie(response, "popcorn");
    }

    // rest function to do a login
    @POST
    @Path("/login")
    public Response login(@Context HttpServletRequest request, @Context HttpServletResponse response, Auth auth) throws SQLException {

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

                // login ok, save uid
                final HttpSession session = request.getSession(true);
                session.setAttribute("uid", user.getUid());

                return Response.ok(new LoginOk(user)).build();

            } catch (UserNotFoundException e) {
                removeSession(request.getSession(), response);
                throw new AuthFailedException();
            }
        }

        // login failed
        else {
            removeSession(request.getSession(), response);
            throw new AuthFailedException();
        }
    }

    @POST
    @UserArea
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request, @Context HttpServletResponse response) {

        // invalidate session and remove cookies
        removeSession(request.getSession(), response);

        return Response.ok().build();
    }

    @POST
    @UserArea
    @Path("/change-password")
    public Response changePassword(ChangePassword change) throws SQLException {

        if (change == null || !change.isValid()) {
            throw new BadRequestException("Missing email, oldPassword or newPassword");
        }

        // try to change the password
        try {
            db.changePasswordWithOldPassword(change.getEmail(), change.getOldPassword(), change.getNewPassword());
            return Response.ok().build();
        } catch (WrongPasswordException | UserNotFoundException e) {
            throw new AuthFailedException();
        }
    }

    @POST
    @Path("/registration")
    public Response registration(@Context UriInfo uriInfo, Registration registration) throws SQLException {

        // validate registration
        if (registration == null || !registration.isValid()) {
            throw new BadRequestException("Bad registration request");
        }

        // try to add the user
        try {

            // add user to db
            final User user = new User(registration);
            db.createUser(user);

            try {
                // request verification code
                final String code = db.requestConfirmationCode(user.getUid());

                // send email
                EmailSender.instance().sendRegistrationEmail(uriInfo.getRequestUri(), user, code);

            } catch (SendGridException e) {
                logger.severe("Cannot send verification email to: " + user.getEmail() + " --> " + e);
            }
        } catch (ConstrainException | UserNotFoundException e) {
            throw ConflictException.EMAIL_IN_USE;
        }

        return Response.ok().build();
    }

    @POST
    @Path("/confirm")
    public ActivateUser confirmUser(ConfirmCode confirmCode) throws SQLException {

        // validate code
        if (confirmCode == null || !confirmCode.isValid()) {
            throw new BadRequestException("Bad confirmation code");
        }

        // try to confirm the user
        try {
            db.confirmUser(confirmCode.getCode());
            return new ActivateUser(0, "User activated");
        } catch (UserNotFoundException e) {
            throw NotFoundException.GENERIC;
        } catch (UserAlreadyActivatedException e) {
            return new ActivateUser(1, "User already activated");
        }
    }

    @POST
    @Path("/forgot-password")
    public Response forgotPassword(@Context UriInfo uriInfo, ForgotPassword forgotPassword) throws SQLException {

        // validate email
        if (forgotPassword == null || !forgotPassword.isValid()) {
            throw new BadRequestException("Missing email");
        }

        // check if user exists
        try {
            final User user = db.getUser(forgotPassword.getEmail());

            // check if enabled
            if (!user.isEnabled()) {
                throw new UserNotFoundException();
            }

            // if here -> request password reset
            final String code = db.requestResetPassword(user.getUid());

            // send email
            try {
                EmailSender.instance().sendRecoverPasswordEmail(uriInfo.getRequestUri(), user, code);
            } catch (SendGridException e) {
                logger.severe("Cannot send password recover email to: " + user.getEmail() + " --> " + e);
            }

            // ok
            return Response.ok().build();

        } catch (UserNotFoundException e) {
            throw NotFoundException.USER_NOT_FOUND;
        }
    }

    @POST
    @Path("/change-password-code")
    public Response changePasswordWithCode(ChangePasswordWithCode change) throws SQLException {

        if (change == null || !change.isValid()) {
            throw new BadRequestException("Missing email, code or newPassword");
        }

        // try to change the password
        try {
            db.changePasswordWithCode(change.getEmail(), change.getCode(), change.getNewPassword());
            return Response.ok().build();
        } catch (UserNotFoundException | WrongCodeException e) {
            throw new AuthFailedException();
        }
    }

    @GET
    @UserArea
    @Path("/me")
    public UserDetails getUser(@Context HttpServletRequest request) throws NotFoundException, SQLException {

        final HttpSession session = request.getSession(false);
        if (session != null) {
            final Integer uid = (Integer) session.getAttribute("uid");
            try {
                final User current = (uid != null) ? db.getUser(uid) : null;
                if (current != null) {
                    return new UserDetails(current);
                }
            } catch (UserNotFoundException e) {
                // nothing, see later
            }
        }

        // if here -> no user logged in
        throw NotFoundException.USER_NOT_FOUND;
    }

    @GET
    @AdminArea
    public List<User> getUsers() throws SQLException {
        return db.getUsers();
    }

    @GET
    @AdminArea
    @Path("/{id}")
    public User getUser(@PathParam("id") int uid) throws SQLException {
        try {
            return db.getUser(uid);
        } catch (UserNotFoundException e) {
            throw NotFoundException.USER_NOT_FOUND;
        }
    }

    @POST
    @AdminArea
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
    @AdminArea
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
    @AdminArea
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
