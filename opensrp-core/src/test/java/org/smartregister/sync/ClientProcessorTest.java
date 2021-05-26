package org.smartregister.sync;

import android.content.ContentValues;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.TestSyncConfiguration;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.sync.mock.MockEditor;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by raihan on 11/6/17.
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*", "org.powermock.*"})
public class ClientProcessorTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ClientProcessor clientProcessor;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private DetailsRepository detailsRepository;
    @Mock
    private CloudantDataHandler cloudantDataHandler;
    @Mock
    private CommonRepository cr;

    @Mock
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        doReturn(new TestSyncConfiguration()).when(coreLibrary).getSyncConfiguration();
        doReturn(context).when(coreLibrary).context();
        doReturn("ec_client_fields.json").when(coreLibrary).getEcClientFieldsFile();

        when(context.detailsRepository()).thenReturn(detailsRepository);
        when(context.commonrepository(Mockito.anyString())).thenReturn(cr);
        when(cr.executeInsertStatement(Mockito.any(ContentValues.class), Mockito.anyString())).thenReturn(1l);
        context.updateApplicationContext(RuntimeEnvironment.application);

        clientProcessor = spy(new ClientProcessor(context.applicationContext()));

        doReturn(ClientData.clientClassificationJson).when(clientProcessor).getFileContents("ec_client_classification.json");
        doReturn(ClientData.ec_client_fields_json).when(clientProcessor).getFileContents("ec_client_fields.json");
        doReturn(ClientData.ec_client_alerts).when(clientProcessor).getFileContents("ec_client_alerts.json");

        Whitebox.setInternalState(clientProcessor, "mCloudantDataHandler", cloudantDataHandler);
        Whitebox.setInternalState(clientProcessor, "preferences", sharedPreferences);
    }

    @Test
    public void processClientTest() throws Exception {
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }


        PowerMockito.when(cloudantDataHandler.getUpdatedEventsAndAlerts(Mockito.any(Date.class))).thenReturn(eventList);
        PowerMockito.when(cloudantDataHandler.getClientByBaseEntityId(Mockito.anyString())).thenReturn(clientList.get(0));


        PowerMockito.when(sharedPreferences.getLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(0l);
        SharedPreferences.Editor edit = Mockito.mock(SharedPreferences.Editor.class);
        PowerMockito.when(sharedPreferences.edit()).thenReturn(edit);
        PowerMockito.when(edit.putLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(MockEditor.getEditor());

        clientProcessor.processClient();
        clientProcessor.processClient(eventList);
    }

    @Test
    public void assertProcessFieldReturnsTrue() throws Exception {
        final JSONObject fieldObject = new JSONObject(ClientData.ec_client_fields_json);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }

        JSONArray fieldArray = fieldObject.getJSONArray("bindobjects");
        Assert.assertEquals(clientProcessor.processField(fieldArray.getJSONObject(0), eventList.get(0), clientList.get(0)), Boolean.TRUE);
//        for(int i=0;i<fieldArray.length();i++){
//            JSONArray columns = fieldArray.getJSONObject(i).getJSONArray("columns");
//            for(int j =0;j<columns.length();j++){
//      Assert.assertEquals(clientProcessor.processField(columns.getJSONObject(j).getJSONObject("json_mapping"),eventList.get(0),clientList.get(0)),Boolean.TRUE);
//            }
//
//        }

    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCall() throws Exception {
        Assert.assertNotNull(new ClientProcessor(context.applicationContext()));
        Assert.assertNotNull(ClientProcessor.getInstance(context.applicationContext()));

    }

    @Test
    public void ProcessEventReturnsNotNull() throws Exception {

        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }
        Assert.assertNotNull(clientProcessor.processEvent(eventList.get(0), clientList.get(0), new JSONObject(ClientData.clientClassificationJson)));

    }

    @Test
    public void getClientAddressAsMapReturnsNotNull() throws Exception {

        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }
        Assert.assertNotNull(clientProcessor.getClientAddressAsMap(clientList.get(0)));

    }

    @Test
    public void updateClientDetailsTableCallsSaveClientDetails() throws Exception {

        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }
        ClientProcessor clientProcessorspy = spy(clientProcessor);
        clientProcessorspy.updateClientDetailsTable(eventList.get(0), clientList.get(0));
        Mockito.verify(clientProcessorspy, Mockito.atLeastOnce()).saveClientDetails(anyString(), ArgumentMatchers.<String, String>anyMap(), anyLong());
    }

    @Test
    public void processCaseModelReturnsNotNUll() throws Exception {

        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }

        JSONObject clientClassification = new JSONObject(ClientData.clientClassificationJson);
        JSONArray clientClassificationArray = clientClassification.getJSONArray("case_classification_rules");
        JSONObject clientClassificationRulesObject = clientClassificationArray.getJSONObject(0);
        JSONArray creates_case = clientClassificationRulesObject.getJSONObject("rule").getJSONArray("fields").getJSONObject(0).getJSONArray("creates_case");
        Boolean b = clientProcessor.processCaseModel(eventList.get(0), clientList.get(0), creates_case);
        Assert.assertEquals(Boolean.TRUE, b);

    }
}
