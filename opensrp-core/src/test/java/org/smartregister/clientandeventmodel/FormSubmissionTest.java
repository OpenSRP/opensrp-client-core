package org.smartregister.clientandeventmodel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.mock.FormSubmissionMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaderchowdhury on 28/11/17.
 */

public class FormSubmissionTest extends BaseUnitTest {

    private FormSubmissionMock formSubmission;
    @Mock
    private FormInstance formInstance;
    @Mock
    private SubFormData subForm;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        formSubmission = new FormSubmissionMock("", "", "", "",
                "", 0l, formInstance);
        Mockito.when(formInstance.getSubFormByName(Mockito.anyString())).thenReturn(subForm);
    }

    @Test
    public void clientVersion() {
        Assert.assertEquals(formSubmission.clientVersion(), 0l);
    }

    @Test
    public void formDataDefinitionVersion() {
        Assert.assertEquals(formSubmission.formDataDefinitionVersion(), "");
    }

    @Test
    public void serverVersion() {
        formSubmission.setServerVersion(0l);
        Assert.assertEquals(formSubmission.serverVersion(), 0l);
    }

    @Test
    public void getField() {
        Assert.assertEquals(formSubmission.getField(""), null);
    }

    @Test
    public void getFields() {
        List<String> fields = new ArrayList<>();
        fields.add("");
        Assert.assertNotNull(formSubmission.getFields(fields));
    }

    @Test
    public void getInstanceId() {
        Assert.assertEquals(formSubmission.getInstanceId(), "");
    }

    @Test
    public void getSubFormByName() {
        Assert.assertNotNull(formSubmission.getSubFormByName(""));
    }

    public void getMetadata() {
        Assert.assertEquals(formSubmission.getMetadata(), null);
    }

    @Test
    public void assertequals() {
        Assert.assertEquals(formSubmission.equals(formSubmission), true);
    }

    @Test
    public void asserthashCode() {
        Assert.assertNotNull(formSubmission.hashCode());
    }

    @Test
    public void asserttoString() {
        Assert.assertNotNull(formSubmission.toString());
    }

}
