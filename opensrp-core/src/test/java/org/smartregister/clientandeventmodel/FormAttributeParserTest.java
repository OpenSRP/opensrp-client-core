package org.smartregister.clientandeventmodel;

import android.content.res.AssetManager;

import com.google.gson.JsonParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
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
 * Created by kaderchowdhury on 22/11/17.
 */
public class FormAttributeParserTest extends BaseUnitTest {

    @Mock
    private AssetManager assetManager;
    @Mock
    private android.content.Context context;
    private FormAttributeParser parser;
    private String FORMNAME = "child_enrollment";
    private String SUBFORMNAME = "magic_subform";
    private String SOURCE = "www/form/";
    private String formDefinition = SOURCE + FORMNAME + "/form_definition.json";
    private String model = SOURCE + FORMNAME + "/model.xml";
    private String formJSON = SOURCE + FORMNAME + "/form.json";
    private String formMultiJSON = SOURCE + FORMNAME + "/form_multi.json";
    private String DEFAULT_BIND_PATH = "/model/instance/Child_Vaccination_Enrollment/";
    private String VALUES = "1 2 3 4";
    private String instanceId = "instanceID";
    private String birth_date_known = "birth_date_known";
    private String pkchild = "pkchild";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        parser = new FormAttributeParser(context);
    }

    @Test
    public void assertTestCreateFormSubmissionMap() throws Exception {
        InputStream formDefinitionStream = new FileInputStream(getFileFromPath(this, formDefinition));
        InputStream modelStream = new FileInputStream(getFileFromPath(this, model));
        InputStream formJSONStream = new FileInputStream(getFileFromPath(this, formJSON));
        List<FormField> formFields = new ArrayList<FormField>();
        formFields.add(new FormField("NULL", VALUES, SOURCE));
        formFields.add(new FormField(instanceId, VALUES, SOURCE));
        FormData fd = new FormData("bind_type", SOURCE, formFields,
                new ArrayList<SubFormData>());

        FormInstance fi = new FormInstance();
        fi.setForm(fd);
        FormSubmission fs = new FormSubmission("", "", FORMNAME, "", 0l, "", fi, 0l);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open(formDefinition)).thenReturn(formDefinitionStream);
        Mockito.when(assetManager.open(model)).thenReturn(modelStream);
        Mockito.when(assetManager.open(formJSON)).thenReturn(formJSONStream);
        Assert.assertNotNull(parser.createFormSubmissionMap(fs));
    }

    @Test
    public void assertTestCreateFormSubmissionMapMultiSelect() throws Exception {
        InputStream formDefinitionStream = new FileInputStream(getFileFromPath(this, formDefinition));
        InputStream modelStream = new FileInputStream(getFileFromPath(this, model));
        InputStream formJSONStream = new FileInputStream(getFileFromPath(this, formMultiJSON));
        List<FormField> formFields = new ArrayList<FormField>();

        formFields.add(new FormField(birth_date_known, VALUES, SOURCE));
        formFields.add(new FormField(instanceId, VALUES, SOURCE));

        List<SubFormData> subFormData = new ArrayList<SubFormData>();
        SubFormData sf = new SubFormData();
        sf.setName(SUBFORMNAME);
        List<Map<String, String>> instances = new ArrayList<>();
        HashMap<String, String> instance = new HashMap<>();
        instance.put(birth_date_known, VALUES);
        instances.add(instance);
        sf.setInstances(instances);
        sf.setDefault_bind_path(DEFAULT_BIND_PATH);
        subFormData.add(sf);

        sf = new SubFormData();
        sf.setName(SUBFORMNAME);
        instances = new ArrayList<>();
        instance = new HashMap<>();
        instance.put(instanceId, VALUES);
        instances.add(instance);
        sf.setInstances(instances);
        sf.setDefault_bind_path(DEFAULT_BIND_PATH);
        subFormData.add(sf);

        FormData fd = new FormData(pkchild, SOURCE, formFields, subFormData);

        FormInstance fi = new FormInstance();
        fi.setForm(fd);
        FormSubmission fs = new FormSubmission("", "", FORMNAME, "", 0l, "", fi, 0l);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open(formDefinition)).thenReturn(formDefinitionStream);
        Mockito.when(assetManager.open(model)).thenReturn(modelStream);
        Mockito.when(assetManager.open(formJSON)).thenReturn(formJSONStream);
        Assert.assertNotNull(parser.createFormSubmissionMap(fs));
    }

    @Test
    public void assertGetFieldname() throws Exception {
        InputStream formDefinitionStream = new FileInputStream(getFileFromPath(this, formDefinition));
        InputStream modelStream = new FileInputStream(getFileFromPath(this, model));
        InputStream formJSONStream = new FileInputStream(getFileFromPath(this, formMultiJSON));
        List<FormField> formFields = new ArrayList<FormField>();

        formFields.add(new FormField(birth_date_known, VALUES, SOURCE));
        formFields.add(new FormField(instanceId, VALUES, SOURCE));
        formFields.add(new FormField("first_name", "Ephraim Kigamba", SOURCE));

        List<SubFormData> subFormData = new ArrayList<SubFormData>();
        SubFormData sf = new SubFormData();
        sf.setName(SUBFORMNAME);
        List<Map<String, String>> instances = new ArrayList<>();
        HashMap<String, String> instance = new HashMap<>();
        instance.put(birth_date_known, VALUES);
        instances.add(instance);
        sf.setInstances(instances);
        sf.setDefault_bind_path("/model/instance/Child_Vaccination_Enrollment/birth_date_known");
        subFormData.add(sf);

        sf = new SubFormData();
        sf.setName(SUBFORMNAME);
        instances = new ArrayList<>();
        instance = new HashMap<>();
        instance.put(instanceId, VALUES);
        instances.add(instance);
        sf.setInstances(instances);
        sf.setDefault_bind_path("/model/instance/Child_Vaccination_Enrollment/meta/instanceID");
        subFormData.add(sf);

        FormData fd = new FormData(pkchild, SOURCE, formFields, subFormData);

        FormInstance fi = new FormInstance();
        fi.setForm(fd);
        FormSubmission fs = new FormSubmission("", "", FORMNAME, "", 0l, "", fi, 0l);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open(formDefinition)).thenReturn(formDefinitionStream);
        Mockito.when(assetManager.open(model)).thenReturn(modelStream);
        Mockito.when(assetManager.open(formJSON)).thenReturn(formJSONStream);

        HashMap<String, String> newAtributes = new HashMap<>();
        newAtributes.put("openmrs_entity", "concept");
        newAtributes.put("openmrs_entity_id", "1593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        Assert.assertEquals(parser.getFieldName(newAtributes, fs), "mother_name");
    }

    public String getStringFromStream(InputStream is) throws Exception {
        String fileContents = "";
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        fileContents = new String(buffer, "UTF-8");
        return fileContents;
    }

    @Test
    public void assertGetFieldnameSubForm() throws Exception {
        HashMap<String, String> newAtributes = new HashMap<>();
        newAtributes.put("openmrs_entity", "encounter");
        newAtributes.put("openmrs_entity_id", "bdk");

        List<FormField> formFields = new ArrayList<FormField>();

        formFields.add(new FormField(birth_date_known, VALUES, SOURCE));
        formFields.add(new FormField(instanceId, VALUES, SOURCE));

        List<SubFormData> subFormData = new ArrayList<SubFormData>();
        SubFormData sf = new SubFormData();
        sf.setName(SUBFORMNAME);
        List<Map<String, String>> instances = new ArrayList<>();
        HashMap<String, String> instance = new HashMap<>();
        instance.put(birth_date_known, VALUES);
        instances.add(instance);
        sf.setInstances(instances);
        sf.setDefault_bind_path("/model/instance/Child_Vaccination_Enrollment/birth_date_known");
        subFormData.add(sf);

        sf = new SubFormData();
        sf.setName(SUBFORMNAME);
        instances = new ArrayList<>();
        instance = new HashMap<>();
        instance.put(instanceId, VALUES);
        instances.add(instance);
        sf.setInstances(instances);
        sf.setDefault_bind_path("/model/instance/Child_Vaccination_Enrollment/meta/instanceID");
        subFormData.add(sf);

        FormData fd = new FormData(pkchild, SOURCE, formFields, subFormData);

        FormAttributeParser spyparser = PowerMockito.spy(parser);

        FormInstance fi = new FormInstance();
        fi.setForm(fd);
        FormSubmission fs = new FormSubmission("", "", FORMNAME, "", 0l, "", fi, 0l);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        FileInputStream formDefinitionIS = (new FileInputStream(getFileFromPath(this, formDefinition)));
        String stringOfFormDefinition = getStringFromStream(formDefinitionIS);

        Object obj = JsonParser.parseString(stringOfFormDefinition);
        Mockito.doReturn(obj).when(spyparser).getFormDefinitionData(FORMNAME);
        Mockito.when(assetManager.open(model)).thenReturn(new FileInputStream(getFileFromPath(this, model)));
        Mockito.when(assetManager.open(formJSON)).thenReturn(new FileInputStream(getFileFromPath(this, formMultiJSON)));

        Assert.assertEquals(spyparser.getFieldName(newAtributes, SUBFORMNAME, fs), birth_date_known);

    }

    private static File getFileFromPath(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
    }

    @Test
    public void fileObjectShouldNotBeNull() throws Exception {
        File file = getFileFromPath(this, model);
        Assert.assertNotNull(file);
    }

}
