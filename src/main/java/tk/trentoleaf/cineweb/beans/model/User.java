package tk.trentoleaf.cineweb.beans.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import tk.trentoleaf.cineweb.annotations.hibernate.SafeString;
import tk.trentoleaf.cineweb.beans.rest.in.Registration;
import tk.trentoleaf.cineweb.utils.Utils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * User bean: represent a User.
 */
public class User implements Serializable {

    // default admin user
    public static User FIRST_ADMIN = new User(true, Role.ADMIN, "admin@trentoleaf.tk", "admin", "First", "Admin");

    private int uid;
    private boolean enabled;

    @NotNull(message = "Role cannot be null")
    private Role role;

    @Size(max = 255, message = "Email too long")
    @Email(message = "Email must be a valid email")
    @NotEmpty(message = "Email cannot be null")
    private String email;

    @Size(max = 60, message = "Password too long")
    private String password;

    @Size(max = 100, message = "FirstName too long")
    @SafeString(message = "FirstName cannot be null")
    private String firstName;

    @Size(max = 100, message = "SecondName too long")
    @SafeString(message = "SecondName cannot be null")
    private String secondName;

    @Min(value = 0, message = "Credit must be >= 0")
    private double credit;

    /**
     * Construct an empty user.
     */
    public User() {
    }

    /**
     * Construct a user.
     *
     * @param enabled    True if this user is enabled.
     * @param role       User Role: can be CLIENT or ADMIN.
     * @param email      User email.
     * @param password   User password.
     * @param firstName  User first name.
     * @param secondName User second name.
     */
    public User(boolean enabled, Role role, String email, String password, String firstName, String secondName) {
        this.enabled = enabled;
        this.role = role;
        this.email = email.toLowerCase();
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    /**
     * Construct a user.
     *
     * @param enabled    True if this user is enabled.
     * @param role       User Role: can be CLIENT or ADMIN.
     * @param email      User email.
     * @param password   User password.
     * @param firstName  User first name.
     * @param secondName User second name.
     * @param credit     User initial credit.
     */
    public User(boolean enabled, Role role, String email, String password, String firstName, String secondName, double credit) {
        this.enabled = enabled;
        this.role = role;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
        this.credit = credit;
    }

    /**
     * Construct a new user from a registration object.
     *
     * @param registration The object representing the user registration.
     */
    public User(Registration registration) {
        this(false, Role.CLIENT, registration.getEmail(), registration.getPassword(),
                registration.getFirstName(), registration.getSecondName());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email.toLowerCase();
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void removePassword() {
        this.password = null;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public boolean isValidWithPassword() {
        return Utils.isValid(this) && StringUtils.isNotEmpty(password);
    }

    public void addCredit(double credit) {
        this.credit += credit;
    }

    @Override
    public int hashCode() {
        return uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (uid != user.uid) return false;
        if (enabled != user.enabled) return false;
        if (Double.compare(user.credit, credit) != 0) return false;
        if (role != user.role) return false;
        if (!email.equalsIgnoreCase(user.email)) return false;
        if (!firstName.equals(user.firstName)) return false;
        return secondName.equals(user.secondName);
    }
}
