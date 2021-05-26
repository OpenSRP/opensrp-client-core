package org.smartregister.domain.db.mock;

import org.joda.time.DateTime;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;

import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class EventMock extends Event {


    public EventMock(String baseEntityId, String eventId, String eventType, DateTime eventDate, String entityType, String providerId, String locationId, String formSubmissionId) {
        super(baseEntityId, eventId,eventType, eventDate, entityType, providerId, locationId, formSubmissionId,null,null);
    }

    @Override
    public List<Obs> getObs() {
        return super.getObs();
    }

    @Override
    public void setObs(List<Obs> obs) {
        super.setObs(obs);
    }

    @Override
    public void addObs(Obs observation) {
        super.addObs(observation);
    }

    @Override
    public String getBaseEntityId() {
        return super.getBaseEntityId();
    }

    @Override
    public void setBaseEntityId(String baseEntityId) {
        super.setBaseEntityId(baseEntityId);
    }

    @Override
    public String getLocationId() {
        return super.getLocationId();
    }

    @Override
    public void setLocationId(String locationId) {
        super.setLocationId(locationId);
    }

    @Override
    public DateTime getEventDate() {
        return super.getEventDate();
    }

    @Override
    public void setEventDate(DateTime eventDate) {
        super.setEventDate(eventDate);
    }

    @Override
    public String getEventType() {
        return super.getEventType();
    }

    @Override
    public void setEventType(String eventType) {
        super.setEventType(eventType);
    }

    @Override
    public String getFormSubmissionId() {
        return super.getFormSubmissionId();
    }

    @Override
    public void setFormSubmissionId(String formSubmissionId) {
        super.setFormSubmissionId(formSubmissionId);
    }

    @Override
    public String getProviderId() {
        return super.getProviderId();
    }

    @Override
    public void setProviderId(String providerId) {
        super.setProviderId(providerId);
    }

    @Override
    public String getEventId() {
        return super.getEventId();
    }

    @Override
    public void setEventId(String eventId) {
        super.setEventId(eventId);
    }

    @Override
    public String getEntityType() {
        return super.getEntityType();
    }

    @Override
    public void setEntityType(String entityType) {
        super.setEntityType(entityType);
    }

    @Override
    public Map<String, String> getDetails() {
        return super.getDetails();
    }

    @Override
    public void setDetails(Map<String, String> details) {
        super.setDetails(details);
    }

    @Override
    public void addDetails(String key, String val) {
        super.addDetails(key, val);
    }

    @Override
    public long getVersion() {
        return super.getVersion();
    }

    @Override
    public void setVersion(long version) {
        super.setVersion(version);
    }

    @Override
    public Obs findObs(String parentId, boolean nonEmpty, String... fieldIds) {
        return super.findObs(parentId, nonEmpty, fieldIds);
    }

    @Override
    public Event withBaseEntityId(String baseEntityId) {
        return super.withBaseEntityId(baseEntityId);
    }

    @Override
    public Event withLocationId(String locationId) {
        return super.withLocationId(locationId);
    }

    @Override
    public Event withEventDate(DateTime eventDate) {
        return super.withEventDate(eventDate);
    }

    @Override
    public Event withEventType(String eventType) {
        return super.withEventType(eventType);
    }

    @Override
    public Event withFormSubmissionId(String formSubmissionId) {
        return super.withFormSubmissionId(formSubmissionId);
    }

    @Override
    public Event withProviderId(String providerId) {
        return super.withProviderId(providerId);
    }

    @Override
    public Event withEntityType(String entityType) {
        return super.withEntityType(entityType);
    }

    public Event withObs(List<Obs> obs) {
        return super.withObs(obs);
    }

    @Override
    public Event withObs(Obs observation) {
        return super.withObs(observation);
    }
}
