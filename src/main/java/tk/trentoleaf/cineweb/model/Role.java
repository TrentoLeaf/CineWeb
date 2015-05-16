package tk.trentoleaf.cineweb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Map<String, Role> map;
    static {
        map = new HashMap<>();
        for(Role r : Role.values()) {
            map.put(r.roleID, r);
        }
    }

    public static Role fromID(String id) {
        // NB: id can contains some spaces at the end since saved as CHAR(8)
        return map.get(id.trim());
    }

}
