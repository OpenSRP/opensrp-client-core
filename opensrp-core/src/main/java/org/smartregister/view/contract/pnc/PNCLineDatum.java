package org.smartregister.view.contract.pnc;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PNCLineDatum {

    @SerializedName("start")
    private int startDay;
    @SerializedName("end")
    private int endDay;
    private PNCVisitType type;

    public PNCLineDatum(int startDay, int endDay, PNCVisitType type) {
        this.startDay = startDay;
        this.endDay = endDay;
        this.type = type;
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

    public int getStartDay() {
        return startDay;
    }

    public int getEndDay() {
        return endDay;
    }

    public String getType() {
        return type.toString();
    }
}
