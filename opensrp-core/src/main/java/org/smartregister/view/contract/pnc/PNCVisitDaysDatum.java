package org.smartregister.view.contract.pnc;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PNCVisitDaysDatum {
    Integer day;
    @SerializedName("type")
    PNCVisitType visitType;

    public PNCVisitDaysDatum(Integer day, PNCVisitType visitType) {
        this.day = day;
        this.visitType = visitType;
    }

    public Integer getDay() {
        return day;
    }

    public PNCVisitType getVisitType() {
        return visitType;
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
