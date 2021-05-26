package org.smartregister.util;

import org.smartregister.domain.LoginResponse;
import org.smartregister.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class FakeUserService extends UserService {
    private boolean shouldSucceedLocalLogin;
    private LoginResponse shouldSucceedRemoteLogin;
    private String expectedUserName = "";
    private String expectedPassword = "";
    private List<String> actualCalls = new ArrayList<String>();
    private boolean hasARegisteredUser;
    private Session session;

    public FakeUserService() {
        super(null, null, null, null, null, null, null, null);
    }

    @Override
    public boolean isValidLocalLogin(String userName, byte[] password) {
        assertExpectedCredentials(userName, password);
        actualCalls.add("local");
        return shouldSucceedLocalLogin;
    }

    @Override
    public LoginResponse isValidRemoteLogin(String userName, char[] password) {
        assertExpectedCredentials(userName, password);
        actualCalls.add("remote");
        return shouldSucceedRemoteLogin;
    }

    @Override
    public void localLogin(String userName, char[] password) {
        super.setupContextForLogin(password);
        actualCalls.add("login");
        assertExpectedCredentials(userName, password);
    }


    public void remoteLogin(String userName, char[] password, String anmLocation) {
        super.setupContextForLogin(password);
        actualCalls.add("login");
        assertExpectedCredentials(userName, password);
    }

    @Override
    public boolean hasARegisteredUser() {
        return hasARegisteredUser;
    }

    @Override
    public void logout() {
        super.logoutSession();
        actualCalls.add("logout");
    }

    @Override
    public void logoutSession() {
        super.logoutSession();
    }

    private void assertExpectedCredentials(String userName, char[] password) {
        if (!expectedUserName.equals(userName)) {
            throw new RuntimeException("Expected user: " + expectedUserName + ". Actual: " + userName);
        }
        if (!expectedPassword.equals(password)) {
            throw new RuntimeException("Expected user: " + expectedUserName + ". Actual: " + password);
        }
    }

    public void setupFor(String userName, String password, boolean hasARegisteredUser, boolean shouldSucceedLocalLogin, LoginResponse shouldSucceedRemoteLogin) {
        this.expectedUserName = userName;
        this.expectedPassword = password;
        this.hasARegisteredUser = hasARegisteredUser;
        this.shouldSucceedLocalLogin = shouldSucceedLocalLogin;
        this.shouldSucceedRemoteLogin = shouldSucceedRemoteLogin;
    }

    public void assertOrderOfCalls(String... calls) {
        if (!actualCalls.equals(asList(calls))) {
            throw new RuntimeException("Expected calls: " + asList(calls) + ". Actual: " + actualCalls);
        }
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    protected Session session() {
        return session;
    }
}
