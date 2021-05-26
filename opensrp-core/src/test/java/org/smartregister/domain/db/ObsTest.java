package org.smartregister.domain.db;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Obs;
import org.smartregister.domain.db.mock.ObsMock;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class ObsTest extends BaseUnitTest {

    private String fieldType = "filedType";

    private String fieldDataType = "dataType";

    private String fieldCode = "fieldCode";

    private String parentCode = "parentCode";

    private List<Object> values = new ArrayList<>();

    private String comments = "good job";

    private String formSubmissionField = "FSField";

    private List<Object> humanReadableValues = new ArrayList<>();
    ObsMock ob;

    @Before
    public void setUp() {
        ob = new ObsMock(fieldType, fieldDataType, fieldCode, parentCode, values, comments, formSubmissionField, humanReadableValues);
    }

    @Test
    public void assertConstructorNotNull() {
        assertNotNull(ob);
    }


    public String getFieldType() {
        return ob.getFieldType();
    }

    @Test
    public void setFieldType() {
        ob.setFieldType(fieldType);
        assertEquals(getFieldType(), fieldType);
    }


    public String getFieldDataType() {
        return ob.getFieldDataType();
    }

    @Test
    public void setFieldDataType() {
        ob.setFieldDataType(fieldDataType);
        assertEquals(getFieldDataType(), fieldDataType);
    }


    public String getFieldCode() {
        return ob.getFieldCode();
    }

    @Test
    public void setFieldCode() {
        ob.setFieldCode(fieldCode);
        assertEquals(getFieldCode(), fieldCode);
    }


    public String getParentCode() {
        return ob.getParentCode();
    }

    @Test
    public void setParentCode() {
        ob.setParentCode(parentCode);
        assertEquals(getParentCode(), parentCode);
    }


    public Object getValue() {
        return ob.getValue();
    }

    @Test
    public void setValue() {
        Object value = new Object();
        ob.setValue(value);
        assertEquals(value.toString(),getValue());
    }


    public List<Object> getValues() {
        return ob.getValues();
    }

    @Test
    public void setValues() {
        ob.setValues(values);
        assertEquals(getValues(), values);
    }


    public String getComments() {
        return ob.getComments();
    }

    @Test
    public void setComments() {
        ob.setComments(comments);
        assertEquals(getComments(), comments);
    }


    public String getFormSubmissionField() {
        return ob.getFormSubmissionField();
    }

    @Test
    public void setFormSubmissionField() {
        ob.setFormSubmissionField(formSubmissionField);
        assertEquals(getFormSubmissionField(), formSubmissionField);
    }

    @Test
    public void withFieldType() {
        assertNotNull(ob.withFieldType(fieldType));
    }

    @Test
    public void withFieldDataType() {
        assertNotNull(ob.withFieldDataType(fieldDataType));
    }

    @Test
    public void withFieldCode() {
        assertNotNull(ob.withFieldCode(fieldCode));
    }

    @Test
    public void withParentCode() {
        assertNotNull(ob.withParentCode(parentCode));
    }

    @Test
    public void withValue() {
        Object value = new Object();
        assertNotNull(ob.withValue(value));
    }

    @Test
    public void withValues() {
        assertNotNull(ob.withValues(values));
    }

    @Test
    public void addToValueList() {
        Object value = new Object();
        assertNotNull(ob.addToValueList(value));
    }

    @Test
    public void withComments() {
        assertNotNull(ob.withComments(comments));
    }

    @Test
    public void withFormSubmissionField() {
        assertNotNull(ob.withFormSubmissionField(formSubmissionField));
    }

    @Test
    public void testConstructor() {
        List<Object> values = new ArrayList<>();
        List<Object> humanReadableValues = new ArrayList<>();
        Obs obs = new Obs("fieldType", "fieldDataType", "fieldCode", "parentCode", values,
                "comments", "formSubmissionField", humanReadableValues, true);
        assertEquals(obs.getFieldType(), "fieldType");
        assertEquals(obs.getFieldDataType(), "fieldDataType");
        assertEquals(obs.getFieldCode(), "fieldCode");
        assertEquals(obs.getParentCode(), "parentCode");
        assertEquals(obs.getValues(), values);
        assertEquals(obs.getComments(), "comments");
        assertEquals(obs.getFormSubmissionField(), "formSubmissionField");
        assertEquals(obs.getHumanReadableValues(), humanReadableValues);
        assertTrue(obs.isSaveObsAsArray());

        obs = new Obs();
        obs.withsaveObsAsArray(false);
        assertFalse(obs.isSaveObsAsArray());
    }
}
