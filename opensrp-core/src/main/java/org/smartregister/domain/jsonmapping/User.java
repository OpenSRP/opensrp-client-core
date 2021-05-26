package org.smartregister.domain.jsonmapping;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.smartregister.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * The person who actually entered or modified data into system. The
 * {@link User} before getting access to any service in system have to be
 * authenticated by system. A user may also be linked with a provider by
 * {@link BaseEntity}
 */
public class User extends BaseEntity {

    private String username;

    private char[] password;

    private String salt;

    private String status;

    private List<String> roles;

    private List<String> permissions;

    private String preferredName;

    public User() {

    }

    public User(String baseEntityId) {
        super(baseEntityId);
    }

    public User(String baseEntityId, String username, char[] password, String salt) {
        super(baseEntityId);
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

    public User(String baseEntityId, String username, char[] password, String salt, String status, List<String> roles, List<String> permissions) {
        super(baseEntityId);
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.status = status;
        this.roles = roles;
        this.permissions = permissions;
    }

    public User(String baseEntityId, String username, char[] password, String preferredName, String salt, String status, List<String> roles,
                List<String> permissions) {
        super(baseEntityId);
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.status = status;
        this.roles = roles;
        this.permissions = permissions;
        this.preferredName = preferredName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getRoles() {
        return roles;
    }

    /**
     * WARNING: Overrides all existing roles
     *
     * @param roles
     * @return
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);
    }

    public boolean removeRole(String role) {
        return roles.remove(role);
    }

    public boolean hasRole(String role) {
        if (roles != null)
            for (String r : roles) {
                if (role.equalsIgnoreCase(r)) {
                    return true;
                }
            }
        return false;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    /**
     * WARNING: Overrides all existing permissions
     *
     * @param permissions
     * @return
     */
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(String permission) {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        permissions.add(permission);
    }

    public boolean removePermission(String permission) {
        return permissions.remove(permission);
    }

    public boolean hasPermission(String permission) {
        if (permissions != null)
            for (String p : permissions) {
                if (permission.equalsIgnoreCase(p)) {
                    return true;
                }
            }
        return false;
    }

    public User withUsername(String username) {
        this.username = username;
        return this;
    }

    public User withPassword(char[] password) {
        this.password = password;
        return this;
    }

    public User withSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public User withStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * WARNING: Overrides all existing roles
     *
     * @param roles
     * @return
     */
    public User withRoles(List<String> roles) {
        this.roles = roles;
        return this;
    }

    public User withRole(String role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);
        return this;
    }

    /**
     * WARNING: Overrides all existing permissions
     *
     * @param permissions
     * @return
     */
    public User withPermissions(List<String> permissions) {
        this.permissions = permissions;
        return this;
    }

    public User withPermission(String permission) {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        permissions.add(permission);
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

}