package tk.trentoleaf.cineweb.rest.entities;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ForgotPassword implements Serializable {

    private String email;

    public ForgotPassword() {
    }

    public ForgotPassword(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public boolean isValid() {
        return StringUtils.isNotEmpty(email);
    }

    @Override
    public String toString() {
        return "ForgotPassword{" +
                "email='" + email + '\'' +
                '}';
    }
}
