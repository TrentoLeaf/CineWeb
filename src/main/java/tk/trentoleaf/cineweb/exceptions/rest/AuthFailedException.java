package tk.trentoleaf.cineweb.exceptions.rest;

public class AuthFailedException extends NotFoundException {

    public AuthFailedException() {
        super("Wrong email or password");
    }
}
