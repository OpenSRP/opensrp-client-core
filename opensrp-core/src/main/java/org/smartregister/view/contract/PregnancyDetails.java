package org.smartregister.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PregnancyDetails {

    private static final int MONTHS_PREGNANT = 9;
    private String monthsPregnant;
    private String edd;
    private boolean isEDDPassed;
    private boolean isLastMonthOfPregnancy;
    private int daysPastEdd;

    public PregnancyDetails(String monthsPregnantArg, String eddArg, int daysPastEddArg) {
        monthsPregnant = monthsPregnantArg;
        edd = eddArg;
        isEDDPassed = Integer.valueOf(monthsPregnant) >= MONTHS_PREGNANT;
        isLastMonthOfPregnancy = Integer.valueOf(monthsPregnant) >= MONTHS_PREGNANT - 1;
        daysPastEdd = daysPastEddArg;
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

    public boolean isLastMonthOfPregnancy() {
        return isLastMonthOfPregnancy;
    }
}
