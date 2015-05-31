package tk.trentoleaf.cineweb.rest.entities;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ChangePasswordWithCode implements Serializable {

    private String email;
    private String code;
    private String newPassword;

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

    public boolean isValid() {
        return StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(code) && StringUtils.isNotEmpty(newPassword);
    }

}
