package tk.trentoleaf.cineweb.beans.rest;

import java.io.Serializable;

public class ActivateUser implements Serializable {

    private int code;
    private String msg;

    public ActivateUser(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "ActivateUser{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
