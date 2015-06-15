package tk.trentoleaf.cineweb.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import tk.trentoleaf.cineweb.entities.Registration;
import tk.trentoleaf.cineweb.utils.Utils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class User implements Serializable {

    private int uid;
    private boolean enabled;

    @NotNull
    private Role role;

    @Email(message = "Email must be a valid email")
    @NotEmpty(message = "Email cannot be null")
    private String email;

    private String password;

    @NotEmpty(message = "FirstName cannot be null")
    private String firstName;

    @NotEmpty(message = "SecondName cannot be null")
    private String secondName;

    @Min(value = 0, message = "Credit must be >= 0")
    private double credit;

    public static User FIRST_ADMIN = new User(true, Role.ADMIN, "admin@trentoleaf.tk", "admin", "First", "Admin");

    public User() {
    }

    public User(boolean enabled, Role role, String email, String password, String firstName, String secondName) {
        this.enabled = enabled;
        this.role = role;
        this.email = email.toLowerCase();
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public User(Registration registration) {
        this(false, Role.CLIENT, registration.getEmail(), registration.getPassword(), registration.getFirstName(), registration.getSecondName());
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

    public boolean isValid() {
//        return role != null && StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(firstName)
//                && StringUtils.isNotEmpty(secondName) && credit >= 0;
        return Utils.isValid(this);
    }

    public boolean isValidWithPassword() {
        return isValid() && StringUtils.isNotEmpty(password);
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
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        return !(secondName != null ? !secondName.equals(user.secondName) : user.secondName != null);
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", enabled=" + enabled +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", credit=" + credit +
                '}';
    }
}
