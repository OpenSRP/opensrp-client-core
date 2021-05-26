package org.smartregister.domain;

/**
 * Created by Richard Kareko on 6/4/20.
 */
public enum SyncEntity {
    TASKS("Tasks"), EVENTS("Events"), LOCATIONS("Locations"),
    STRUCTURES("structures"), PLANS("Plans");
    private String value;

    SyncEntity(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
