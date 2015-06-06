package tk.trentoleaf.cineweb.entities;

import org.apache.commons.lang3.StringUtils;
import tk.trentoleaf.cineweb.utils.Utils;

import java.io.Serializable;

public class Registration implements Serializable {

    private String email;
    private String password;
    private String firstName;
    private String secondName;

    public Registration() {
    }

    public Registration(String email, String password, String firstName, String secondName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public boolean isValid() {
        return Utils.isValidEmail(email) && StringUtils.isNotEmpty(password)
                && StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(secondName);
    }
}
