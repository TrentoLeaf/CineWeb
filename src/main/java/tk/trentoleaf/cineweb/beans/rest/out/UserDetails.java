package tk.trentoleaf.cineweb.beans.rest.out;

import tk.trentoleaf.cineweb.beans.model.User;

import java.io.Serializable;

public class UserDetails implements Serializable {

    private String email;
    private String firstName;
    private String secondName;
    private double credit;

    public UserDetails(User user) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.secondName = user.getSecondName();
        this.credit = user.getCredit();
    }

}