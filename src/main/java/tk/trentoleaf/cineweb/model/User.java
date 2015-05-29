package tk.trentoleaf.cineweb.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class User implements Serializable {

    private int uid;
    private Role role;
    private String email;
    private String password;
    private String firstName;
    private String secondName;
    private double credit;

    public User() {
    }

    public User(Role role, String email, String password, String firstName, String secondName) {
        this.role = role;
        this.email = email.toLowerCase();
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
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
        return role != null && StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(firstName)
                && StringUtils.isNotEmpty(secondName) && credit >= 0;
    }

    public boolean isValidWithPassword() {
        return isValid() && StringUtils.isNotEmpty(password);
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
                ", role=" + role +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", credit=" + credit +
                '}';
    }
}
