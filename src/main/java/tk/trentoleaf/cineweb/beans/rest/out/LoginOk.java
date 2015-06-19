package tk.trentoleaf.cineweb.beans.rest.out;

import tk.trentoleaf.cineweb.beans.model.Role;
import tk.trentoleaf.cineweb.beans.model.User;

import java.io.Serializable;

public class LoginOk implements Serializable {

    private String email;
    private String name;
    private Role role;

    private LoginOk(String email, String name, Role role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public LoginOk(User user) {
        this(user.getEmail(), user.getSecondName() + " " + user.getFirstName(), user.getRole());
    }

    @Override
    public String toString() {
        return "LoginOk{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}
