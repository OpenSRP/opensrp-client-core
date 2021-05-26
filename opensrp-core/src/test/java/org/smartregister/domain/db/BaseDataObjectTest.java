package org.smartregister.domain.db;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.BaseDataObject;
import org.smartregister.domain.User;
import org.smartregister.domain.db.mock.BaseDataObjectMock;

import static org.junit.Assert.assertEquals;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class BaseDataObjectTest extends BaseUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private BaseDataObjectMock baseDataObject;

    @Mock
    private User user;

    @Before
    public void setUp() {
        baseDataObject = new BaseDataObjectMock();
    }


    @Test
    public void assertSetCreator() {
        baseDataObject.setCreator(this.user);
        assertEquals(baseDataObject.getCreator(), user);
    }


    public DateTime getDateCreated() {
        return baseDataObject.getDateCreated();
    }

    @Test
    public void assertSetDateCreated() {
        DateTime dateCreated = new DateTime(0l);
        baseDataObject.setDateCreated(dateCreated);
        assertEquals(getDateCreated(), dateCreated);
    }


    @Test
    public void assertSetEditor() {
        baseDataObject.setEditor(user);
        assertEquals(baseDataObject.getEditor(), user);
    }


    public DateTime getDateEdited() {
        return baseDataObject.getDateEdited();
    }

    @Test
    public void setDateEdited() {
        DateTime dateEdited = new DateTime(0l);
        baseDataObject.setDateEdited(dateEdited);
        Assert.assertEquals(getDateEdited(), dateEdited);
    }


    public Boolean getVoided() {
        return baseDataObject.getVoided();
    }

    @Test
    public void setVoided() {
        Boolean voided = Boolean.TRUE;
        baseDataObject.setVoided(voided);
        Assert.assertEquals(getVoided(), voided);
    }


    public DateTime getDateVoided() {
        return baseDataObject.getDateVoided();
    }

    @Test
    public void setDateVoided() {
        DateTime dateVoided = new DateTime(0l);
        baseDataObject.setDateVoided(dateVoided);
        Assert.assertEquals(getDateVoided(), dateVoided);
    }


    @Test
    public void assertSetVoider() {
        baseDataObject.setVoider(user);
        assertEquals(baseDataObject.getVoider(), user);
    }


    public String getVoidReason() {
        return baseDataObject.getVoidReason();
    }

    @Test
    public void assertsetVoidReason() {
        String voidReason = "voidreadsom";
        baseDataObject.setVoidReason(voidReason);
        Assert.assertEquals(getVoidReason(), voidReason);
    }


    public long getServerVersion() {
        return baseDataObject.getServerVersion();
    }

    @Test
    public void assertSetServerVersion() {
        long serverVersion = 0l;
        baseDataObject.setServerVersion(serverVersion);
        Assert.assertEquals(getServerVersion(), serverVersion);
    }


    public BaseDataObject withDateCreated(DateTime dateCreated) {
        return baseDataObject.withDateCreated(dateCreated);
    }


    public BaseDataObject withEditor(String editor) {
        return baseDataObject.withEditor(user);
    }


    public BaseDataObject withDateEdited(DateTime dateEdited) {
        return baseDataObject.withDateEdited(dateEdited);
    }


    public BaseDataObject withVoided(Boolean voided) {
        return baseDataObject.withVoided(voided);
    }


    public BaseDataObject withDateVoided(DateTime dateVoided) {
        return baseDataObject.withDateVoided(dateVoided);
    }


    @Test
    public void assertBaseObjectNotNull() {
        Assert.assertNotNull(baseDataObject.withCreator(user));
        Assert.assertNotNull(withDateCreated(new DateTime(0l)));
        Assert.assertNotNull(withDateEdited(new DateTime(0l)));
        Assert.assertNotNull(withDateVoided(new DateTime(0l)));
        Assert.assertNotNull(withEditor(""));
        Assert.assertNotNull(baseDataObject.withServerVersion(0l));
        Assert.assertNotNull(baseDataObject.withVoider(user));
        Assert.assertNotNull(withVoided(Boolean.TRUE));
        Assert.assertNotNull(baseDataObject.withVoidReason(""));
    }
}
