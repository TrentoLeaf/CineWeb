package tk.trentoleaf.cineweb.beans.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
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

    // map to convert a String to the corresponding role
    private static final Map<String, Role> map;

    static {
        map = new HashMap<>();
        for (Role r : Role.values()) {
            map.put(r.roleID, r);
        }
    }

    /**
     * Convert a String to the corresponding Role.
     *
     * @param roleID The String representing the Role.
     * @return The corresponding Role object.
     */
    public static Role fromID(String roleID) {
        return map.get(roleID);
    }

}
