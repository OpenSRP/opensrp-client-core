package org.smartregister.clientandeventmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event extends BaseDataObject {

    @JsonProperty
    private String eventId;
    @JsonProperty
    private String baseEntityId;
    @JsonProperty
    private Map<String, String> identifiers;
    @JsonProperty
    private String locationId;
    @JsonProperty
    private String childLocationId;
    @JsonProperty
    private Date eventDate;
    @JsonProperty
    private String eventType;
    @JsonProperty
    private String formSubmissionId;
    @JsonProperty
    private String providerId;
    @JsonProperty
    private String status;
    @JsonProperty
    private Map<String, Date> statusHistory;
    @JsonProperty
    private String priority;
    @JsonProperty
    private List<String> episodeOfCare;
    @JsonProperty
    private List<String> referrals;
    @JsonProperty
    private String category;
    @JsonProperty
    private int duration;
    @JsonProperty
    private String reason;
    @JsonProperty
    private List<Obs> obs;
    @JsonProperty
    private String entityType;

    @JsonProperty
    private Map<String, String> details;
    @JsonProperty
    private long version;
    @JsonProperty
    private List<Photo> photos;
    @JsonProperty
    private String team;
    @JsonProperty
    private String teamId;

    @JsonProperty
    private String syncStatus;

    public Event() {
        this.version = System.currentTimeMillis();
    }

    public Event(String baseEntityId, String eventId, String eventType, Date eventDate, String
            entityType, String providerId, String locationId, String formSubmissionId) {
        this.baseEntityId = baseEntityId;
        this.identifiers = new HashMap<>();
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.entityType = entityType;
        this.providerId = providerId;
        this.locationId = locationId;
        this.formSubmissionId = formSubmissionId;
        this.version = System.currentTimeMillis();
    }

    public Event(String baseEntityId, HashMap<String, String> identifiers, String eventId, String
            eventType, Date eventDate, String entityType, String providerId, String locationId,
                 String formSubmissionId) {
        this.baseEntityId = baseEntityId;
        this.identifiers = identifiers;
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.entityType = entityType;
        this.providerId = providerId;
        this.locationId = locationId;
        this.formSubmissionId = formSubmissionId;
        this.version = System.currentTimeMillis();
    }

    public List<Obs> getObs() {
        return obs;
    }

    /**
     * WARNING: Overrides all existing obs
     *
     * @param obs
     * @return
     */
    public void setObs(List<Obs> obs) {
        this.obs = obs;
    }

    public void addObs(Obs observation) {
        if (obs == null) {
            obs = new ArrayList<>();
        }

        obs.add(observation);
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public Map<String, String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Map<String, String> identifiers) {
        this.identifiers = identifiers;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getChildLocationId() {
        return childLocationId;
    }

    public void setChildLocationId(String childLocationId) {
        this.childLocationId = childLocationId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public void addDetails(String key, String val) {
        if (details == null) {
            details = new HashMap<>();
        }
        details.put(key, val);
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Event withBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
        return this;
    }

    public Event withIdentifiers(HashMap<String, String> identifiers) {
        this.identifiers = identifiers;
        return this;
    }

    public Event addIdentifier(String key, String value) {
        if (identifiers == null) {
            identifiers = new HashMap<>();
        }
        identifiers.put(key, value);
        return this;
    }

    public Event withLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    public Event withChildLocationId(String childLocationId) {
        this.childLocationId = childLocationId;
        return this;
    }

    public Event withEventDate(Date eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public Event withEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public Event withFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
        return this;
    }

    public Event withProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public Event withEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public Event withTeam(String team) {
        this.team = team;
        return this;
    }

    public Event withTeamId(String teamId) {
        this.teamId = teamId;
        return this;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Event withSyncStatus(String syncStatus) {
        setSyncStatus(syncStatus);
        return this;
    }


    /**
     * WARNING: Overrides all existing obs
     *
     * @param obs
     * @return
     */
    public Event withObs(List<Obs> obs) {
        this.obs = obs;
        return this;
    }

    public Event withObs(Obs observation) {
        if (obs == null) {
            obs = new ArrayList<>();
        }
        obs.add(observation);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "_id", "_rev");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "_id", "_rev");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

