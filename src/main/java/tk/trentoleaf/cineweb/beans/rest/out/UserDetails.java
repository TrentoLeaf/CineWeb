package tk.trentoleaf.cineweb.beans.rest.out;

import tk.trentoleaf.cineweb.beans.model.Role;
import tk.trentoleaf.cineweb.beans.model.User;

import java.io.Serializable;

public class UserDetails implements Serializable {
/* OLD by Andrea
    private String email;
    private String firstName;
    private String secondName;
    private double credit;
    private Role role;

    public UserDetails(User user) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.secondName = user.getSecondName();
        this.credit = user.getCredit();
        this.role = user.getRole();
    }
*/

    private String email;
    private String name;
    private Role role;

    public UserDetails(User user) {
        this.email = user.getEmail();
        this.name = user.getSecondName() + " " + user.getFirstName();
        this.role = user.getRole();
    }



}