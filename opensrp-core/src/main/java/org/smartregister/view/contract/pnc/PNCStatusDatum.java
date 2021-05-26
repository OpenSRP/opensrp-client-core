package org.smartregister.view.contract.pnc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PNCStatusDatum {

    private int day;
    private PNCVisitStatus status;

    public PNCStatusDatum(int day, PNCVisitStatus status) {
        this.day = day;
        this.status = status;
    }

    public int day() {
        return day;
    }

    public PNCVisitStatus status() {
        return status;
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
