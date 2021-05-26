package org.smartregister.view.contract.pnc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PNCCircleDatum {
    private int day;
    private PNCVisitType type;
    private boolean colored;

    public PNCCircleDatum(int day, PNCVisitType type, boolean colored) {
        this.day = day;
        this.type = type;
        this.colored = colored;
    }

    public int day() {
        return day;
    }

    public PNCVisitType type() {
        return type;
    }

    public boolean coloured() {
        return colored;
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
