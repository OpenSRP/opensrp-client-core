package org.smartregister.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Environment {
    @SerializedName("env")
    @Expose
    protected String env;

    @SerializedName("client.id")
    @Expose
    protected String id;

    @SerializedName("url")
    @Expose
    protected String url;

    @SerializedName("client.secret")
    @Expose
    protected String secret;

    public String getEnv() {
        return env;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    public String getUrl() {
        return url;
    }
}