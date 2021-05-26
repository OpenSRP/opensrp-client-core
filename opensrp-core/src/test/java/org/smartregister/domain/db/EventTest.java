package org.smartregister.domain.db;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Obs;
import org.smartregister.domain.db.mock.EventMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class EventTest extends BaseUnitTest {
    EventMock event;

    String baseEntityId = "baseEntityId";
    String eventId = "eventId";
    String eventType = "eventType";
    DateTime eventDate = new DateTime(0l);
    String entityType = "entityType";
    String providerId = "providerId";
    String locationId = "locationId";
    String formSubmissionId = "formSubmissionId";

    @Before
    public void setUp() {
        event = new EventMock(baseEntityId, eventId, eventType, eventDate, entityType, providerId, locationId, formSubmissionId);
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(event);
    }


    public List<Obs> getObs() {
        return event.getObs();
    }

    @Test
    public void setObs() {
        event.setObs(null);
        List<Obs> obs = new ArrayList<>();
        Assert.assertNotNull(getObs());
        event.setObs(obs);
        Assert.assertEquals(getObs(), obs);
    }

    @Test
    public void addObs() {
        event.setObs(null);
        Obs observation = new Obs();
        event.addObs(observation);
        Assert.assertEquals(getObs().get(0), observation);
    }


    public String getBaseEntityId() {
        return event.getBaseEntityId();
    }

    @Test
    public void setBaseEntityId() {
        event.setBaseEntityId(baseEntityId);
        Assert.assertEquals(getBaseEntityId(), baseEntityId);
    }


    public String getLocationId() {
        return event.getLocationId();
    }

    @Test
    public void setLocationId() {
        event.setLocationId(locationId);
        Assert.assertEquals(getLocationId(), locationId);
    }


    public DateTime getEventDate() {
        return event.getEventDate();
    }

    @Test
    public void setEventDate() {
        event.setEventDate(eventDate);
        Assert.assertEquals(getEventDate(), eventDate);
    }


    public String getEventType() {
        return event.getEventType();
    }

    @Test
    public void setEventType() {
        event.setEventType(eventType);
        Assert.assertEquals(getEventType(), eventType);
    }


    public String getFormSubmissionId() {
        return event.getFormSubmissionId();
    }

    @Test
    public void setFormSubmissionId() {
        event.setFormSubmissionId(formSubmissionId);
        Assert.assertEquals(getFormSubmissionId(), formSubmissionId);
    }


    public String getProviderId() {
        return event.getProviderId();
    }

    @Test
    public void setProviderId() {
        event.setProviderId(providerId);
        Assert.assertEquals(getProviderId(), providerId);
    }


    public String getEventId() {
        return event.getEventId();
    }

    @Test
    public void setEventId() {
        event.setEventId(eventId);
        Assert.assertEquals(getEventId(), eventId);
    }


    public String getEntityType() {
        return event.getEntityType();
    }

    @Test
    public void setEntityType() {
        event.setEntityType(entityType);
        Assert.assertEquals(getEntityType(), entityType);
    }


    public Map<String, String> getDetails() {
        return event.getDetails();
    }

    Map<String, String> details = new HashMap<>();

    @Test
    public void setDetails() {
        event.setDetails(details);
        Assert.assertEquals(getDetails(), details);
    }

    @Test
    public void addDetails() {
        event.setDetails(null);
        event.addDetails("key", "val");
        Assert.assertEquals(event.getDetails().get("key"), "val");
    }


    public long getVersion() {
        return event.getVersion();
    }

    @Test
    public void setVersion() {
        event.setVersion(0l);
        Assert.assertEquals(getVersion(), 0l);
    }

    @Test
    public void findObs() {
        String parentId = "P";
        boolean nonEmpty = true;
        String[] fieldIds = {"f1", "f2"};
        Obs observation = new Obs();
        observation.setFieldCode("x");
        observation.setFormSubmissionField("x");
        observation.setParentCode("x");
        List<Obs> obslist = new ArrayList<>();
        obslist.add(observation);
        event.setObs(obslist);

        Assert.assertNull(event.findObs(parentId, true, fieldIds));
        observation = new Obs();
        List<Object> objectlist = new ArrayList<>();
        obslist = new ArrayList<>();
        objectlist.add(observation);
        observation.setValues(objectlist);
        observation.setFieldCode("f1");
        observation.setFormSubmissionField("x");
        observation.setParentCode("P");
        obslist.add(observation);
        event.setObs(obslist);

        Assert.assertNotNull(event.findObs(parentId, true, fieldIds));
        Assert.assertEquals(event.findObs(parentId, true, fieldIds), observation);
    }

    @Test
    public void withBaseEntityId() {
        Assert.assertNotNull(event.withBaseEntityId(baseEntityId));
    }

    @Test
    public void withLocationId() {
        Assert.assertNotNull(event.withLocationId(locationId));
    }

    @Test
    public void withEventDate() {
        Assert.assertNotNull(event.withEventDate(eventDate));
    }

    @Test
    public void withEventType() {
        Assert.assertNotNull(event.withEventType(eventType));
    }

    @Test
    public void withFormSubmissionId() {
        Assert.assertNotNull(event.withFormSubmissionId(formSubmissionId));
    }

    @Test
    public void withProviderId() {
        Assert.assertNotNull(event.withProviderId(providerId));
    }

    @Test
    public void withEntityType() {
        Assert.assertNotNull(event.withEntityType(entityType));
    }

    @Test
    public void withObsList() {
        List<Obs> obs = new ArrayList<>();
        Assert.assertNotNull(event.withObs(obs));
    }

    @Test
    public void withObs() {
        Obs observation = new Obs();
        event.setObs(null);
        Assert.assertNotNull(event.withObs(observation));
    }

}
