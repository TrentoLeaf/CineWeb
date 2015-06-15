package tk.trentoleaf.cineweb.rest;

import com.sendgrid.SendGridException;
import tk.trentoleaf.cineweb.annotations.AdminArea;
import tk.trentoleaf.cineweb.annotations.UserArea;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.entities.*;
import tk.trentoleaf.cineweb.exceptions.db.*;
import tk.trentoleaf.cineweb.exceptions.rest.AuthFailedException;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;
import tk.trentoleaf.cineweb.exceptions.rest.ConflictException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.utils.CSRFUtils;
import tk.trentoleaf.cineweb.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

    // invalidate session
    private void invalidateSession() {

        // invalidate session
        final HttpSession session = request.getSession();
        session.invalidate();

        // re-init CSRF protection
        CSRFUtils.addCSRFToken(request, response);
    }

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    @Context
    private UriInfo uriInfo;

    // rest function to do a login
    @POST
    @Path("/login")
    public Response login(@NotNull(message = "Missing email, password") @Valid Auth auth) throws SQLException {

        // try authentication
        boolean success = db.authenticate(auth.getEmail(), auth.getPassword());

        // login ok, get user
        if (success) {
            try {
                final User user = db.getUser(auth.getEmail());

                // login ok, save uid
                final HttpSession session = request.getSession(true);
                session.setAttribute(Utils.UID, user.getUid());

                return Response.ok(new LoginOk(user)).build();

            } catch (UserNotFoundException e) {
                invalidateSession();
                throw new AuthFailedException();
            }
        }

        // login failed
        else {
            invalidateSession();
            throw new AuthFailedException();
        }
    }

    @POST
    @UserArea
    @Path("/logout")
    public Response logout() {

        // invalidate session
        invalidateSession();

        return Response.ok().build();
    }

    @POST
    @UserArea
    @Path("/change-password")
    public Response changePassword(@NotNull(message = "Missing email, oldPassword, newPassword") @Valid ChangePassword change) throws SQLException {

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
    public Response registration(@NotNull(message = "Bad registration request") @Valid Registration registration) throws SQLException {

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
    public ActivateUser confirmUser(@NotNull(message = "Missing code") @Valid ConfirmCode confirmCode) throws SQLException {

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
    public Response forgotPassword(@NotNull(message = "Missing email") @Valid ForgotPassword forgotPassword) throws SQLException {

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
    public Response changePasswordWithCode(@NotNull(message = "Bad changePassword") @Valid ChangePasswordWithCode change) throws SQLException {

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
    public UserDetails getUser() throws NotFoundException, SQLException {

        final HttpSession session = request.getSession(false);
        if (session != null) {
            final Integer uid = (Integer) session.getAttribute(Utils.UID);
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
    public User createUser(@NotNull(message = "Missing user object") User user) throws SQLException {

        // check if user is valid
        if (!user.isValidWithPassword()) {
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
    public User updateUser(@PathParam("id") int id, @NotNull(message = "Missing user object") @Valid User user) throws SQLException {

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
