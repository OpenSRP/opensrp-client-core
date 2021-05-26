package org.smartregister.clientandeventmodel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.mock.EventMock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class EventTest extends BaseUnitTest {

    private EventMock event;

    private String baseEntityId = "baseEntityId";
    private String eventId = "eventId";
    private String eventType = "eventType";
    private Date eventDate = new Date(0l);
    private String entityType = "entityType";
    private String providerId = "providerId";
    private String locationId = "locationId";
    private String formSubmissionId = "formSubmissionId";
    private Map<String, String> details = new HashMap<>();
    private HashMap<String, String> identifiers = new HashMap<>();
    private String status = "done";

    @Before
    public void setUp() {
        event = new EventMock(baseEntityId, eventId, eventType, eventDate, entityType, providerId, locationId, formSubmissionId);
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(new EventMock());
        Assert.assertNotNull(event);
        Assert.assertNotNull(new EventMock(baseEntityId, new HashMap<String, String>(), eventId, eventType, eventDate, entityType, providerId, locationId, formSubmissionId));
    }

    public List<Obs> getObs() {
        return event.getObs();
    }

    @Test
    public void assertsetObs() {
        event.setObs(null);
        List<Obs> obs = new ArrayList<>();
        Assert.assertNull(getObs());
        event.setObs(obs);
        Assert.assertEquals(getObs(), obs);
    }

    @Test
    public void assertaddObs() {
        event.setObs(null);
        Obs observation = new Obs();
        event.addObs(observation);
        Assert.assertEquals(getObs().get(0), observation);
    }

    public String getBaseEntityId() {
        return event.getBaseEntityId();
    }

    @Test
    public void assertsetBaseEntityId() {
        event.setBaseEntityId(baseEntityId);
        Assert.assertEquals(getBaseEntityId(), baseEntityId);
    }

    public String getLocationId() {
        return event.getLocationId();
    }

    @Test
    public void assertsetLocationId() {
        event.setLocationId(locationId);
        Assert.assertEquals(getLocationId(), locationId);
    }

    public Date getEventDate() {
        return event.getEventDate();
    }

    @Test
    public void assertsetEventDate() {
        event.setEventDate(eventDate);
        Assert.assertEquals(getEventDate(), eventDate);
    }

    public String getEventType() {
        return event.getEventType();
    }

    @Test
    public void assertsetEventType() {
        event.setEventType(eventType);
        Assert.assertEquals(getEventType(), eventType);
    }

    public String getFormSubmissionId() {
        return event.getFormSubmissionId();
    }

    @Test
    public void assertsetFormSubmissionId() {
        event.setFormSubmissionId(formSubmissionId);
        Assert.assertEquals(getFormSubmissionId(), formSubmissionId);
    }

    public String getProviderId() {
        return event.getProviderId();
    }

    @Test
    public void assertsetProviderId() {
        event.setProviderId(providerId);
        Assert.assertEquals(getProviderId(), providerId);
    }

    public String getEventId() {
        return event.getEventId();
    }

    @Test
    public void assertsetEventId() {
        event.setEventId(eventId);
        Assert.assertEquals(getEventId(), eventId);
    }

    public String getEntityType() {
        return event.getEntityType();
    }

    @Test
    public void assertsetEntityType() {
        event.setEntityType(entityType);
        Assert.assertEquals(getEntityType(), entityType);
    }

    public Map<String, String> getDetails() {
        return event.getDetails();
    }

    @Test
    public void assertsetDetails() {
        event.setDetails(details);
        Assert.assertEquals(getDetails(), details);
    }

    @Test
    public void assertaddDetails() {
        event.setDetails(null);
        event.addDetails("key", "val");
        Assert.assertEquals(event.getDetails().get("key"), "val");
    }

    public long getVersion() {
        return event.getVersion();
    }

    @Test
    public void assertsetVersion() {
        event.setVersion(0l);
        Assert.assertEquals(getVersion(), 0l);
    }

    @Test
    public void assertwithBaseEntityId() {
        Assert.assertNotNull(event.withBaseEntityId(baseEntityId));
    }

    @Test
    public void assertwithLocationId() {
        Assert.assertNotNull(event.withLocationId(locationId));
    }

    @Test
    public void assertwithEventDate() {
        Assert.assertNotNull(event.withEventDate(eventDate));
    }

    @Test
    public void assertwithEventType() {
        Assert.assertNotNull(event.withEventType(eventType));
    }

    @Test
    public void assertwithFormSubmissionId() {
        Assert.assertNotNull(event.withFormSubmissionId(formSubmissionId));
    }

    @Test
    public void assertwithProviderId() {
        Assert.assertNotNull(event.withProviderId(providerId));
    }

    @Test
    public void assertwithEntityType() {
        Assert.assertNotNull(event.withEntityType(entityType));
    }

    @Test
    public void assertwithObsList() {
        List<Obs> obs = new ArrayList<>();
        Assert.assertNotNull(event.withObs(obs));
    }

    @Test
    public void assertwithObs() {
        Obs observation = new Obs();
        event.setObs(null);
        Assert.assertNotNull(event.withObs(observation));
    }

    public Map<String, String> getIdentifiers() {
        return event.getIdentifiers();
    }

    @Test
    public void assertsetIdentifiers() {
        event.setIdentifiers(identifiers);
        Assert.assertEquals(getIdentifiers(), identifiers);
    }

    @Test
    public void assertwithIdentifiers() {
        Assert.assertNotNull(event.withIdentifiers(identifiers));
    }

    @Test
    public void assertequals() {
        Object o = new Event();
        Assert.assertEquals(event.equals(o), false);
    }

    @Test
    public void asserthashCode() {
        Assert.assertNotNull(event.hashCode());
    }

    @Test
    public void asserttoString() {
        Assert.assertNotNull(event.toString());
    }

    public String getStatus() {
        return event.getStatus();
    }

    @Test
    public void assertsetStatus() {
        event.setStatus(status);
        Assert.assertEquals(getStatus(), status);
    }
}
