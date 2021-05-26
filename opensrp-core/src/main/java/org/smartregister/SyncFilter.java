package org.smartregister;

public enum SyncFilter {
    PROVIDER("providerId"), LOCATION("locationId"), LOCATION_ID("locationId"), TEAM("team"), TEAM_ID("teamId");

    private String value;

    SyncFilter(String value) {
        this.value = value;
    }

    public static SyncFilter from(String filter) {
        return valueOf(filter);
    }

    public String value() {
        return this.value;
    }

}
