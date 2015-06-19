package tk.trentoleaf.cineweb.beans.rest;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class Auth implements Serializable {

    @NotEmpty(message = "Email cannot be null")
    private String email;

    @NotEmpty(message = "Password cannot be null")
    private String password;

    @SuppressWarnings("unused")
    public Auth() {
    }

    public Auth(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
