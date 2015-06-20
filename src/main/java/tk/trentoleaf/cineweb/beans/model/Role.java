package tk.trentoleaf.cineweb.beans.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enumeration that represent a user role.
 */
public enum Role {

    @SerializedName("admin")
    ADMIN("admin", "Admin user"),

    @SerializedName("client")
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
        List<Role> roles = new ArrayList<>();
        roles.add(ADMIN);
        roles.add(CLIENT);
        return roles;
    }

    // map to convert a String to the corresponding role
    private static final Map<String, Role> map;

    static {
        map = new HashMap<>();
        for (Role r : Role.values()) {
            map.put(r.roleID, r);
        }
    }

    // convert a string to a role
    public static Role fromID(String id) {
        // NB: id can contains some spaces at the end since saved as CHAR(8)
        return map.get(id.trim());
    }

}
