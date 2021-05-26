package org.smartregister.repository;

import android.content.ContentValues;
import android.util.Pair;

import net.sqlcipher.Cursor;
import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.domain.Event;
import org.smartregister.domain.SyncStatus;
import org.smartregister.domain.db.Column;
import org.smartregister.domain.db.ColumnAttribute;
import org.smartregister.domain.db.EventClient;
import org.smartregister.p2p.sync.data.JsonData;
import org.smartregister.sync.ClientData;
import org.smartregister.sync.intent.P2pProcessRecordsService;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by onaio on 29/08/2017.
 */
public class EventClientRepositoryTest extends BaseUnitTest {

    @InjectMocks
    private EventClientRepository eventClientRepository;
    private String baseEntityId = "baseEntityId";
    private String syncStatus = "syncStatus";
    private String eventType = "eventType";
    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqliteDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        eventClientRepository = new EventClientRepository();
    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCall() {
        Assert.assertNotNull(new EventClientRepository());
    }

    @Test
    public void batchInsertClientsReturnsNotNull() throws Exception {
        Assert.assertTrue(eventClientRepository.batchInsertClients(new JSONArray(ClientData.clientJsonArray)));
    }

    @Test
    public void batchInsertEventsReturnsNotNull() throws Exception {
        Assert.assertTrue(eventClientRepository.batchInsertEvents(new JSONArray(ClientData.eventJsonArray), 0l));
    }

