package tk.trentoleaf.cineweb.beans.rest.in;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class ForgotPassword implements Serializable {

    @NotEmpty(message = "Email cannot be null")
    private String email;

    @SuppressWarnings("unused")
    public ForgotPassword() {
    }

    public ForgotPassword(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
