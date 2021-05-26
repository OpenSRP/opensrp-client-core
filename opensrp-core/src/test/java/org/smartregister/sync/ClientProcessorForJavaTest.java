package org.smartregister.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.Obs;
import org.smartregister.domain.jsonmapping.ClassificationRule;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.ColumnType;
import org.smartregister.domain.jsonmapping.Field;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.shadows.ShadowAssetHandler;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.JsonFormUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.smartregister.sync.ClientProcessorForJava.JSON_ARRAY;


public class ClientProcessorForJavaTest extends BaseUnitTest {
    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private org.smartregister.Context opensrpContext;


    @Mock
    private Context context;

    @Captor
    private ArgumentCaptor<String> closeCaseArgumentCaptor;

    @Captor
    private ArgumentCaptor detailsRepositoryAddArgumentCaptor;

    private ClientProcessorForJava clientProcessor;
    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .serializeNulls().create();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        clientProcessor = new ClientProcessorForJava(context);
    }


    @Test
    public void testGetFormattedValueDate() throws Exception {
        Column column = new Column();
        column.dataType = ColumnType.Date;
        column.saveFormat = "yyyy-MM-dd";
        column.sourceFormat = "dd-MM-yyyy";
        String columnValue = "16-04-2019";

        String res = Whitebox.invokeMethod(clientProcessor, "getFormattedValue", column, columnValue);

        assertEquals(res, "2019-04-16");
    }

    @Test
    public void testGetFormattedValueString() throws Exception {
        clientProcessor = new ClientProcessorForJava(context);

        Column column = new Column();
        column.dataType = ColumnType.String;
        column.saveFormat = "Sheila is %s";
        String columnValue = "smart";

        String res = Whitebox.invokeMethod(clientProcessor, "getFormattedValue", column, columnValue);

        assertEquals(res, "Sheila is smart");
    }


    @Test
    public void testProcessEventMarksItSaved() throws Exception {
        ClientProcessorForJava clientProcessorForJava = Mockito.spy(new ClientProcessorForJava(context));
        Event mockEvent = Mockito.mock(Event.class);
        clientProcessorForJava.processEvent(mockEvent, null, null);

        Mockito.verify(clientProcessorForJava).completeProcessing(mockEvent);
    }

    @Test
    public void testGetValuesStrShouldGetCorrectlyFormattedString() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        List<String> values = new ArrayList<>();
        Obs obs = new Obs();
        obs.withsaveObsAsArray(true);
        String valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, JSON_ARRAY);
        assertNull(valStr);

        values.add("val1");
        values.add("val2");
        values.add("val3");
        valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, null);
        assertEquals("[\"val1\",\"val2\",\"val3\"]", valStr);

        obs.setSaveObsAsArray(false);
        valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, JSON_ARRAY);
        assertEquals("[\"val1\",\"val2\",\"val3\"]", valStr);

        valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, null);
        assertEquals("val1", valStr);
    }

    @Test
    public void testProcessEventUsingMiniProcessorShouldCallMiniProcessorProcessEventClient() throws Exception {
        MiniClientProcessorForJava miniClientProcessorForJava = Mockito.mock(MiniClientProcessorForJava.class);
        String eventType = "Custom Test Event";
        HashSet<String> supportedEventTypes = new HashSet<>();
        supportedEventTypes.add(eventType);
        Mockito.doReturn(supportedEventTypes).when(miniClientProcessorForJava).getEventTypes();

        String baseEntityId = "bei";
        EventClient eventClient = new EventClient(new Event(), new Client(baseEntityId));

        ClientProcessorForJava clientProcessorForJava = Mockito.spy(new ClientProcessorForJava(context));
        clientProcessorForJava.addMiniProcessors(miniClientProcessorForJava);

        ClientClassification clientClassification = new ClientClassification();
        clientProcessorForJava.processEventUsingMiniProcessor(clientClassification, eventClient, eventType);

        Mockito.verify(miniClientProcessorForJava, Mockito.times(1)).processEventClient(Mockito.eq(eventClient), ArgumentMatchers.<List<Event>>any(), Mockito.eq(clientClassification));
    }

    @Test
    public void testGetClientAttributesShouldReturnRequiredValues() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("national_id", "3434-34");
        attributes.put("drivers-license", "DL-324");
        Client client = new Client("123-23");
        client.setAttributes(attributes);
        Map<String, Object> result = Whitebox.invokeMethod(clientProcessor, "getClientAttributes", client);
        assertEquals(attributes, result);
    }

    @Test
    public void testGetGenderShouldReturnCorrectValue() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        Client client = new Client("123-23");
        client.setGender("Female");
        Map<String, String> resultMap = Whitebox.invokeMethod(clientProcessor, "getGender", client);
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("gender", "Female");
        assertEquals(expectedMap, resultMap);
    }

    @Test
    public void testUpdateIdentifierShouldRemoveHyphenFromOpenmrsId() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put("zeir_id", "23-23");
        Whitebox.setInternalState(clientProcessor, "openmrsGenIds", new String[]{"zeir_id"});
        Whitebox.invokeMethod(clientProcessor, "updateIdentifier", contentValues);
        assertEquals("2323", contentValues.get("zeir_id"));
    }

    @Test
    public void testCloseCaseShouldReturnFalseIfCloseCaseIsEmpty() {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        assertFalse(clientProcessor.closeCase(new Client("1233-2"), new ArrayList<>()));
    }

    @Test
    public void testCloseCaseShouldPassCorrectValuesToCloseCase() {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        PowerMockito.when(opensrpContext.commonrepository("child")).thenReturn(commonRepository);
        assertTrue(clientProcessor.closeCase(new Client("1233-2"), Arrays.asList("child")));
        Mockito.verify(commonRepository).closeCase(closeCaseArgumentCaptor.capture(), closeCaseArgumentCaptor.capture());
        assertEquals("1233-2", closeCaseArgumentCaptor.getAllValues().get(0));
        assertEquals("child", closeCaseArgumentCaptor.getAllValues().get(1));
    }

    @Test
    public void testAddContentValuesToDetailsTableShouldNotPopulateIfUpdateClientDetailsIsFalse() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(coreLibrary.getSyncConfiguration()).thenReturn(syncConfiguration);
        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);
        Mockito.when(opensrpContext.detailsRepository()).thenReturn(detailsRepository);
        Mockito.when(syncConfiguration.updateClientDetailsTable()).thenReturn(false);
        clientProcessor.addContentValuesToDetailsTable(new ContentValues(), new Date().getTime());
        Mockito.verify(detailsRepository, Mockito.never()).add(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testAddContentValuesToDetailsTableShouldPopulateIfUpdateClientDetailsIsTrue() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);
        Mockito.when(coreLibrary.getSyncConfiguration()).thenReturn(syncConfiguration);
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.detailsRepository()).thenReturn(detailsRepository);
        Mockito.when(syncConfiguration.updateClientDetailsTable()).thenReturn(true);
        ContentValues contentValues = new ContentValues();
        contentValues.put("base_entity_id", "2342-234");

        Long timestamp = new Date().getTime();
        clientProcessor.addContentValuesToDetailsTable(contentValues, timestamp);
        Mockito.verify(detailsRepository).add((String) detailsRepositoryAddArgumentCaptor.capture(), (String) detailsRepositoryAddArgumentCaptor.capture(),
                (String) detailsRepositoryAddArgumentCaptor.capture(), (Long) detailsRepositoryAddArgumentCaptor.capture());

        assertEquals("2342-234", detailsRepositoryAddArgumentCaptor.getAllValues().get(0));

        assertEquals("base_entity_id", detailsRepositoryAddArgumentCaptor.getAllValues().get(1));

        assertEquals(contentValues.getAsString("base_entity_id"), detailsRepositoryAddArgumentCaptor.getAllValues().get(2));

        assertEquals(timestamp, detailsRepositoryAddArgumentCaptor.getAllValues().get(3));

    }


    @Test
    public void testGetColumnMappingsShouldReturnTheCorrectTable() throws IOException {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.getEcClientFieldsFile()).thenReturn("ec_client_fields.json");
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open("ec_client_fields.json")).thenReturn(new ByteArrayInputStream(ClientData.ec_client_fields_json.getBytes()));
        Table ecHousehold = clientProcessor.getColumnMappings("ec_household");
        assertNotNull(ecHousehold);
        assertEquals(ecHousehold.name, "ec_household");
    }

    @Test
    public void testGetColumnMappingsShouldReturnNullIfFileIsNotRead() throws IOException {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.getEcClientFieldsFile()).thenReturn("ec_client_fields.json");
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open("ec_client_fields.json")).thenReturn(null);
        Table ecHousehold = clientProcessor.getColumnMappings("ec_household");
        assertNull(ecHousehold);
    }

    @Test
    public void testUpdateClientDetailsTableShouldNotSaveClientDetailsIdConfigIsFalse() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);
        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        Mockito.when(coreLibrary.getSyncConfiguration()).thenReturn(syncConfiguration);
        Mockito.when(syncConfiguration.updateClientDetailsTable()).thenReturn(false);
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.detailsRepository()).thenReturn(detailsRepository);
        Event event = new Event();
        clientProcessor.updateClientDetailsTable(event, new Client("2323-2"));
        assertTrue(Boolean.valueOf(event.getDetails().get(ClientProcessorForJava.detailsUpdated)));
        Mockito.verify(detailsRepository, Mockito.never()).add(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testUpdateClientDetailsTableShouldSaveClientDetailsIdConfigIsTrue() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);

        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        Mockito.when(coreLibrary.getSyncConfiguration()).thenReturn(syncConfiguration);
        Mockito.when(syncConfiguration.updateClientDetailsTable()).thenReturn(true);

        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.detailsRepository()).thenReturn(detailsRepository);

        DateTime dateTime = new DateTime();
        Event event = new Event();
        event.setEventDate(dateTime);
        Obs obs = new Obs();
        obs.setFieldCode("12212AAAAA");
        obs.setFormSubmissionField("reminders");
        Object value = (Object) "no";
        obs.setValues(Arrays.asList(value));
        obs.setHumanReadableValue(new ArrayList<>());
        obs.setFieldDataType("text");
        event.addObs(obs);
        Client client = new Client("234-13");
        client.setGender("Female");
        Map<String, Object> attribsMap = new HashMap<>();
        attribsMap.put("national_id", "423");
        attribsMap.put("phone_number", "0707070");
        client.setAttributes(attribsMap);
        clientProcessor.updateClientDetailsTable(event, client);
        assertTrue(Boolean.valueOf(event.getDetails().get(ClientProcessorForJava.detailsUpdated)));
        Mockito.verify(detailsRepository, Mockito.atLeast(4)).add(anyString(), anyString(), anyString(), anyLong());
    }

    @Config(shadows = {ShadowAssetHandler.class})
    @Test
    public void processClientShouldCallProcessEvent() throws Exception {
        String baseEntityId = "998098s0kldsckljsd";
        Client client = new Client(baseEntityId);
        Event event = new Event(baseEntityId, "eventId", "Birth Reg", new DateTime(), "client", "anm", "location-id", "form-submission-id");
        List<EventClient> eventClients = new ArrayList<>();
        eventClients.add(new EventClient(event, client));

        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);
        Mockito.doReturn(true).when(clientProcessorForJava).processEvent(Mockito.eq(event), Mockito.eq(client), Mockito.any(ClientClassification.class));

        clientProcessorForJava.processClient(eventClients);
        Mockito.verify(clientProcessorForJava).processEvent(Mockito.eq(event), Mockito.eq(client), Mockito.any(ClientClassification.class));
    }


    @Config(shadows = {ShadowAssetHandler.class})
    @Test
    public void processClientShouldCallProcessEventsUsingMiniProcessor() throws Exception {
        String baseEntityId = "998098s0kldsckljsd";
        Client client = new Client(baseEntityId);
        String birthRegEventType = "Birth Reg";
        Event event = new Event(baseEntityId, "eventId", birthRegEventType, new DateTime(), "client", "anm", "location-id", "form-submission-id");
        List<EventClient> eventClients = new ArrayList<>();
        EventClient eventClient = new EventClient(event, client);
        eventClients.add(eventClient);

        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);
        HashSet<String> eventTypes = new HashSet<String>();
        eventTypes.add(birthRegEventType);

        MiniClientProcessorForJava mockMiniProcessor = Mockito.mock(MiniClientProcessorForJava.class);
        Mockito.doReturn(eventTypes).when(mockMiniProcessor).getEventTypes();
        clientProcessorForJava.addMiniProcessors(mockMiniProcessor);
        clientProcessorForJava.processClient(eventClients);
        Mockito.verify(clientProcessorForJava).processEventUsingMiniProcessor(Mockito.any(ClientClassification.class), Mockito.eq(eventClient), Mockito.eq(birthRegEventType));
    }

    @Config(shadows = {ShadowAssetHandler.class})
    @Test
    public void processEventShouldCallCompleteProcessingEventAndReturnFalse() throws Exception {
        String baseEntityId = "998098s0kldsckljsd";
        String birthRegEventType = "Birth Reg";
        Event event = new Event(baseEntityId, "eventId", birthRegEventType, new DateTime(), "client", "anm", "location-id", "form-submission-id");

        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);

        assertFalse(clientProcessorForJava.processEvent(event, null, AssetHandler.assetJsonToJava(new HashMap<>(), RuntimeEnvironment.systemContext, "ec_client_classification.json", ClientClassification.class)));
        Mockito.verify(clientProcessorForJava).completeProcessing(Mockito.eq(event));
    }

    @Test
    public void processFieldShouldReturnFalse() {
        assertFalse(clientProcessor.processField(null, null, null));
    }

    @Test
    public void processFieldShouldReturnTrueAndCallProcessCaseModelAndCloseCase() {
        Field field = new Field();
        List<String> createsCase = new ArrayList<>();
        createsCase.add("ec_client");
        createsCase.add("ec_mother_details");

        field.creates_case = createsCase;
        field.field = "eventType";
        field.field_value = "New Woman Registration";


        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);
        Event newWomanRegistration = gson.fromJson("{\"identifiers\":{},\"baseEntityId\":\"aa4d1c8c-b27c-49e4-8c8e-c4201ec033b4\",\"locationId\":\"a0b023eb-fde6-4bc5-921f-463e2fe5e0a7\",\"eventDate\":\"2020-02-14T00:00:00.000Z\",\"eventType\":\"New Woman Registration\",\"formSubmissionId\":\"79bdbe60-4397-4f70-bb86-5f749bf70f3d\",\"providerId\":\"amani\",\"duration\":0,\"obs\":[{\"fieldType\":\"concept\",\"fieldDataType\":\"text\",\"fieldCode\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"0781980123\"],\"set\":[],\"formSubmissionField\":\"mother_guardian_number\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\",\"fieldCode\":\"164826AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"],\"set\":[],\"formSubmissionField\":\"protected_at_birth\",\"humanReadableValues\":[\"Yes\"]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\",\"fieldCode\":\"1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"],\"set\":[],\"formSubmissionField\":\"mother_hiv_status\",\"humanReadableValues\":[\"Negative\"]},{\"fieldType\":\"concept\",\"fieldDataType\":\"start\",\"fieldCode\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"2020-02-14 15:03:21\"],\"set\":[],\"formSubmissionField\":\"start\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"end\",\"fieldCode\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"2020-02-14 15:16:48\"],\"set\":[],\"formSubmissionField\":\"end\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"deviceid\",\"fieldCode\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"355215060433484\"],\"set\":[],\"formSubmissionField\":\"deviceid\",\"humanReadableValues\":[]}],\"entityType\":\"mother\",\"version\":1581682608930,\"teamId\":\"99eec47f-47e1-4c7c-96ca-39fda38ec5be\",\"team\":\"Kibera\",\"dateCreated\":\"2020-02-14T12:22:36.570Z\",\"serverVersion\":1581682956566,\"clientApplicationVersion\":12,\"clientDatabaseVersion\":9,\"type\":\"Event\",\"id\":\"54ba5fac-3a02-4511-8b61-ca7fd4739f7d\",\"revision\":\"v1\"}", Event.class);
        Client womanClient = gson.fromJson("{\"firstName\":\"Ninah\",\"lastName\":\"Mwaura\",\"birthdate\":\"1986-04-13T00:00:00.000Z\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"baseEntityId\":\"aa4d1c8c-b27c-49e4-8c8e-c4201ec033b4\",\"identifiers\":{\"M_ZEIR_ID\":\"102852\",\"OPENMRS_UUID\":\"5f471993-0271-4b0a-94da-f9857e049ae0\"},\"addresses\":[{\"addressType\":\"\",\"addressFields\":{\"address1\":\"Ayani\"}}],\"attributes\":{\"nrc_number\":\"7828282\",\"second_phone_number\":\"0758901234\"},\"dateCreated\":\"2020-02-14T12:22:36.561Z\",\"dateEdited\":\"2020-02-14T12:24:49.860Z\",\"serverVersion\":1581682956549,\"type\":\"Client\",\"id\":\"af47a972-7349-45b7-b8af-096104a18891\",\"revision\":\"v2\"}", Client.class);

        assertTrue(clientProcessorForJava.processField(field, newWomanRegistration, womanClient));
        Mockito.verify(clientProcessorForJava).processCaseModel(newWomanRegistration, womanClient, createsCase);
        Mockito.verify(clientProcessorForJava).closeCase(womanClient, field.closes_case);
    }

    @Test
    public void processCaseModelShouldReturnFalseWhenCreatesCaseParamIsNull() {
        assertFalse(clientProcessor.processCaseModel(null, null, null));
    }

    @Test
    public void processCaseModelShouldReturnTrueAndPerformDbCalls() {
        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);

        Event newWomanRegistrationEvent = gson.fromJson("{\"identifiers\":{},\"baseEntityId\":\"aa4d1c8c-b27c-49e4-8c8e-c4201ec033b4\",\"locationId\":\"a0b023eb-fde6-4bc5-921f-463e2fe5e0a7\",\"eventDate\":\"2020-02-14T00:00:00.000Z\",\"eventType\":\"New Woman Registration\",\"formSubmissionId\":\"79bdbe60-4397-4f70-bb86-5f749bf70f3d\",\"providerId\":\"amani\",\"duration\":0,\"obs\":[{\"fieldType\":\"concept\",\"fieldDataType\":\"text\",\"fieldCode\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"0781980123\"],\"set\":[],\"formSubmissionField\":\"mother_guardian_number\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\",\"fieldCode\":\"164826AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"],\"set\":[],\"formSubmissionField\":\"protected_at_birth\",\"humanReadableValues\":[\"Yes\"]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\",\"fieldCode\":\"1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"],\"set\":[],\"formSubmissionField\":\"mother_hiv_status\",\"humanReadableValues\":[\"Negative\"]},{\"fieldType\":\"concept\",\"fieldDataType\":\"start\",\"fieldCode\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"2020-02-14 15:03:21\"],\"set\":[],\"formSubmissionField\":\"start\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"end\",\"fieldCode\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"2020-02-14 15:16:48\"],\"set\":[],\"formSubmissionField\":\"end\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"deviceid\",\"fieldCode\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"355215060433484\"],\"set\":[],\"formSubmissionField\":\"deviceid\",\"humanReadableValues\":[]}],\"entityType\":\"mother\",\"version\":1581682608930,\"teamId\":\"99eec47f-47e1-4c7c-96ca-39fda38ec5be\",\"team\":\"Kibera\",\"dateCreated\":\"2020-02-14T12:22:36.570Z\",\"serverVersion\":1581682956566,\"clientApplicationVersion\":12,\"clientDatabaseVersion\":9,\"type\":\"Event\",\"id\":\"54ba5fac-3a02-4511-8b61-ca7fd4739f7d\",\"revision\":\"v1\"}", Event.class);
        Client womanClient = gson.fromJson("{\"firstName\":\"Ninah\",\"lastName\":\"Mwaura\",\"birthdate\":\"1986-04-13T00:00:00.000Z\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"baseEntityId\":\"aa4d1c8c-b27c-49e4-8c8e-c4201ec033b4\",\"identifiers\":{\"M_ZEIR_ID\":\"102852\",\"OPENMRS_UUID\":\"5f471993-0271-4b0a-94da-f9857e049ae0\"},\"addresses\":[{\"addressType\":\"\",\"addressFields\":{\"address1\":\"Ayani\"}}],\"attributes\":{\"nrc_number\":\"7828282\",\"second_phone_number\":\"0758901234\"},\"dateCreated\":\"2020-02-14T12:22:36.561Z\",\"dateEdited\":\"2020-02-14T12:24:49.860Z\",\"serverVersion\":1581682956549,\"type\":\"Client\",\"id\":\"af47a972-7349-45b7-b8af-096104a18891\",\"revision\":\"v2\"}", Client.class);

        List<String> createsCase = new ArrayList<>();
        createsCase.add("ec_client");
        createsCase.add("ec_mother_details");

        Mockito.doNothing().when(clientProcessorForJava).processCaseModel(Mockito.eq(newWomanRegistrationEvent), Mockito.eq(womanClient), Mockito.any(Column.class), Mockito.any(ContentValues.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(clientProcessorForJava).addContentValuesToDetailsTable(Mockito.any(ContentValues.class), Mockito.anyLong());
        Mockito.doReturn(1L).when(clientProcessorForJava).executeInsertStatement(Mockito.any(ContentValues.class), Mockito.anyString());
        Mockito.doNothing().when(clientProcessorForJava).updateClientDetailsTable(Mockito.eq(newWomanRegistrationEvent), Mockito.eq(womanClient));
        Mockito.doNothing().when(clientProcessorForJava).updateFTSsearch(Mockito.anyString(), Mockito.any(), Mockito.any(ContentValues.class));

        List<Column> columns = new ArrayList<>();
        Column column = new Column();
        column.column_name = "base_entity_id";
        column.dataType = "";

        columns.add(column);

        Table table = new Table();
        table.name = "ec_client";
        table.columns = columns;

        Table tableMother = new Table();
        tableMother.name = "ec_mother_details";
        tableMother.columns = columns;

        Mockito.doReturn(table).when(clientProcessorForJava).getColumnMappings("ec_client");
        Mockito.doReturn(tableMother).when(clientProcessorForJava).getColumnMappings("ec_mother_details");

        assertTrue(clientProcessorForJava.processCaseModel(newWomanRegistrationEvent, womanClient, createsCase));
        Mockito.verify(clientProcessorForJava).executeInsertStatement(Mockito.any(ContentValues.class), Mockito.eq("ec_client"));
        Mockito.verify(clientProcessorForJava).executeInsertStatement(Mockito.any(ContentValues.class), Mockito.eq("ec_mother_details"));
        Mockito.verify(clientProcessorForJava, Mockito.times(2)).updateClientDetailsTable(newWomanRegistrationEvent, womanClient);
    }

    @Test
    public void processEventShouldReturnFalseWhenCaseClassificationRulesAreNull() throws Exception {
        Event event = new Event();
        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);
        Mockito.doNothing().when(clientProcessorForJava).completeProcessing(Mockito.eq(event));

        Assert.assertFalse(clientProcessorForJava.processEvent(event, new Client("bei"), new ClientClassification()));
    }


    @Test
    public void processEventShouldReturnFalseWhenClientDeathDateIsNotNull() throws Exception {
        Event event = new Event();
        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);
        Mockito.doNothing().when(clientProcessorForJava).completeProcessing(Mockito.eq(event));

        Client client = new Client("bei");
        client.setDeathdate(new DateTime());
        ClientClassification clientClassification = new ClientClassification();
        List<ClassificationRule> classificationRules = new ArrayList<>();
        classificationRules.add(new ClassificationRule());
        clientClassification.case_classification_rules = classificationRules;

        Assert.assertFalse(clientProcessorForJava.processEvent(event, client, clientClassification));
    }


    @Test
    public void processEventShouldReturnTrueWhenParamsAreValidAndEventProcessed() throws Exception {
        Event event = new Event();
        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);
        Mockito.doNothing().when(clientProcessorForJava).completeProcessing(Mockito.eq(event));

        Client client = new Client("bei");
        ClientClassification clientClassification = new ClientClassification();
        List<ClassificationRule> classificationRules = new ArrayList<>();
        classificationRules.add(new ClassificationRule());
        clientClassification.case_classification_rules = classificationRules;


        Mockito.doReturn(false).when(clientProcessorForJava).processClientClass(Mockito.any(ClassificationRule.class), Mockito.eq(event), Mockito.eq(client));
        Mockito.doNothing().when(clientProcessorForJava).updateClientDetailsTable(Mockito.eq(event), Mockito.eq(client));

        Assert.assertTrue(clientProcessorForJava.processEvent(event, client, clientClassification));

        Mockito.verify(clientProcessorForJava).processClientClass(Mockito.any(ClassificationRule.class), Mockito.eq(event), Mockito.eq(client));
        Mockito.verify(clientProcessorForJava).updateClientDetailsTable(Mockito.eq(event), Mockito.eq(client));
    }

    @Test
    public void processClientClassShouldReturnTrueAndProcessEachFieldInTheMappingDefinition() {
        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);
        Event event = new Event();

        Client client = new Client("bei");
        ClassificationRule classificationRule = gson.fromJson("{\"comment\":\"Child: This rule checks whether a given case belongs to Child register\",\"rule\":{\"type\":\"event\",\"fields\":[{\"field\":\"eventType\",\"field_value\":\"New Woman Registration\",\"creates_case\":[\"ec_client\",\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Birth Registration\",\"creates_case\":[\"ec_client\",\"ec_child_details\"]},{\"field\":\"eventType\",\"field_value\":\"Update Birth Registration\",\"creates_case\":[\"ec_client\",\"ec_child_details\"]},{\"field\":\"eventType\",\"field_value\":\"ANC Close\",\"creates_case\":[\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"ANC Registration\",\"creates_case\":[\"ec_client\",\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Update ANC Registration\",\"creates_case\":[\"ec_client\",\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Visit\",\"creates_case\":[\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Opd Registration\",\"creates_case\":[\"ec_client\"]}]}}", ClassificationRule.class);

        Mockito.doReturn(true).when(clientProcessorForJava).processField(Mockito.any(Field.class), Mockito.eq(event), Mockito.eq(client));

        Assert.assertTrue(clientProcessorForJava.processClientClass(classificationRule, event, client));
        Mockito.verify(clientProcessorForJava, Mockito.times(8)).processField(Mockito.any(Field.class), Mockito.eq(event), Mockito.eq(client));
    }

    @Test
    public void processClientClassShouldFalseWhenClassificationRuleIsNull() {
        Event event = new Event();
        Client client = new Client("bei");
        Assert.assertFalse(clientProcessor.processClientClass(null, event, client));
    }

    @Test
    public void processClientClassShouldFalseWhenEventIsNull() {
        Client client = new Client("bei");

        ClassificationRule classificationRule = gson.fromJson("{\"comment\":\"Child: This rule checks whether a given case belongs to Child register\",\"rule\":{\"type\":\"event\",\"fields\":[{\"field\":\"eventType\",\"field_value\":\"New Woman Registration\",\"creates_case\":[\"ec_client\",\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Birth Registration\",\"creates_case\":[\"ec_client\",\"ec_child_details\"]},{\"field\":\"eventType\",\"field_value\":\"Update Birth Registration\",\"creates_case\":[\"ec_client\",\"ec_child_details\"]},{\"field\":\"eventType\",\"field_value\":\"ANC Close\",\"creates_case\":[\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"ANC Registration\",\"creates_case\":[\"ec_client\",\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Update ANC Registration\",\"creates_case\":[\"ec_client\",\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Visit\",\"creates_case\":[\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Opd Registration\",\"creates_case\":[\"ec_client\"]}]}}", ClassificationRule.class);
        Assert.assertFalse(clientProcessor.processClientClass(classificationRule, null, client));
    }

    @Test
    public void processClientClassShouldFalseWhenClientIsNull() {
        Event event = new Event();

        ClassificationRule classificationRule = gson.fromJson("{\"comment\":\"Child: This rule checks whether a given case belongs to Child register\",\"rule\":{\"type\":\"event\",\"fields\":[{\"field\":\"eventType\",\"field_value\":\"New Woman Registration\",\"creates_case\":[\"ec_client\",\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Birth Registration\",\"creates_case\":[\"ec_client\",\"ec_child_details\"]},{\"field\":\"eventType\",\"field_value\":\"Update Birth Registration\",\"creates_case\":[\"ec_client\",\"ec_child_details\"]},{\"field\":\"eventType\",\"field_value\":\"ANC Close\",\"creates_case\":[\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"ANC Registration\",\"creates_case\":[\"ec_client\",\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Update ANC Registration\",\"creates_case\":[\"ec_client\",\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Visit\",\"creates_case\":[\"ec_mother_details\"]},{\"field\":\"eventType\",\"field_value\":\"Opd Registration\",\"creates_case\":[\"ec_client\"]}]}}", ClassificationRule.class);
        Assert.assertFalse(clientProcessor.processClientClass(classificationRule, event, null));
    }

    @Test
    public void processCaseModelWhenGivenColumnConfigurationFromEventObsShouldPopulateContentValuesAsPerColumnConfiguration() {
        String eventJson = "{\"baseEntityId\":\"021a1da2-cebf-44fa-9ef3-3ffc18fa6356\",\"entityType\":\"vaccination\",\"eventDate\":\"2018-10-15T20:00:00.000-04:00\",\"eventType\":\"Vaccination\",\"formSubmissionId\":\"e9c0c4ec-63c3-4104-958e-21e133efd3b2\",\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"obs\":[{\"fieldCode\":\"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"date\",\"fieldType\":\"concept\",\"formSubmissionField\":\"rota_2\",\"humanReadableValues\":[],\"parentCode\":\"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"saveObsAsArray\":false,\"values\":[\"2018-10-16\"]},{\"fieldCode\":\"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"calculate\",\"fieldType\":\"concept\",\"formSubmissionField\":\"rota_2_dose\",\"humanReadableValues\":[],\"parentCode\":\"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"saveObsAsArray\":false,\"values\":[\"2\"]}],\"providerId\":\"chwone\",\"version\":1567465052005,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":11,\"dateCreated\":\"2019-10-07T05:49:52.992-04:00\",\"dateEdited\":\"2019-09-18T06:10:56.784-04:00\",\"serverVersion\":1568801628709}";
        String columnJson = "{\"column_name\":\"name\",\"json_mapping\":{\"field\":\"obs.fieldCode\",\"concept\":\"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value_field\":\"formSubmissionField\"}}";
        Event event = JsonFormUtils.gson.fromJson(eventJson, Event.class);

        Column column = JsonFormUtils.gson.fromJson(columnJson, Column.class);
        ContentValues contentValues = new ContentValues();
        contentValues.put("base_entity_id", "021a1da2-cebf-44fa-9ef3-3ffc18fa6356");

        clientProcessor.processCaseModel(event, null, column, contentValues);

        assertEquals("rota_2", contentValues.getAsString("name"));
    }


    @Test
    public void processCaseModelWhenGivenColumnConfigurationFromEventFieldShouldPopulateContentValues() {
        String eventJson = "{\"baseEntityId\":\"021a1da2-cebf-44fa-9ef3-3ffc18fa6356\",\"entityType\":\"vaccination\",\"eventDate\":\"2018-10-15T20:00:00.000-04:00\",\"eventType\":\"Vaccination\",\"formSubmissionId\":\"e9c0c4ec-63c3-4104-958e-21e133efd3b2\",\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"obs\":[{\"fieldCode\":\"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"date\",\"fieldType\":\"concept\",\"formSubmissionField\":\"rota_2\",\"humanReadableValues\":[],\"parentCode\":\"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"saveObsAsArray\":false,\"values\":[\"2018-10-16\"]},{\"fieldCode\":\"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"calculate\",\"fieldType\":\"concept\",\"formSubmissionField\":\"rota_2_dose\",\"humanReadableValues\":[],\"parentCode\":\"159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"saveObsAsArray\":false,\"values\":[\"2\"]}],\"providerId\":\"chwone\",\"version\":1567465052005,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":11,\"dateCreated\":\"2019-10-07T05:49:52.992-04:00\",\"dateEdited\":\"2019-09-18T06:10:56.784-04:00\",\"serverVersion\":1568801628709}";
        String columnJson = "{\"column_name\":\"anmid\",\"json_mapping\":{\"field\":\"providerId\"}}";
        Event event = JsonFormUtils.gson.fromJson(eventJson, Event.class);

        Column column = JsonFormUtils.gson.fromJson(columnJson, Column.class);
        ContentValues contentValues = new ContentValues();
        contentValues.put("base_entity_id", "021a1da2-cebf-44fa-9ef3-3ffc18fa6356");

        clientProcessor.processCaseModel(event, null, column, contentValues);

        assertEquals("chwone", contentValues.getAsString("anmid"));
    }


    @Test
    public void processCaseModelWhenGivenColumnConfigurationFromClientIdentifiersFieldShouldPopulateContentValues() {
        String eventJson = "{\"baseEntityId\":\"3a221190-c004-4297-88bd-4b925e19098f\",\"entityType\":\"ec_family_member\",\"eventDate\":\"2019-09-23T20:00:00.000-04:00\",\"eventType\":\"Family Member Registration\",\"formSubmissionId\":\"af7143d3-33d9-4bb2-9fe5-14937b4ae92f\",\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"obs\":[{\"fieldCode\":\"\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"surname\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"Kim\"]},{\"fieldCode\":\"\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"age_calculated\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"18.0\"]},{\"fieldCode\":\"\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"wra\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"0\"]},{\"fieldCode\":\"162558AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"disabilities\",\"humanReadableValues\":[\"No\"],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"]},{\"fieldCode\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"phone_number\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"0723433455\"]},{\"fieldCode\":\"1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"service_provider\",\"humanReadableValues\":[\"None\"],\"parentCode\":\"1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"saveObsAsArray\":false,\"values\":[\"164369AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"]},{\"fieldCode\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"start\",\"fieldType\":\"concept\",\"formSubmissionField\":\"start\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"2019-09-24 10:18:41\"]},{\"fieldCode\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"end\",\"fieldType\":\"concept\",\"formSubmissionField\":\"end\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"2019-09-24 10:21:34\"]},{\"fieldCode\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"phonenumber\",\"fieldType\":\"concept\",\"formSubmissionField\":\"phonenumber\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"+15555215554\"]}],\"providerId\":\"chwone\",\"version\":1569309694752,\"clientApplicationVersion\":2,\"clientDatabaseVersion\":11,\"dateCreated\":\"2019-09-24T03:44:30.108-04:00\",\"dateEdited\":\"2019-11-13T00:42:43.387-05:00\",\"serverVersion\":1573623789891}";
        String clientJson = "{\"birthdate\":\"2001-09-23T20:00:00.000-04:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"firstName\":\"Jonny\",\"gender\":\"Male\",\"middleName\":\"Kamau\",\"relationships\":{\"family\":[\"af3e29be-88ee-4063-bb02-963a64c664ab\"]},\"addresses\":[],\"attributes\":{\"id_avail\":\"[\\\"chk_none\\\"]\",\"Community_Leader\":\"[\\\"chk_none\\\"]\",\"Health_Insurance_Type\":\"iCHF\",\"Health_Insurance_Number\":\"123\"},\"baseEntityId\":\"3a221190-c004-4297-88bd-4b925e19098f\",\"identifiers\":{\"opensrp_id\":\"4602652\"},\"clientApplicationVersion\":2,\"clientDatabaseVersion\":11,\"dateCreated\":\"2019-09-24T03:44:30.015-04:00\",\"serverVersion\":1569311069836}";
        String columnJson = "{\"column_name\":\"unique_id\",\"json_mapping\":{\"field\":\"identifiers.opensrp_id\"},\"type\":\"Client\"}";
        Event event = JsonFormUtils.gson.fromJson(eventJson, Event.class);
        Client client = JsonFormUtils.gson.fromJson(clientJson, Client.class);

        Column column = JsonFormUtils.gson.fromJson(columnJson, Column.class);
        ContentValues contentValues = new ContentValues();
        contentValues.put("base_entity_id", "3a221190-c004-4297-88bd-4b925e19098f");

        clientProcessor.processCaseModel(event, client, column, contentValues);

        assertEquals("4602652", contentValues.getAsString("unique_id"));
    }


    @Test
    public void processCaseModelWhenGivenColumnConfigurationFromClientRelationshipsFieldShouldPopulateContentValues() {
        String eventJson = "{\"baseEntityId\":\"3a221190-c004-4297-88bd-4b925e19098f\",\"entityType\":\"ec_family_member\",\"eventDate\":\"2019-09-23T20:00:00.000-04:00\",\"eventType\":\"Family Member Registration\",\"formSubmissionId\":\"af7143d3-33d9-4bb2-9fe5-14937b4ae92f\",\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"obs\":[{\"fieldCode\":\"\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"surname\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"Kim\"]},{\"fieldCode\":\"\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"age_calculated\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"18.0\"]},{\"fieldCode\":\"\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"wra\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"0\"]},{\"fieldCode\":\"162558AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"disabilities\",\"humanReadableValues\":[\"No\"],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"]},{\"fieldCode\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"phone_number\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"0723433455\"]},{\"fieldCode\":\"1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"service_provider\",\"humanReadableValues\":[\"None\"],\"parentCode\":\"1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"saveObsAsArray\":false,\"values\":[\"164369AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"]},{\"fieldCode\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"start\",\"fieldType\":\"concept\",\"formSubmissionField\":\"start\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"2019-09-24 10:18:41\"]},{\"fieldCode\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"end\",\"fieldType\":\"concept\",\"formSubmissionField\":\"end\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"2019-09-24 10:21:34\"]},{\"fieldCode\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"phonenumber\",\"fieldType\":\"concept\",\"formSubmissionField\":\"phonenumber\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"+15555215554\"]}],\"providerId\":\"chwone\",\"version\":1569309694752,\"clientApplicationVersion\":2,\"clientDatabaseVersion\":11,\"dateCreated\":\"2019-09-24T03:44:30.108-04:00\",\"dateEdited\":\"2019-11-13T00:42:43.387-05:00\",\"serverVersion\":1573623789891}";
        String clientJson = "{\"birthdate\":\"2001-09-23T20:00:00.000-04:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"firstName\":\"Jonny\",\"gender\":\"Male\",\"middleName\":\"Kamau\",\"relationships\":{\"family\":[\"af3e29be-88ee-4063-bb02-963a64c664ab\"]},\"addresses\":[],\"attributes\":{\"id_avail\":\"[\\\"chk_none\\\"]\",\"Community_Leader\":\"[\\\"chk_none\\\"]\",\"Health_Insurance_Type\":\"iCHF\",\"Health_Insurance_Number\":\"123\"},\"baseEntityId\":\"3a221190-c004-4297-88bd-4b925e19098f\",\"identifiers\":{\"opensrp_id\":\"4602652\"},\"clientApplicationVersion\":2,\"clientDatabaseVersion\":11,\"dateCreated\":\"2019-09-24T03:44:30.015-04:00\",\"serverVersion\":1569311069836}";
        String columnJson = "{\"column_name\":\"relational_id\",\"json_mapping\":{\"field\":\"relationships.family\"},\"type\":\"Client\"}";
        Event event = JsonFormUtils.gson.fromJson(eventJson, Event.class);
        Client client = JsonFormUtils.gson.fromJson(clientJson, Client.class);

        Column column = JsonFormUtils.gson.fromJson(columnJson, Column.class);
        ContentValues contentValues = new ContentValues();
        contentValues.put("base_entity_id", "3a221190-c004-4297-88bd-4b925e19098f");

        clientProcessor.processCaseModel(event, client, column, contentValues);

        assertEquals("af3e29be-88ee-4063-bb02-963a64c664ab", contentValues.getAsString("relational_id"));
    }


    @Test
    public void processCaseModelWhenGivenColumnConfigurationFromClientAddressesFieldShouldPopulateContentValues() {
        String eventJson = "{\"baseEntityId\":\"5b85d576-51cd-4ccc-bfd7-2b67d4af89e3\",\"entityType\":\"ec_family\",\"eventDate\":\"2019-09-24T08:12:12.000-04:00\",\"eventType\":\"Update Family Relations\",\"formSubmissionId\":\"fc673dfe-a720-405e-9b2a-0b2e4067364b\",\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"obs\":[],\"providerId\":\"chwone\",\"version\":1569316332001,\"clientApplicationVersion\":2,\"clientDatabaseVersion\":11,\"dateCreated\":\"2019-09-24T05:13:17.700-04:00\",\"dateEdited\":\"2019-11-13T00:42:43.445-05:00\",\"serverVersion\":1573623789901}";
        String clientJson = "{\"birthdate\":\"1970-01-01T01:00:00.000-05:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"firstName\":\"Kitoto\",\"gender\":\"Male\",\"lastName\":\"Family\",\"relationships\":{\"family_head\":[\"9f424541-b5c6-46e5-af99-6c8d6d28559c\"],\"primary_caregiver\":[\"41b652a2-167a-4e32-9717-013b5031c972\"]},\"addresses\":[{\"addressFields\":{\"landmark\":\"Chandaria Factory\"},\"addressType\":\"\",\"cityVillage\":\"Kasabuni\"}],\"attributes\":{},\"baseEntityId\":\"5b85d576-51cd-4ccc-bfd7-2b67d4af89e3\",\"identifiers\":{\"opensrp_id\":\"4626057_family\"},\"clientApplicationVersion\":2,\"clientDatabaseVersion\":11,\"dateCreated\":\"2019-09-24T08:10:33.460-04:00\",\"dateEdited\":\"2019-09-24T05:13:17.679-04:00\",\"serverVersion\":1569316449432}";
        String columnJson = "{\"column_name\":\"village_town\",\"json_mapping\":{\"field\":\"addresses.cityVillage\"},\"type\":\"Client\"}";
        Event event = JsonFormUtils.gson.fromJson(eventJson, Event.class);
        Client client = JsonFormUtils.gson.fromJson(clientJson, Client.class);

        Column column = JsonFormUtils.gson.fromJson(columnJson, Column.class);
        ContentValues contentValues = new ContentValues();
        contentValues.put("base_entity_id", "5b85d576-51cd-4ccc-bfd7-2b67d4af89e3");

        ReflectionHelpers.setStaticField(ClientProcessorForJava.class, "instance", clientProcessor);
        clientProcessor.processCaseModel(event, client, column, contentValues);

        assertEquals("Kasabuni", contentValues.get("village_town"));
    }

    @Test
    public void processFieldShouldCreateCaseBasedOnObsValueFilled() {
        //Field field = JsonFormUtils.gson.fromJson("{\"creates_case\":[\"ec_family\"],\"field\":\"rType.sampleField\",\"field_value\":\"Family Registration\", \"values\": []}"
        Field field = JsonFormUtils.gson.fromJson("{\"creates_case\":[\"ec_family_disability\"],\"field\":\"obs.formSubmissionField\", \"values\": [\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"], \"concept\": \"disabilities\"}"
                , Field.class);
        Event event = JsonFormUtils.gson.fromJson("{\"baseEntityId\":\"3a221190-c004-4297-88bd-4b925e19098f\",\"entityType\":\"ec_family_member\",\"eventDate\":\"2019-09-23T20:00:00.000-04:00\",\"eventType\":\"Family Member Registration\",\"formSubmissionId\":\"af7143d3-33d9-4bb2-9fe5-14937b4ae92f\",\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"obs\":[{\"fieldCode\":\"\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"surname\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"Kim\"]},{\"fieldCode\":\"\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"age_calculated\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"18.0\"]},{\"fieldCode\":\"\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"wra\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"0\"]},{\"fieldCode\":\"162558AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"disabilities\",\"humanReadableValues\":[\"No\"],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"]},{\"fieldCode\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"phone_number\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"0723433455\"]},{\"fieldCode\":\"1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"text\",\"fieldType\":\"concept\",\"formSubmissionField\":\"service_provider\",\"humanReadableValues\":[\"None\"],\"parentCode\":\"1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"saveObsAsArray\":false,\"values\":[\"164369AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"]},{\"fieldCode\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"start\",\"fieldType\":\"concept\",\"formSubmissionField\":\"start\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"2019-09-24 10:18:41\"]},{\"fieldCode\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"end\",\"fieldType\":\"concept\",\"formSubmissionField\":\"end\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"2019-09-24 10:21:34\"]},{\"fieldCode\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"phonenumber\",\"fieldType\":\"concept\",\"formSubmissionField\":\"phonenumber\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"+15555215554\"]}],\"providerId\":\"chwone\",\"version\":1569309694752,\"clientApplicationVersion\":2,\"clientDatabaseVersion\":11,\"dateCreated\":\"2019-09-24T03:44:30.108-04:00\",\"dateEdited\":\"2019-11-13T00:42:43.387-05:00\",\"serverVersion\":1573623789891 }"
                , Event.class);
        Client client = JsonFormUtils.gson.fromJson("{\"birthdate\":\"2001-09-23T20:00:00.000-04:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"firstName\":\"Jonny\",\"gender\":\"Male\",\"middleName\":\"Kamau\",\"relationships\":{\"family\":[\"af3e29be-88ee-4063-bb02-963a64c664ab\"]},\"addresses\":[],\"attributes\":{\"id_avail\":\"[\\\"chk_none\\\"]\",\"Community_Leader\":\"[\\\"chk_none\\\"]\",\"Health_Insurance_Type\":\"iCHF\",\"Health_Insurance_Number\":\"123\"},\"baseEntityId\":\"3a221190-c004-4297-88bd-4b925e19098f\",\"identifiers\":{\"opensrp_id\":\"4602652\"},\"clientApplicationVersion\":2,\"clientDatabaseVersion\":11,\"dateCreated\":\"2019-09-24T03:44:30.015-04:00\",\"serverVersion\":1569311069836}"
                , Client.class);

        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);

        assertTrue(clientProcessorForJava.processField(field, event, client));
        ArgumentCaptor<ArrayList<String>> createsCaseCaptor = ArgumentCaptor.forClass(ArrayList.class);
        Mockito.verify(clientProcessorForJava).processCaseModel(Mockito.eq(event), Mockito.eq(client), createsCaseCaptor.capture());
        assertEquals("ec_family_disability", createsCaseCaptor.getValue().get(0));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        clientProcessor = null;
    }
}
