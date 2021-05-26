package org.smartregister.view.contract;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.smartregister.util.DateUtil;

public class ECChildClient {
    private final String entityId;
    private final String gender;
    private final String dateOfBirth;

    public ECChildClient(String entityId, String gender, String dateOfBirth) {
        this.entityId = entityId;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isMale() {
        return gender != null && gender.equalsIgnoreCase("Male");
    }

    public boolean isFemale() {
        return !isMale();
    }

    public int getAgeInDays() {
        return StringUtils.isBlank(dateOfBirth) ? 0
                : Days.daysBetween(LocalDate.parse(dateOfBirth), DateUtil.today()).getDays();
    }

    public String getAgeInString() {
        return format(getAgeInDays());
    }

    public String format(int days_since) {
        int DAYS_THRESHOLD = 28;
        int WEEKS_THRESHOLD = 119;
        int MONTHS_THRESHOLD = 720;
        if (days_since < DAYS_THRESHOLD) {
            return (int) Math.floor(days_since) + "d";
        } else if (days_since < WEEKS_THRESHOLD) {
            return (int) Math.floor(days_since / 7) + "w";
        } else if (days_since < MONTHS_THRESHOLD) {
            return (int) Math.floor(days_since / 30) + "m";
        } else {
            return (int) Math.floor(days_since / 365) + "y";
        }
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
