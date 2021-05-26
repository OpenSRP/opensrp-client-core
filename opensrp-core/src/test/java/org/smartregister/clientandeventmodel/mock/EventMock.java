package org.smartregister.clientandeventmodel.mock;

import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class EventMock extends Event {
    public EventMock() {
        super();
    }

    public EventMock(String baseEntityId, String eventId, String eventType, Date eventDate, String entityType, String providerId, String locationId, String formSubmissionId) {
        super(baseEntityId, eventId, eventType, eventDate, entityType, providerId, locationId, formSubmissionId);
    }

    public EventMock(String baseEntityId, HashMap<String, String> identifiers, String eventId, String eventType, Date eventDate, String entityType, String providerId, String locationId, String formSubmissionId) {
        super(baseEntityId, identifiers, eventId, eventType, eventDate, entityType, providerId, locationId, formSubmissionId);
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
    public Date getEventDate() {
        return super.getEventDate();
    }

    @Override
    public void setEventDate(Date eventDate) {
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

//    @Override
//    public Obs findObs(String parentId, boolean nonEmpty, String... fieldIds) {
//        return super.findObs(parentId, nonEmpty, fieldIds);
//    }

    @Override
    public Event withBaseEntityId(String baseEntityId) {
        return super.withBaseEntityId(baseEntityId);
    }

    @Override
    public Event withLocationId(String locationId) {
        return super.withLocationId(locationId);
    }

    @Override
    public Event withEventDate(Date eventDate) {
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

    @Override
    public Event withObs(List<Obs> obs) {
        return super.withObs(obs);
    }

    @Override
    public Event withObs(Obs observation) {
        return super.withObs(observation);
    }

    @Override
    public Map<String, String> getIdentifiers() {
        return super.getIdentifiers();
    }

    @Override
    public void setIdentifiers(Map<String, String> identifiers) {
        super.setIdentifiers(identifiers);
    }

    @Override
    public Event withIdentifiers(HashMap<String, String> identifiers) {
        return super.withIdentifiers(identifiers);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
    }
}
