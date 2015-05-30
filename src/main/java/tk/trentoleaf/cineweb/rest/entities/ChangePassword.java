package tk.trentoleaf.cineweb.rest.entities;

import java.io.Serializable;

public class ChangePassword implements Serializable {

    private String email;
    private String oldPassword;
    private String newPassword;

    public ChangePassword() {
    }

    public ChangePassword(String email, String oldPassword, String newPassword) {
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

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
