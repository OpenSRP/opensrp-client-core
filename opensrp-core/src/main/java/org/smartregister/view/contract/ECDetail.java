package org.smartregister.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ECDetail {
    private String caseId;
    private CoupleDetails coupleDetails;
    private String village;
    private String subcenter;
    private String ecNumber;
    private boolean isHighPriority;
    private String address;
    private String photoPath;
    private List<Child> children;
    private List<TimelineEvent> timelineEvents;
    private Map<String, String> details;

    public ECDetail(String caseId, String village, String subcenter, String ecNumber, boolean
            isHighPriority, String address, String photoPath, List<Child> children, CoupleDetails
                            coupleDetails, Map<String, String> details) {
        this.caseId = caseId;
        this.coupleDetails = coupleDetails;
        this.village = village;
        this.subcenter = subcenter;
        this.ecNumber = ecNumber;
        this.isHighPriority = isHighPriority;
        this.address = address;
        this.photoPath = photoPath;
        this.children = children;
        this.details = details;

        this.timelineEvents = new ArrayList<TimelineEvent>();
    }

    public ECDetail addTimelineEvents(List<TimelineEvent> events) {
        this.timelineEvents = events;
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
