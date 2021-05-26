package org.smartregister.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class Alert implements Serializable {
    private final String scheduleName;
    private String caseID;
    private String visitCode;
    private AlertStatus status;
    private String startDate;
    private String expiryDate;
    private String completionDate;
    private boolean offline;

    public Alert(String caseID, String scheduleName, String visitCode, AlertStatus status, String
            startDate, String expiryDate) {
        this.caseID = caseID;
        this.visitCode = visitCode;
        this.status = status;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.scheduleName = scheduleName;
        this.offline = false;
    }

    public Alert(String caseID, String scheduleName, String visitCode, AlertStatus status, String
            startDate, String expiryDate, boolean offline) {
        this.caseID = caseID;
        this.visitCode = visitCode;
        this.status = status;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.scheduleName = scheduleName;
        this.offline = offline;
    }

    public Alert withCompletionDate(String completionDate) {
        this.completionDate = completionDate;
        return this;
    }

    public String scheduleName() {
        return scheduleName;
    }

    public String visitCode() {
        return visitCode;
    }

    public AlertStatus status() {
        return status;
    }

    public String startDate() {
        return startDate;
    }

    public String expiryDate() {
        return expiryDate;
    }

    public String caseId() {
        return caseID;
    }

    public String completionDate() {
        return completionDate;
    }

    public boolean isComplete() {
        return AlertStatus.complete.equals(status);
    }

    public boolean offline() {
        return offline;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
