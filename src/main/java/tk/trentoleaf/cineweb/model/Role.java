package tk.trentoleaf.cineweb.model;

import java.util.ArrayList;
import java.util.List;

public enum Role {

    ADMIN("admin", "Admin user"),
    CLIENT("client", "Client user");

    private String roleID;
    private String description;

    Role(String roleID, String description) {
        this.roleID = roleID;
        this.description = description;
    }

    public String getRoleID() {
        return roleID;
    }

    public String getDescription() {
        return description;
    }

    public static List<Role> getRoles() {
        List roles = new ArrayList();
        roles.add(ADMIN);
        roles.add(CLIENT);
        return roles;
    }
}
