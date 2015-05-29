package tk.trentoleaf.cineweb.rest.entities;

import java.io.Serializable;

public class ChangePassword implements Serializable {

    private String email;
    private String oldPassword;
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

}
