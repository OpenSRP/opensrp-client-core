package org.smartregister.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ndegwamartin on 03/06/2020.
 */
public class AccountUserInfo {

    private String name;

    private String email;

    private Boolean enabled;

    @SerializedName("preferred_username")
    private String preferredUsername;

    @SerializedName("email_verified")
    private Boolean emailVerified;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
