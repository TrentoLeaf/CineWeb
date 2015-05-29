package tk.trentoleaf.cineweb.rest.exceptions;

public class AuthFailedException extends NotFoundException {

    public AuthFailedException() {
        super("Wrong email or password");
    }
}
