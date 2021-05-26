package org.smartregister.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.LocalDate;
import org.smartregister.domain.ANCServiceType;
import org.smartregister.domain.ChildServiceType;

import java.util.HashMap;
import java.util.Map;

import static org.smartregister.util.DateUtil.formatDate;
import static org.smartregister.util.DateUtil.getLocalDateFromISOString;

public class ServiceProvidedDTO implements Comparable<ServiceProvidedDTO> {

    public static ServiceProvidedDTO emptyService = new ServiceProvidedDTO("", "", null);

    private String name;
    private String date;
    private int day;
    private Map<String, String> data = new HashMap<String, String>();

    public ServiceProvidedDTO(String name, String date, Map<String, String> data) {
        this.name = name;
        this.date = date;
        this.data = data;
    }

    public ServiceProvidedDTO(String name, Integer visitDay, String date) {
        this.name = name;
        day = visitDay;
        this.date = date;
    }

    public ChildServiceType type() {
        return ChildServiceType.tryParse(name, ChildServiceType.EMPTY);
    }

    public ANCServiceType ancServiceType() {
        return ANCServiceType.tryParse(name, ANCServiceType.EMPTY);
    }

    public String name() {
        return name;
    }

    public String date() {
        return formatDate(date);
    }

    public String dateForDisplay() {
        return formatDate(date, "dd/MM");
    }

    public int day() {
        return day;
    }

    public LocalDate localDate() {
        return getLocalDateFromISOString(date);
    }

    public String shortDate() {
        return formatDate(date, "dd/MM");
    }

    public String servicedOn() {
        return type().shortName() + ": " + shortDate();
    }

    public String servicedOnWithServiceName() {
        return ancServiceType().serviceDisplayName() + ": " + shortDate();
    }

    public String ancServicedOn() {
        return shortDate();
    }

    public Map<String, String> data() {
        return data == null ? new HashMap<String, String>() : data;
    }

    public ServiceProvidedDTO withDay(int day) {
        this.day = day;
        return this;
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

    @Override
    public int compareTo(ServiceProvidedDTO another) {
        return this.localDate().isAfter(another.localDate()) ? 1 : 0;
    }
}
