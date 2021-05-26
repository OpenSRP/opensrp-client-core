package org.smartregister.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChildDetail {
    private final String caseId;
    private final String thayiCardNumber;
    private final String photo_path;

    private final LocationDetails location;
    private final CoupleDetails coupleDetails;
    private final BirthDetails childDetails;

    private List<TimelineEvent> timelineEvents;
    private Map<String, String> details;

    public ChildDetail(String caseId, String thayiCardNumber, CoupleDetails coupleDetails,
                       LocationDetails location, BirthDetails childDetails, String photoPath) {
        this.caseId = caseId;
        this.thayiCardNumber = thayiCardNumber;
        this.photo_path = photoPath;

        this.coupleDetails = coupleDetails;
        this.location = location;
        this.childDetails = childDetails;

        this.timelineEvents = new ArrayList<TimelineEvent>();
    }

    public ChildDetail addTimelineEvents(List<TimelineEvent> events) {
        this.timelineEvents.addAll(events);
        return this;
    }

    public ChildDetail addExtraDetails(Map<String, String> details) {
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
