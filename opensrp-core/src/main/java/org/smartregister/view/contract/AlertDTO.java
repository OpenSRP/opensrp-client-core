package org.smartregister.view.contract;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.smartregister.domain.ANCServiceType;
import org.smartregister.domain.ChildServiceType;
import org.smartregister.util.DateUtil;

public class AlertDTO {
    public static final AlertDTO emptyAlert = new AlertDTO("", "", "");

    private final String name;
    private final String status;
    private final String date;

    public AlertDTO(String name, String status, String date) {
        this.name = name;
        this.status = status;
        this.date = date;
    }

    public String name() {
        return name;
    }

    public String status() {
        return status;
    }

    public AlertStatus alertStatus() {
        return StringUtils.isBlank(status) ? AlertStatus.EMPTY : AlertStatus.from(status);
    }

    public ChildServiceType type() {
        return ChildServiceType.tryParse(name, ChildServiceType.EMPTY);
    }

    public ANCServiceType ancServiceType() {
        return ANCServiceType.tryParse(name, ANCServiceType.EMPTY);
    }

    public String date() {
        return DateUtil.formatDate(date);
    }

    public String shortDate() {
        return DateUtil.formatDate(date, "dd/MM");
    }

    public boolean isUrgent() {
        return AlertStatus.from(status).equals(AlertStatus.URGENT);
    }

    public boolean isCompleted() {
        return AlertStatus.from(status).equals(AlertStatus.COMPLETE);
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
