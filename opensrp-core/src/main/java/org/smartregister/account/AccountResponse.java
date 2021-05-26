package org.smartregister.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ndegwamartin on 06/05/2020.
 */
public class AccountResponse {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("refresh_expires_in")
    private Integer refreshExpiresIn;

    @SerializedName("expires_in")
    private Integer expiresIn;

    @SerializedName("scope")
    private String scope;

    private int status;

    private AccountError accountError;

    public AccountResponse(int status, AccountError accountError) {
        this.status = status;
        this.accountError = accountError;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public AccountError getAccountError() {
        return accountError;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public Integer getRefreshExpiresIn() {
        return refreshExpiresIn;
    }
}
