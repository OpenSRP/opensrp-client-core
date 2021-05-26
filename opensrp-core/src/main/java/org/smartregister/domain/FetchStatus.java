package org.smartregister.domain;

import java.io.Serializable;

public enum FetchStatus implements Displayable, Serializable {
    fetchStarted("Update started"),
    fetched("Update successful."),
    nothingFetched("Already up to " + "" + "" + "date."),
    fetchedFailed("Update failed. Please try again."),
    noConnection("No " + "network" + " connection detected"),
    fetchProgress("Update progress");

    private String displayValue;

    FetchStatus(String displayValueArg) {
        displayValue = displayValueArg;
    }

    public String displayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
}
