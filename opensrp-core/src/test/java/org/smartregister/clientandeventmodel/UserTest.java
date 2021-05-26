package org.smartregister.clientandeventmodel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.mock.UserMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaderchowdhury on 27/11/17.
 */

public class UserTest extends BaseUnitTest {

    private UserMock user;
    private List<String> list = new ArrayList<>();
    private String username = "root";
    private String password = "root";
    private String salt = "101";
    private String status = "1";
    private String role = "admin";
    private String permission = "drwxrwxr-x";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new UserMock("");
        list.add("admin");
        list.add("member");
    }

    @Test
    public void assertConstructoNotnUll() {
        Assert.assertNotNull(new UserMock(""));
        Assert.assertNotNull(new UserMock("", "", "", ""));
        Assert.assertNotNull(new UserMock("", "", "", "", "", null, null));
    }

    public String getUsername() {
        return user.getUsername();
    }

    @Test
    public void setUsername() {
        user.setUsername(username);
        Assert.assertEquals(getUsername(), username);
    }

    public String getPassword() {
        return user.getPassword();
    }

    @Test
    public void setPassword() {
        user.setPassword(password);
        Assert.assertEquals(getPassword(), password);
    }

    public String getSalt() {
        return user.getSalt();
    }

    @Test
    public void setSalt() {
        user.setSalt(salt);
        Assert.assertEquals(getSalt(), salt);
    }

    public String getStatus() {
        return user.getStatus();
    }

    @Test
    public void setStatus() {
        user.setStatus(status);
        Assert.assertEquals(getStatus(), status);
    }

    public List<String> getRoles() {
        return user.getRoles();
    }

    @Test
    public void setRoles() {
        user.setRoles(list);
        Assert.assertEquals(getRoles(), list);
    }

    @Test
    public void addRole() {
        user.setRoles(null);
        user.addRole(role);
        Assert.assertEquals(getRoles().get(0), role);
    }

    @Test
    public void removeRole() {
        user.setRoles(list);
        user.removeRole(role);
        Assert.assertEquals(getRoles().contains(role), false);
    }

    @Test
    public void hasRole() {
        user.setRoles(list);
        Assert.assertEquals(user.hasRole(role), true);
        Assert.assertEquals(user.hasRole("invalid"), false);
    }

    @Test
    public void isDefaultAdmin() {
        user.setRoles(list);
        user.setUsername("admin");
        Assert.assertEquals(user.isDefaultAdmin(), true);
    }

    @Test
    public void hasAdminRights() {
        user.setRoles(null);
        user.setUsername("NOTadmin");
        Assert.assertEquals(user.hasAdminRights(), false);
        user.setRoles(list);
        user.setUsername("admin");
        Assert.assertEquals(user.hasAdminRights(), true);
    }

    public List<String> getPermissions() {
        return user.getPermissions();
    }

    @Test
    public void setPermissions() {
        user.setPermissions(list);
        Assert.assertEquals(getPermissions(), list);
    }

    @Test
    public void addPermission() {
        user.setPermissions(null);
        user.addPermission(permission);
        Assert.assertEquals(getPermissions().get(0), permission);
    }

    @Test
    public void removePermission() {
        user.setPermissions(null);
        user.addPermission(permission);
        Assert.assertEquals(user.removePermission(permission), true);
        Assert.assertEquals(user.removePermission(permission), false);
    }

    @Test
    public void hasPermission() {
        user.setPermissions(list);
        Assert.assertEquals(user.hasPermission(permission), false);
        Assert.assertEquals(user.hasPermission(role), true);
    }

    @Test
    public void withUsername() {
        Assert.assertNotNull(user.withUsername(username));
    }

    @Test
    public void withPassword() {
        Assert.assertNotNull(user.withPassword(password));
    }

    @Test
    public void withSalt() {
        Assert.assertNotNull(user.withSalt(salt));
    }

    @Test
    public void withStatus() {
        Assert.assertNotNull(user.withStatus(status));
    }

    @Test
    public void withRoles() {
        Assert.assertNotNull(user.withRoles(list));
    }

    @Test
    public void withRole() {
        Assert.assertNotNull(user.withRole(role));
    }

    @Test
    public void withPermissions() {
        Assert.assertNotNull(user.withPermissions(list));
    }

    @Test
    public void withPermission() {
        Assert.assertNotNull(user.withPermission(permission));
    }

    @Test
    public void assertequals() {
        Assert.assertEquals(user.equals(user), true);
    }

    @Test
    public void asserthashCode() {
        Assert.assertNotNull(user.hashCode());
    }

    @Test
    public void asserttoString() {
        Assert.assertNotNull(user.toString());
    }

}
