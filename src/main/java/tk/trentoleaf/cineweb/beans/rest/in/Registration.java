package tk.trentoleaf.cineweb.beans.rest.in;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.io.Serializable;

public class Registration implements Serializable {

    @Size(max = 255, message = "Email too long")
    @NotEmpty(message = "Email cannot be null")
    @Email(message = "Email must be a valid email")
    private String email;

    @Size(max = 60, message = "Password too long")
    @NotEmpty(message = "Password cannot be null")
    private String password;

    @Size(max = 100, message = "FirstName too long")
    @NotEmpty(message = "FirstName cannot be null")
    private String firstName;

    @Size(max = 100, message = "SecondName too long")
    @NotEmpty(message = "SecondName cannot be null")
    private String secondName;

    @SuppressWarnings("unused")
    public Registration() {
    }

    public Registration(String email, String password, String firstName, String secondName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

}
