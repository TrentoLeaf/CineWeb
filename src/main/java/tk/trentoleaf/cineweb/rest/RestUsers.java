package tk.trentoleaf.cineweb.rest;

import com.sendgrid.SendGridException;
import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.annotations.rest.Compress;
import tk.trentoleaf.cineweb.annotations.rest.UserArea;
import tk.trentoleaf.cineweb.beans.model.TopClient;
import tk.trentoleaf.cineweb.beans.model.User;
import tk.trentoleaf.cineweb.beans.rest.in.*;
import tk.trentoleaf.cineweb.beans.rest.out.ActivateUser;
import tk.trentoleaf.cineweb.beans.rest.out.UserDetails;
import tk.trentoleaf.cineweb.db.StatisticsDB;
import tk.trentoleaf.cineweb.db.UsersDB;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.exceptions.db.UniqueViolationException;
import tk.trentoleaf.cineweb.exceptions.db.UserAlreadyActivatedException;
import tk.trentoleaf.cineweb.exceptions.db.UserNotFoundException;
import tk.trentoleaf.cineweb.exceptions.db.WrongCredentialsException;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;
import tk.trentoleaf.cineweb.exceptions.rest.ConflictException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;
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

/**
 * Users end point. Handles users registration, password resets, authentication. Implements CRUD operations on the users.
 */
@Path("/users")
public class RestUsers {
    private Logger logger = Logger.getLogger(RestUsers.class.getSimpleName());

    // DB singleton
    private UsersDB usersDB = UsersDB.instance();
    private StatisticsDB statisticsDB = StatisticsDB.instance();

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
    public Response login(@NotNull(message = "Missing email, password") @Valid Auth auth) {
        try {

            // try authentication
            boolean success = usersDB.authenticate(auth.getEmail(), auth.getPassword());

            // login ok, get user
            if (success) {

                // retrieve the user
                final User user = usersDB.getUser(auth.getEmail());

                // login ok, save uid
                final HttpSession session = request.getSession(true);
                session.setAttribute(Utils.UID, user.getUid());

                return Response.ok(new UserDetails(usersDB.getUser(user.getUid()))).build();
            }

            // login failed
            else {
                invalidateSession();
                throw NotFoundException.wrongCredentials();
            }

        } catch (UserNotFoundException e) {
            invalidateSession();
            throw NotFoundException.wrongCredentials();
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
    public Response changePassword(@NotNull(message = "Missing email, oldPassword, newPassword") @Valid ChangePassword change) {

        // try to change the password
        try {
            usersDB.changePasswordWithOldPassword(change.getEmail(), change.getOldPassword(), change.getNewPassword());
            return Response.ok().build();
        } catch (WrongCredentialsException e) {
            throw NotFoundException.wrongCredentials();
        }
    }

    @POST
    @Path("/registration")
    public Response registration(@NotNull(message = "Bad registration request") @Valid Registration registration) {

        // try to add the user
        try {

            // add user to usersDB
            final User user = new User(registration);
            usersDB.createUser(user);

            // send the email
            try {
                // request verification code
                final String code = usersDB.requestConfirmationCode(user.getUid());

                // send email
                EmailSender.instance().sendRegistrationEmail(uriInfo.getRequestUri(), user, code);

            } catch (SendGridException | UserNotFoundException e) {
                logger.severe("Cannot send verification email to: " + user.getEmail() + " --> " + e);
            }

        } catch (UniqueViolationException e) {
            throw ConflictException.emailInUse();
        }

        return Response.ok().build();
    }

    @POST
    @Path("/confirm")
    public ActivateUser confirmUser(@NotNull(message = "Missing code") @Valid ConfirmCode confirmCode) {

        // try to confirm the user
        try {
            usersDB.confirmUser(confirmCode.getCode());
            return new ActivateUser(0, "User activated");
        } catch (UserNotFoundException e) {
            throw new NotFoundException();
        } catch (UserAlreadyActivatedException e) {
            return new ActivateUser(1, "User already activated");
        }
    }

    @POST
    @Path("/forgot-password")
    public Response forgotPassword(@NotNull(message = "Missing email") @Valid ForgotPassword forgotPassword) {

        // check if user exists
        try {
            final User user = usersDB.getUser(forgotPassword.getEmail());

            // check if enabled
            if (!user.isEnabled()) {
                throw new UserNotFoundException();
            }

            // if here -> request password reset
            final String code = usersDB.requestResetPassword(user.getUid());

            // send email
            try {
                EmailSender.instance().sendRecoverPasswordEmail(uriInfo.getRequestUri(), user, code);
            } catch (SendGridException e) {
                logger.severe("Cannot send password recover email to: " + user.getEmail() + " --> " + e);
            }

            // ok
            return Response.ok().build();

        } catch (UserNotFoundException e) {
            throw new NotFoundException();
        }
    }

    @POST
    @Path("/change-password-code")
    public Response changePasswordWithCode(@NotNull(message = "Bad changePassword") @Valid ChangePasswordWithCode change) {

        // try to change the password
        try {
            usersDB.changePasswordWithCode(change.getEmail(), change.getCode(), change.getNewPassword());
            return Response.ok().build();
        } catch (WrongCredentialsException e) {
            throw NotFoundException.wrongCredentials();
        }
    }

    @GET
    @UserArea
    @Path("/me")
    public UserDetails getUser() throws SQLException {

        // get current session
        final HttpSession session = request.getSession(false);
        assert session != null;

        // get logged user
        final Integer uid = (Integer) session.getAttribute(Utils.UID);
        assert uid != null;

        // return the current user
        try {
            final User current = usersDB.getUser(uid);
            return new UserDetails(current);
        } catch (UserNotFoundException e) {
            throw new NotFoundException();
        }
    }

    @GET
    @AdminArea
    @Compress
    public List<User> getUsers() {
        return usersDB.getUsers();
    }

    @GET
    @AdminArea
    @Path("/{id}")
    public User getUser(@PathParam("id") int uid) {
        try {
            return usersDB.getUser(uid);
        } catch (UserNotFoundException e) {
            throw new NotFoundException();
        }
    }

    @GET
    @AdminArea
    @Compress
    @Path("/top")
    public List<TopClient> getTopClients() {
        return statisticsDB.getTopClients();
    }

    @POST
    @AdminArea
    public User createUser(@NotNull(message = "Missing user object") @Valid User user) {

        // check if user is valid
        if (!user.isValidWithPassword()) {
            throw new BadRequestException("User not valid");
        }

        // create user
        try {
            usersDB.createUser(user);
            user.removePassword();
            return user;
        } catch (UniqueViolationException e) {
            throw ConflictException.emailInUse();
        }
    }

    @PUT
    @AdminArea
    @Path("/{id}")
    public User updateUser(@PathParam("id") int id, @NotNull(message = "Missing user object") @Valid User user) {

        // update user
        try {
            user.setUid(id);
            usersDB.updateUser(user);
            return user;
        } catch (UniqueViolationException e) {
            throw ConflictException.emailInUse();
        } catch (UserNotFoundException ex) {
            throw new NotFoundException();
        }
    }

    @DELETE
    @AdminArea
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") int id) {
        try {
            usersDB.deleteUser(id);
            return Response.ok().build();
        } catch (UserNotFoundException e) {
            throw new NotFoundException();
        }
    }

}
