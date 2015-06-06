package tk.trentoleaf.cineweb.exceptions.rest;

public class BadRequestException extends RestException {

    public static final BadRequestException BAD_USER = new BadRequestException("User not valid");
    public static final BadRequestException BAD_FILM = new BadRequestException("Film not valid");
    public static final BadRequestException BAD_PLAY = new BadRequestException("Play not valid");

    public BadRequestException(String message) {
        super(message);
    }
}
