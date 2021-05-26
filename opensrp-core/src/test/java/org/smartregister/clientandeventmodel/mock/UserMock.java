package org.smartregister.clientandeventmodel.mock;

import org.smartregister.clientandeventmodel.User;

import java.util.List;

/**
 * Created by kaderchowdhury on 27/11/17.
 */

public class UserMock extends User {
    protected UserMock() {
        super();
    }

    public UserMock(String baseEntityId) {
        super(baseEntityId);
    }

    public UserMock(String baseEntityId, String username, String password, String salt) {
        super(baseEntityId, username, password, salt);
    }

    public UserMock(String baseEntityId, String username, String password, String salt, String status, List<String> roles, List<String> permissions) {
        super(baseEntityId, username, password, salt, status, roles, permissions);
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
    }

    @Override
    public String getSalt() {
        return super.getSalt();
    }

    @Override
    public void setSalt(String salt) {
        super.setSalt(salt);
    }

    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
    }

    @Override
    public List<String> getRoles() {
        return super.getRoles();
    }

    @Override
    public void setRoles(List<String> roles) {
        super.setRoles(roles);
    }

    @Override
    public void addRole(String role) {
        super.addRole(role);
    }

    @Override
    public boolean removeRole(String role) {
        return super.removeRole(role);
    }

    @Override
    public boolean hasRole(String role) {
        return super.hasRole(role);
    }

    @Override
    public boolean isDefaultAdmin() {
        return super.isDefaultAdmin();
    }

    @Override
    public boolean hasAdminRights() {
        return super.hasAdminRights();
    }

    @Override
    public List<String> getPermissions() {
        return super.getPermissions();
    }

    @Override
    public void setPermissions(List<String> permissions) {
        super.setPermissions(permissions);
    }

    @Override
    public void addPermission(String permission) {
        super.addPermission(permission);
    }

    @Override
    public boolean removePermission(String permission) {
        return super.removePermission(permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        return super.hasPermission(permission);
    }

    @Override
    public User withUsername(String username) {
        return super.withUsername(username);
    }

    @Override
    public User withPassword(String password) {
        return super.withPassword(password);
    }

    @Override
    public User withSalt(String salt) {
        return super.withSalt(salt);
    }

    @Override
    public User withStatus(String status) {
        return super.withStatus(status);
    }

    @Override
    public User withRoles(List<String> roles) {
        return super.withRoles(roles);
    }

    @Override
    public User withRole(String role) {
        return super.withRole(role);
    }

    @Override
    public User withPermissions(List<String> permissions) {
        return super.withPermissions(permissions);
    }

    @Override
    public User withPermission(String permission) {
        return super.withPermission(permission);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
