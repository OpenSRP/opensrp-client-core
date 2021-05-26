package org.smartregister.domain;

public enum ResponseErrorStatus {
    malformed_url("Malformed Url"),
    not_found("Url Not Found"),
    timeout("Connection Timeout");

    private String displayValue;
    ResponseErrorStatus(String s) {
        displayValue = s;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
}
