package tk.trentoleaf.cineweb.model;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private Role role;
    private String email;
    private transient String password;      // transient = non viene serializzato
    private String firstName;
    private String secondName;
    private double credit;

    public User() {
    }

    public User(Role role, String email, String password, String firstName, String secondName) {
        this.role = role;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", credit=" + credit +
                '}';
    }
}
