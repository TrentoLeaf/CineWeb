package tk.trentoleaf.cineweb.beans.rest.in;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ChangePasswordWithCode implements Serializable {

    @NotEmpty(message = "Email cannot be null")
    private String email;

    @NotEmpty(message = "Code cannot be null")
    private String code;

    @NotEmpty(message = "NewPassword cannot be null")
    private String newPassword;

    @SuppressWarnings("unused")
    public ChangePasswordWithCode() {
    }

    public ChangePasswordWithCode(String email, String code, String newPassword) {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getCode() {
        return code;
    }

}
