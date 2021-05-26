package org.smartregister.domain;

public enum SyncStatus {
    SYNCED("SYNCED"), PENDING("PENDING");
    private String value;

    SyncStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
