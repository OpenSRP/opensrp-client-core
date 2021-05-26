package org.smartregister.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ANCDetail {
    private final String caseId;
    private final String thayiCardNumber;
    private final CoupleDetails coupleDetails;

    private final LocationDetails location;
    private final PregnancyDetails pregnancyDetails;

    private List<TimelineEvent> timelineEvents;
    private Map<String, String> details;

    public ANCDetail(String caseId, String thayiCardNumber, CoupleDetails coupleDetails,
                     LocationDetails location, PregnancyDetails pregnancyDetails) {
        this.caseId = caseId;
        this.thayiCardNumber = thayiCardNumber;
        this.coupleDetails = coupleDetails;
        this.location = location;
        this.pregnancyDetails = pregnancyDetails;

        this.timelineEvents = new ArrayList<TimelineEvent>();
    }

    public ANCDetail addTimelineEvents(List<TimelineEvent> events) {
        this.timelineEvents.addAll(events);
        return this;
    }

    public ANCDetail addExtraDetails(Map<String, String> details) {
        this.details = details;
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
}

