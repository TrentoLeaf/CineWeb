package tk.trentoleaf.cineweb.entities;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Auth implements Serializable {

    private String email;
    private String password;

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

    public boolean isValid() {
        return StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(password);
    }
}
