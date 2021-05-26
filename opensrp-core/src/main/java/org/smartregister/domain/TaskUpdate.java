package org.smartregister.domain;


import com.google.gson.annotations.SerializedName;

public class TaskUpdate {


    @SerializedName("identifier")
    private String identifier;

    @SerializedName("status")
    private String status;

    @SerializedName("businessStatus")
    private String businessStatus;

    @SerializedName("serverVersion")
    private String serverVersion;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }


}
