package org.smartregister.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ANM {
    private final String name;
    private final long fpCount;
    private final long eligibleCoupleCount;
    private final long ancCount;
    private final long pncCount;
    private final long childCount;

    public ANM(String name, long eligibleCoupleCount, long fpCount, long ancCount, long pncCount,
               long childCount) {
        this.name = name;
        this.eligibleCoupleCount = eligibleCoupleCount;
        this.fpCount = fpCount;
        this.ancCount = ancCount;
        this.pncCount = pncCount;
        this.childCount = childCount;
    }

    public String name() {
        return name;
    }

    public long ancCount() {
        return ancCount;
    }

    public long pncCount() {
        return pncCount;
    }

    public long childCount() {
        return childCount;
    }

    public long ecCount() {
        return eligibleCoupleCount;
    }

    public long fpCount() {
        return fpCount;
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
