package tk.trentoleaf.cineweb.beans.rest.out;

import java.io.Serializable;

public class ErrorResponse implements Serializable {

    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

}
