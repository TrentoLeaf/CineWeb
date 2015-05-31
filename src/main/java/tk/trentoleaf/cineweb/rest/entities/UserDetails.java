package tk.trentoleaf.cineweb.rest.entities;

import tk.trentoleaf.cineweb.model.User;

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

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public double getCredit() {
        return credit;
    }
}
