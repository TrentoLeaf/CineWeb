package tk.trentoleaf.cineweb.entities;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class ConfirmCode implements Serializable {

    @NotEmpty(message = "Code cannot be null")
    private String code;

    @SuppressWarnings("unused")
    public ConfirmCode() {
    }

    public ConfirmCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