    @Test
    public void getEventsByServerVersionsReturnsNotNull() throws Exception {
        String query = "SELECT json FROM event WHERE serverVersion > 0 AND serverVersion <= 0 ORDER BY serverVersion";
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEvents(0l, 0l));

    }

    @Test
    public void getEventsBySyncDateAndSyncStatusReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEvents(new Date(), "unsynced"));

    }

    @Test
    public void getEventsBySyncDateReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEvents(new Date()));

    }

    @Test
    public void fetchEventClientsByEventVersion() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.fetchEventClientsByEventTypes(Collections.singletonList("Registration")));
    }

    @Test
    public void testFetchEventClients() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.fetchEventClients(Collections.singletonList("123")));
    }

    @Test
    public void testFetchEventClientsByMinMaxTimestamp() throws Exception {

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.fetchEventClients(10000l, 20000l));
    }

    @Test
    public void getUnsyncedEventsReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getUnSyncedEvents(100));

    }

    @Test
    public void getEventsByBaseEntityIdReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        JSONObject events = eventClientRepository.getEventsByBaseEntityId(baseEntityId);
        Assert.assertNotNull(events);
        Assert.assertTrue(events.has("events"));

    }

    @Test
    public void getEventsByBaseEntityIdReturnsNotNullIfIdIsNull() {
        Assert.assertNotNull(eventClientRepository.getEventsByBaseEntityId(null));
        Mockito.verifyNoMoreInteractions(sqliteDatabase);

    }

    @Test
    public void getEventsByBaseEntityIdReturnsNotNullOnError() {
        Mockito.doThrow(new RuntimeException()).when(sqliteDatabase).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));
        Assert.assertNotNull(eventClientRepository.getEventsByBaseEntityId(null));
    }

    @Test
    public void testGetEventsByBaseEntityIdAndEventTypeReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEventsByBaseEntityIdAndEventType(baseEntityId, eventType));

    }

    @Test
    public void testGetEventsByBaseEntityIdsAndSyncStatusReturnsNotNull() throws Exception {

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEventsByBaseEntityIdsAndSyncStatus(syncStatus, Arrays.asList(baseEntityId)));

    }

    @Test
    public void testGetEventsReturnsNotNullOrEmpty() throws Exception {

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        List<EventClient> eventClients = eventClientRepository.getEvents(Arrays.asList(baseEntityId), Arrays.asList(syncStatus), Arrays.asList(eventType));
        Assert.assertNotNull(eventClients);
        Assert.assertTrue(eventClients.size() > 0);

    }

    @Test
    public void testGetEventsGeneratesCorrectQueryString() throws Exception {
        String baseEntityId2 = "baseEntityId2";
        String baseEntityId3 = "baseEntityId3";
        String eventType2 = "eventType2";

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());

        String expectedQueryString = "SELECT json FROM event WHERE baseEntityId IN (?,?,?)  AND syncStatus IN (?)  AND eventType IN (?,?)  ORDER BY serverVersion";

        Assert.assertNotNull(eventClientRepository.getEvents(Arrays.asList(baseEntityId, baseEntityId2, baseEntityId3), Arrays.asList(syncStatus), Arrays.asList(eventType, eventType2)));

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String[]> queryParamsCaptor = ArgumentCaptor.forClass(String[].class);
        Mockito.verify(sqliteDatabase).rawQuery(queryCaptor.capture(), queryParamsCaptor.capture());

        Assert.assertEquals(expectedQueryString, queryCaptor.getValue());
        Assert.assertArrayEquals(new String[]{baseEntityId, baseEntityId2, baseEntityId3, syncStatus, eventType, eventType2}, queryParamsCaptor.getValue());

    }

    public static MatrixCursor getEventCursor() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"json", "timestamp"});
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        for (int i = 0; i < eventArray.length(); i++) {
            matrixCursor.addRow(new String[]{eventArray.getJSONObject(i).toString(), "1985-07-24T00:00:00.000Z"});
        }
        return matrixCursor;
    }

    public static MatrixCursor getClientCursor() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"json", "timestamp"});
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        for (int i = 0; i < clientArray.length(); i++) {
            matrixCursor.addRow(new String[]{clientArray.getJSONObject(i).toString(), "1985-07-24T00:00:00.000Z"});
        }
        return matrixCursor;
    }

    @Test
    public void getEventsByFormSubmissionIdReturnsNotNull() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"json"});
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        for (int i = 0; i < eventArray.length(); i++) {
            matrixCursor.addRow(new String[]{eventArray.getJSONObject(i).toString()});
        }
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(eventClientRepository.getEventsByFormSubmissionId("FormSubmissionID"));

    }

    @Test
    public void markEventsAsSyncedCallsUpdateAsMAnyTimesAsEventsAndClientsPassedIntoMethod() throws Exception {

        HashMap<String, Object> syncedObjects = new HashMap<String, Object>();
        syncedObjects.put("clients", getClientList());
        syncedObjects.put("events", getEventList());
        eventClientRepository.markEventsAsSynced(syncedObjects);
        Mockito.verify(sqliteDatabase, Mockito.times(getEventList().size() + getClientList().size())).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void markAllAsUnSyncedCallsUpdate2timesFor2Objects() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{baseEntityId, syncStatus});
        Mockito.when(sqliteDatabase.rawQuery("select baseEntityId,syncStatus from client", null)).thenReturn(matrixCursor);
        Mockito.when(sqliteDatabase.rawQuery("select baseEntityId,syncStatus from event", null)).thenReturn(matrixCursor);
        eventClientRepository.markAllAsUnSynced();
        Mockito.verify(sqliteDatabase, Mockito.times(2)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void addorUpdateClientCallsInsert1timeForNewClients() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
//        matrixCursor.addRow(new String []{baseEntityId, syncStatus});

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        eventClientRepository.addorUpdateClient(baseEntityId, getClientList().get(0));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.any(ContentValues.class));

    }

    @Test
    public void addorUpdateClientCallsUpdate1timeForOldClients() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{baseEntityId, syncStatus});

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        eventClientRepository.addorUpdateClient(baseEntityId, getClientList().get(0));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    public ArrayList<JSONObject> getClientList() throws Exception {
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }
        return clientList;
    }

    @Test
    public void addorUpdateEventCallsInsert1timeForNewEvents() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String[].class))).thenReturn(matrixCursor);
        eventClientRepository.addEvent(baseEntityId, getEventList().get(0));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.any(ContentValues.class));

    }

    @Test
    public void addorUpdateEventCallsUpdate1timeForOldEvents() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{baseEntityId, syncStatus});

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        eventClientRepository.addEvent(baseEntityId, getEventList().get(0));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    public ArrayList<JSONObject> getEventList() throws Exception {
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        return eventList;
    }

    @Test
    public void deleteClientCallsDelete1time() {
        eventClientRepository.deleteClient(baseEntityId);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void deleteEventCallsDelete1time() {
        eventClientRepository.deleteEventsByBaseEntityId(baseEntityId, "eventType");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void fetchEventClientsByRowId() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"rowid", "json"});
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        for (int i = 0; i < eventArray.length(); i++) {
            matrixCursor.addRow(new String[]{(i + 1) + "", eventArray.getJSONObject(i).toString()});
        }

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(Object[].class))).thenReturn(matrixCursor);
        P2pProcessRecordsService.EventClientQueryResult eventClientQueryResult = eventClientRepository.fetchEventClientsByRowId(0);

        Assert.assertEquals(eventArray.length(), eventClientQueryResult.getMaxRowId());
    }

    @Test
    public void assertConvertToJson() {
        Assert.assertNull(eventClientRepository.convertToJson(null));
    }

    public MatrixCursor getCursorSyncStatus() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{"{\"json\":\"data\"}", syncStatus});
        return matrixCursor;
    }

    @Test
    public void assertGetUnValidatedEventFormSubmissionIdsReturnsList() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        org.junit.Assert.assertNotNull(eventClientRepository.getUnValidatedEventFormSubmissionIds(1));
    }


    @Test
    public void assertGetUnValidatedClientBaseEntityIdsReturnsList() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        org.junit.Assert.assertNotNull(eventClientRepository.getUnValidatedClientBaseEntityIds(1));
    }


    @Test
    public void assertcreateTableCallsExecSql() {
        int count = 1; //first one to create table, rest for create index
        for (Column cc : EventClientRepository.client_column.values()) {
            if (cc.column().index()) {
                count++;
            }
        }
        EventClientRepository.createTable(sqliteDatabase, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        Mockito.verify(sqliteDatabase, Mockito.times(count)).execSQL(Mockito.anyString());
    }

    /**
     * Events manually saved should always have an unprocessed status
     */
    @Test
    public void testAddEventDefaultStatus() {
        EventClientRepository eventClientRepository = Mockito.spy(new EventClientRepository());
        String baseEntityId = "12345";
        JSONObject jsonObject = Mockito.mock(JSONObject.class);

        eventClientRepository.addEvent(baseEntityId, jsonObject);

        Mockito.verify(eventClientRepository).addEvent(baseEntityId, jsonObject, BaseRepository.TYPE_Unprocessed);
    }

    @Test
    public void getMinMaxServerVersionsShouldReturnMaxAndMinServerVersionFromEvents() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.put("events", jsonArray);

        for (int i = 0; i < 5; i++) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("serverVersion", (i + 1) * 1000L);

            jsonArray.put(jsonObject1);
        }

        Pair<Long, Long> minMaxServerVersions = eventClientRepository.getMinMaxServerVersions(jsonObject);
        Assert.assertEquals(5000L, minMaxServerVersions.second, 0L);
        Assert.assertEquals(1000L, minMaxServerVersions.first, 0L);
    }

    @Test
    public void getMinMaxServerVersionsShouldReturnDefaultMinMaxWhenEventsArrayIsEmpty() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.put("events", jsonArray);

        Pair<Long, Long> minMaxServerVersions = eventClientRepository.getMinMaxServerVersions(jsonObject);
        Assert.assertEquals(Long.MIN_VALUE, minMaxServerVersions.second, 0L);
        Assert.assertEquals(Long.MAX_VALUE, minMaxServerVersions.first, 0L);
    }

    @Test
    public void getEventsShouldReturnGenerateMaxRowIdAndIncludeRowIdAndSyncStatusInJson() throws JSONException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"json", EventClientRepository.event_column.syncStatus.name(), AllConstants.ROWID}, 0);

        for (int i = 0; i < 30; i++) {
            matrixCursor.addRow(new Object[]{"{\"eventId\": \"d89sd\"}", BaseRepository.TYPE_Synced, (i + 1L)});
        }
        Mockito.doReturn(matrixCursor).when(sqliteDatabase).rawQuery(Mockito.eq("SELECT json,syncStatus,rowid FROM event WHERE rowid > ?  ORDER BY rowid ASC LIMIT ?"), Mockito.any(Object[].class));

        JsonData jsonData = eventClientRepository.getEvents(0, 20, null);

        Assert.assertEquals(30L, jsonData.getHighestRecordId());
        JSONObject jsonObject = jsonData.getJsonArray().getJSONObject(0);
        Assert.assertTrue(jsonObject.has(EventClientRepository.event_column.syncStatus.name()));
        Assert.assertTrue(jsonObject.has(AllConstants.ROWID));
    }

    @Test
    public void getClientShouldReturnGenerateMaxRowIdAndIncludeRowIdAndSyncStatusInJson() throws JSONException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"json", EventClientRepository.event_column.syncStatus.name(), AllConstants.ROWID}, 0);

        for (int i = 0; i < 30; i++) {
            matrixCursor.addRow(new Object[]{"{\"gender\": \"male\"}", BaseRepository.TYPE_Synced, (i + 1L)});
        }

        Mockito.doReturn(matrixCursor).when(sqliteDatabase).rawQuery(Mockito.eq("SELECT json,syncStatus,rowid FROM client WHERE rowid > ?  ORDER BY rowid ASC LIMIT ?"), Mockito.any(Object[].class));

        JsonData jsonData = eventClientRepository.getClients(0, 20, null);

        Assert.assertEquals(30L, jsonData.getHighestRecordId());
        JSONObject jsonObject = jsonData.getJsonArray().getJSONObject(0);
        Assert.assertTrue(jsonObject.has(EventClientRepository.event_column.syncStatus.name()));
        Assert.assertTrue(jsonObject.has(AllConstants.ROWID));
    }

    @Test
    public void getClientsWithLastLocationIDShouldReturnGenerateMaxRowIdAndIncludeRowIdAndSyncStatusInJson() throws JSONException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"json", EventClientRepository.event_column.syncStatus.name(), AllConstants.ROWID, "eventJson"}, 0);

        for (int i = 0; i < 30; i++) {
            matrixCursor.addRow(new Object[]{"{\"gender\": \"male\"}", BaseRepository.TYPE_Synced, (i + 1L), "{\"baseEntityId\":\"a423f801-8f6e-421d-ac9b-a3e4a24a0d61\",\"locationId\":\"d5ff0ea1-bbc5-424d-84c2-5b084e10ef90\"}"});
        }

        Mockito.doReturn(matrixCursor).when(sqliteDatabase).rawQuery(Mockito.eq("SELECT json,syncStatus,rowid,(select event.json from event where event.baseEntityId = client.baseEntityId \n" +
                " ORDER by event.eventDate desc , event.updatedAt desc , event.dateEdited desc , event.serverVersion desc limit 1) eventJson FROM client WHERE rowid > ?  ORDER BY rowid ASC LIMIT ?"), Mockito.any(Object[].class));

        JsonData jsonData = eventClientRepository.getClientsWithLastLocationID(0, 20);

        Assert.assertEquals(30L, jsonData.getHighestRecordId());
        JSONObject jsonObject = jsonData.getJsonArray().getJSONObject(0);
        Assert.assertTrue(jsonObject.has(EventClientRepository.event_column.syncStatus.name()));
        Assert.assertTrue(jsonObject.has(AllConstants.ROWID));
    }


    @Test
    public void testPopulateFormSubmissionIdsShouldPartitionListWithPageSize() {
        EventClientRepository spyEventClientRepository = Mockito.spy(eventClientRepository);
        spyEventClientRepository.FORM_SUBMISSION_IDS_PAGE_SIZE = 2;
        Set<String> formSubmissionIds = new HashSet<>();
        List<String> formSubmissionIdsList = new ArrayList<>();
        formSubmissionIdsList.add("erwr");
        formSubmissionIdsList.add("trytry");
        formSubmissionIdsList.add("poll;");
        formSubmissionIdsList.add("wer");
        formSubmissionIdsList.add("qasdcwe");

        Cursor mockCursor = Mockito.mock(Cursor.class);

        Mockito.doReturn(sqliteDatabase).when(spyEventClientRepository).getReadableDatabase();

        Mockito.doReturn(mockCursor).when(sqliteDatabase).rawQuery(Mockito.anyString(), Mockito.any());

        Mockito.doAnswer(new Answer() {
            int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) {
                ++count;
                if (count <= 1)
                    return true;
                else {
                    count = 0;
                    return false;
                }
            }
        }).when(mockCursor).moveToNext();

        Mockito.doAnswer(invocation -> UUID.randomUUID().toString()).when(mockCursor).getString(Mockito.eq(0));

        spyEventClientRepository
                .populateFormSubmissionIds(formSubmissionIdsList, formSubmissionIds);

        Mockito.verify(spyEventClientRepository, Mockito.times(3))
                .populateFormSubmissionIds(Mockito.anyList(), Mockito.anySet());

        Assert.assertEquals(3, formSubmissionIds.size());
    }

    @Test
    public void testGetEventsByTaskIds() throws Exception {
        String query = "SELECT json FROM event WHERE taskId IN (?)";
        String[] params = new String[]{"taskId-1"};
        when(sqliteDatabase.rawQuery(query, params)).thenReturn(getEventCursor());

        Set<String> taskIds = new HashSet<>();
        taskIds.add("taskId-1");

        List<Event> events = eventClientRepository.getEventsByTaskIds(taskIds);
        Mockito.verify(sqliteDatabase).rawQuery(query, params);
        Assert.assertNotNull(events.size());

    }

    @Test
    public void testGetEventsByEventId() throws Exception {
        String eventId = "event-id";
        String query = "SELECT json FROM event WHERE eventId= ? ";
        String[] params = new String[]{eventId};
        when(sqliteDatabase.rawQuery(query, params)).thenReturn(getEventCursor());

        JSONObject actualJsonObject = eventClientRepository.getEventsByEventId(eventId);
        Mockito.verify(sqliteDatabase).rawQuery(query, params);
        Assert.assertNotNull(actualJsonObject);
        Assert.assertEquals("03b1321a-d1fb-4fd0-b1cd-a3f3509fc6a6", actualJsonObject.get("baseEntityId"));
        Assert.assertEquals("2184aaaa-d1cf-4099-945a-c66bd8a93e1e", actualJsonObject.get("formSubmissionId"));
    }

    @Test
    public void testGetEventsByEventIdWithNullParam() throws Exception {

        JSONObject actualJsonObject = eventClientRepository.getEventsByEventId(null);
        Mockito.verifyNoInteractions(sqliteDatabase);
        Assert.assertNull(actualJsonObject);
    }

    @Test
    public void testGetEventsByEventIds() throws Exception {
        String query = "SELECT json FROM event WHERE eventId IN (?)";
        String eventId = "eventId-1";
        String[] params = new String[]{eventId};
        when(sqliteDatabase.rawQuery(query, params)).thenReturn(getEventCursor());

        Set<String> eventIds = new HashSet<>();
        eventIds.add("eventId-1");

        List<Event> events = eventClientRepository.getEventsByEventIds(eventIds);
        Mockito.verify(sqliteDatabase).rawQuery(query, params);
        Assert.assertNotNull(events);
    }

    @Test
    public void testGetSqliteType() {
        Assert.assertEquals("varchar", eventClientRepository.getSqliteType(ColumnAttribute.Type.text));
        Assert.assertEquals("boolean", eventClientRepository.getSqliteType(ColumnAttribute.Type.bool));
        Assert.assertEquals("datetime", eventClientRepository.getSqliteType(ColumnAttribute.Type.date));

        Assert.assertEquals("varchar", eventClientRepository.getSqliteType(ColumnAttribute.Type.list));
        Assert.assertEquals("varchar", eventClientRepository.getSqliteType(ColumnAttribute.Type.map));
        Assert.assertEquals("integer", eventClientRepository.getSqliteType(ColumnAttribute.Type.longnum));
    }

    @Test
    public void testFetchEventClientsByLastSyncDateAndSyncStatus() {
        eventClientRepository = spy(eventClientRepository);
        String syncStatus = SyncStatus.SYNCED.name();
        Date lastSyncDate = new Date();
        String lastSyncString = DateUtil.yyyyMMddHHmmss.format(lastSyncDate);
        String query = "select json,updatedAt from event where syncStatus = ? and updatedAt > ? ORDER BY serverVersion";

        eventClientRepository.fetchEventClients(lastSyncDate, syncStatus);

        Mockito.verify(eventClientRepository).fetchEventClientsCore(query, new String[]{syncStatus, lastSyncString});
    }

    @Test
    public void testDropIndexesRemovesTheIndexesForAGivenTable() throws Exception {
        String query = "SELECT name FROM sqlite_master WHERE type = ? AND sql is not null AND tbl_name = ?";
        String[] params = new String[]{"index", EventClientRepository.Table.event.name()};
        when(sqliteDatabase.rawQuery(query, params)).thenReturn(getIndexCursor());

        eventClientRepository.dropIndexes(sqliteDatabase, EventClientRepository.Table.event);

        Mockito.verify(sqliteDatabase).execSQL("DROP INDEX event_index");

    }

    @Test
    public void testFetchClientByBaseEntityIdsReturnsClientWithGivenBaseEntityId() {
        Set<String> baseEntityIds = Collections.singleton("base_entity_id_1");
        String query = "SELECT json FROM client WHERE baseEntityId in  (?)";
        eventClientRepository = spy(eventClientRepository);

        eventClientRepository.fetchClientByBaseEntityIds(baseEntityIds);

        Mockito.verify(eventClientRepository).fetchClients(query, baseEntityIds.toArray(new String[0]));
    }

    @Test
    public  void testGetUnSyncedClientsReturnsListOfUnsyncedClients() throws Exception {
        String query = "SELECT json FROM client WHERE syncStatus = ? limit 16";
        String[] params = new String[]{BaseRepository.TYPE_Unsynced};
        when(sqliteDatabase.rawQuery(query, params)).thenReturn(getClientCursor());

        List<JSONObject> actualClientJSONObjects = eventClientRepository.getUnSyncedClients(16);
        Assert.assertNotNull(actualClientJSONObjects);
        Assert.assertEquals(16, actualClientJSONObjects.size());
        Mockito.verify(sqliteDatabase).rawQuery(query, params);

    }


    public static MatrixCursor getIndexCursor() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"index"});
        matrixCursor.addRow(new String[]{"event_index"});
        return matrixCursor;
    }

}