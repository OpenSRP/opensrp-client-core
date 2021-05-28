package org.smartregister.domain;

public enum ResponseStatus  {
    failure("failure"),
    success("success");

    private String displayValue;

    ResponseStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
}
