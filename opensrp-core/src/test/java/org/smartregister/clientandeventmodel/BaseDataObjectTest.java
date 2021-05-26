package org.smartregister.clientandeventmodel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.mock.BaseDataObjectMock;

import java.util.Date;

/**
 * Created by kaderchowdhury on 22/11/17.
 */

public class BaseDataObjectTest extends BaseUnitTest {

    private BaseDataObjectMock baseDataObject;
    private User user = new User();
    private Date date = new Date(0l);
    private String reasonString = "reason";

    @Before
    public void setUp() {
        baseDataObject = new BaseDataObjectMock();
    }

    public User getCreator() {
        return baseDataObject.getCreator();
    }

    @Test
    public void assertsetCreator() {
        baseDataObject.setCreator(user);
        Assert.assertEquals(getCreator(), user);
    }

    public Date getDateCreated() {
        return baseDataObject.getDateCreated();
    }

    @Test
    public void assertsetDateCreated() {
        baseDataObject.setDateCreated(date);
        Assert.assertEquals(getDateCreated(), date);
    }

    public User getEditor() {
        return baseDataObject.getEditor();
    }

    @Test
    public void assertsetEditor() {
        baseDataObject.setEditor(user);
        Assert.assertEquals(getEditor(), user);
    }

    public Date getDateEdited() {
        return baseDataObject.getDateEdited();
    }

    @Test
    public void assertsetDateEdited() {
        baseDataObject.setDateEdited(date);
        Assert.assertEquals(getDateEdited(), date);
    }

    public Boolean getVoided() {
        return baseDataObject.getVoided();
    }

    @Test
    public void assertsetVoided() {
        baseDataObject.setVoided(Boolean.TRUE);
        Assert.assertEquals(getVoided(), Boolean.TRUE);
    }

    public Date getDateVoided() {
        return baseDataObject.getDateVoided();
    }

    @Test
    public void assertsetDateVoided() {
        baseDataObject.setDateVoided(date);
        Assert.assertEquals(getDateVoided(), date);
    }

    public User getVoider() {
        return baseDataObject.getVoider();
    }

    @Test
    public void assertsetVoider() {
        baseDataObject.setVoider(user);
        Assert.assertEquals(getVoider(), user);
    }

    public String getVoidReason() {
        return baseDataObject.getVoidReason();
    }

    @Test
    public void assertsetVoidReason() {
        baseDataObject.setVoidReason(reasonString);
        Assert.assertEquals(getVoidReason(), reasonString);
    }

    @Test
    public void assertwithCreator() {
        Assert.assertNotNull(baseDataObject.withCreator(user));
    }

    @Test
    public void assertwithDateCreated() {
        Assert.assertNotNull(baseDataObject.withDateCreated(date));
    }

    @Test
    public void assertwithEditor() {
        Assert.assertNotNull(baseDataObject.withEditor(user));
    }

    @Test
    public void assertwithDateEdited() {
        Assert.assertNotNull(baseDataObject.withDateEdited(date));
    }

    @Test
    public void assertwithVoided() {
        Assert.assertNotNull(baseDataObject.withVoided(Boolean.TRUE));
    }

    @Test
    public void assertwithDateVoided() {
        Assert.assertNotNull(baseDataObject.withDateVoided(date));
    }

    @Test
    public void assertwithVoider() {
        Assert.assertNotNull(baseDataObject.withVoider(user));
    }

    @Test
    public void assertwithVoidReason() {
        Assert.assertNotNull(baseDataObject.withVoidReason(""));
    }

    @Test
    public void asserttoString() {
        Assert.assertNotNull(baseDataObject.toString());
    }
}
