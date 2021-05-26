package org.smartregister.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RefillFollowUps {
    private String name;
    private AlertDTO alert;
    private String type;

    public RefillFollowUps(String name, AlertDTO alert, String type) {
        this.name = name;
        this.alert = alert;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public RefillFollowUps withName(String name) {
        this.name = name;
        return this;
    }

    public AlertDTO alert() {
        return alert;
    }

    public RefillFollowUps withAlert(AlertDTO alert) {
        this.alert = alert;
        return this;
    }

    public String type() {
        return type;
    }

    public RefillFollowUps withType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
