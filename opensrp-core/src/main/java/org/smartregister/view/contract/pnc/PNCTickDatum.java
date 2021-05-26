package org.smartregister.view.contract.pnc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PNCTickDatum {

    private int day;
    private PNCVisitType type;

    public PNCTickDatum(int day, PNCVisitType type) {
        this.day = day;
        this.type = type;
    }

    public int day() {
        return day;
    }

    public PNCVisitType type() {
        return type;
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
