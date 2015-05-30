package tk.trentoleaf.cineweb.rest.entities;

import java.io.Serializable;

public class Registration implements Serializable {

    private String email;
    private String password;
    private String firstName;
    private String secondName;

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

        // TODO
        return false;
    }
}
