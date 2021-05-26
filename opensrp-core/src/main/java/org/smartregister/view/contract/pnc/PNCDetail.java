package org.smartregister.view.contract.pnc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.smartregister.view.contract.CoupleDetails;
import org.smartregister.view.contract.LocationDetails;
import org.smartregister.view.contract.PregnancyOutcomeDetails;
import org.smartregister.view.contract.TimelineEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PNCDetail {
    private final String caseId;
    private final String thayiCardNumber;

    private final LocationDetails location;
    private final CoupleDetails coupleDetails;
    private final PregnancyOutcomeDetails pncDetails;

    private List<TimelineEvent> timelineEvents;
    private Map<String, String> details;

    public PNCDetail(String caseId, String thayiCardNumber, CoupleDetails coupleDetails,
                     LocationDetails location, PregnancyOutcomeDetails pncDetails) {
        this.caseId = caseId;
        this.thayiCardNumber = thayiCardNumber;

        this.coupleDetails = coupleDetails;
        this.location = location;
        this.pncDetails = pncDetails;

        this.timelineEvents = new ArrayList<TimelineEvent>();
    }

    public PNCDetail addTimelineEvents(List<TimelineEvent> events) {
        this.timelineEvents.addAll(events);
        return this;
    }

    public PNCDetail addExtraDetails(Map<String, String> details) {
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
