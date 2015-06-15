package tk.trentoleaf.cineweb.entities;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class ChangePassword implements Serializable {

    @NotEmpty(message = "Email cannot be null")
    private String email;

    @NotEmpty(message = "OldPassword cannot be null")
    private String oldPassword;

    @NotEmpty(message = "NewPassword cannot be null")
    private String newPassword;

    @SuppressWarnings("unused")
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
