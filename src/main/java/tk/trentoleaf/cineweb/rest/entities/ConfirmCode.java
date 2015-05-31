package tk.trentoleaf.cineweb.rest.entities;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ConfirmCode implements Serializable {

    private String code;

    public ConfirmCode() {
    }

    public ConfirmCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public boolean isValid() {
        return StringUtils.isNotEmpty(code);
    }
}
