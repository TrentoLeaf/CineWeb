package tk.trentoleaf.cineweb.rest.entities;

import java.io.Serializable;

public class ErrorResponse implements Serializable {

    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
