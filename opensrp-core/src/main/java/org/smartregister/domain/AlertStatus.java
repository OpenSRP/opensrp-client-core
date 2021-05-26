package org.smartregister.domain;

/**
 * Created by keyman on 07/04/2017.
 */
public enum AlertStatus {
    upcoming("upcoming"), normal("normal"), urgent("urgent"), inProcess("inProcess"), complete(
            "complete"), expired("expired");

    private String value;

    AlertStatus(String value) {
        this.value = value;
    }

    public static AlertStatus from(String alertStatus) {
        return valueOf(alertStatus);
    }

    public String value() {
        return this.value;
    }
}

