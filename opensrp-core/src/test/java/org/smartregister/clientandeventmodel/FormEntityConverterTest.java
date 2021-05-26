package org.smartregister.clientandeventmodel;

import android.content.res.AssetManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 23/11/17.
 */

public class FormEntityConverterTest extends BaseUnitTest {

    private FormEntityConverter formEntityConverter;

    private String FORMNAME = "child_enrollment";
    private String formDefinition = "www/form/" + FORMNAME + "/form_definition.json";
    private String model = "www/form/" + FORMNAME + "/model.xml";
    private String formJSON = "www/form/" + FORMNAME + "/form.json";
    @Mock
    private AssetManager assetManager;
    @Mock
    private android.content.Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FormAttributeParser formAttributeParser = new FormAttributeParser(context);
        formEntityConverter = new FormEntityConverter(formAttributeParser, context);
    }

    @Test
    public void assertGetEventFromFormSubmission() throws Exception {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("encounter_type", "Child Vaccination Enrollment");
        FormData fd = new FormData();
        fd.setBindType("pkchild");
        FormInstance formInstance = new FormInstance();
        formInstance.setForm(fd);
        FormSubmission fs = new FormSubmission("", "", "", "entityId", 0l, "", formInstance, 0l);
        List<FormFieldMap> fields = new ArrayList<>();
        fields.add(getFormFieldMap(FormEntityConstants.Encounter.encounter_date.entity(), FormEntityConstants.Encounter.encounter_date.entityId()));
        fields.add(getFormFieldMap(FormEntityConstants.Encounter.location_id.entity(), FormEntityConstants.Encounter.location_id.entityId()));
        fields.add(getFormFieldMap(FormEntityConstants.Encounter.encounter_start.entity(), FormEntityConstants.Encounter.encounter_start.entityId()));
        fields.add(getFormFieldMap(FormEntityConstants.Encounter.encounter_end.entity(), FormEntityConstants.Encounter.encounter_end.entityId()));
        FormSubmissionMap fsmap = new FormSubmissionMap(fs, attributes, fields, null);
        Assert.assertNotNull(formEntityConverter.getEventFromFormSubmission(fsmap));
    }

    @Test
    public void assertGetEventFromFormSubmissionReturnsEvent() throws Exception {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("encounter_type", "Child Vaccination Enrollment");
        List<FormField> formFields = new ArrayList<FormField>();
        formFields.add(new FormField("NULL", "value", "www/form/"));
        formFields.add(new FormField("instanceID", "value", "www/form/"));
        FormData fd = new FormData("bind_type", "www/form/", formFields,
                new ArrayList<SubFormData>());
        FormInstance formInstance = new FormInstance();
        formInstance.setForm(fd);
        FormSubmission fs = new FormSubmission("", "", FORMNAME, "entityId", 0l, "", formInstance, 0l);
        InputStream formDefinitionStream = new FileInputStream(getFileFromPath(this, formDefinition));
        InputStream modelStream = new FileInputStream(getFileFromPath(this, model));
        InputStream formJSONStream = new FileInputStream(getFileFromPath(this, formJSON));
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open(formDefinition)).thenReturn(formDefinitionStream);
        Mockito.when(assetManager.open(model)).thenReturn(modelStream);
        Mockito.when(assetManager.open(formJSON)).thenReturn(formJSONStream);

        Assert.assertNotNull(formEntityConverter.getEventFromFormSubmission(fs));
    }

    private static File getFileFromPath(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
    }

    public FormFieldMap getFormFieldMap(String entity, String entity_id) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("openmrs_entity", "concept");

        attributes.put("openmrs_entity_id", entity_id);

        Map<String, String> codes = new HashMap<>();

        codes.put("openmrs_code", "CODE:RED");

        return new FormFieldMap(entity, "2017-10-10", "", "", "", attributes, codes);
    }

    public void assertGetEventFromFormSubmissionMock() throws Exception {
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        FormSubmissionMap fs = Mockito.mock(FormSubmissionMap.class);
        Mockito.when(fs.entityId()).thenReturn("entityId");
        Mockito.when(fs.formAttributes()).thenReturn(attributes);
        Mockito.when(attributes.get("encounter_type")).thenReturn("Child Vaccination Enrollment");
        Mockito.when(fs.getFieldValue(Mockito.isNull(String.class))).thenReturn("2017-10-10");
    }
}
