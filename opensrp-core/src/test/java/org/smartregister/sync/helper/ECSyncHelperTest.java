package org.smartregister.sync.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ndegwamartin on 07/09/2018.
 */
public class ECSyncHelperTest extends BaseUnitTest {

    private ECSyncHelper syncHelper;
    private static final String EVENT_CLIENT_REPOSITORY = "eventClientRepository";

    private static final long DUMMY_LONG = 1000l;

    private JSONObject clientJson;
    private String clientBaseEntityId;
    private JSONObject eventJson;
    private String eventBaseEntityId;

    @Mock
    private EventClientRepository eventClientRepository;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    private String clientString = "{\n" +
            "  \"type\": \"Client\",\n" +
            "  \"dateCreated\": \"2019-11-21T15:29:36.799+07:00\",\n" +
            "  \"baseEntityId\": \"8b503cdc-5187-47c9-afc0-3e01b924dbe2\",\n" +
            "  \"identifiers\": {\n" +
            "    \"opensrp_id\": \"11138021_family\"\n" +
            "  },\n" +
            "  \"firstName\": \"Test 2\",\n" +
            "  \"lastName\": \"Family\",\n" +
            "  \"birthdate\": \"1970-01-01T14:00:00.000+07:00\",\n" +
            "  \"birthdateApprox\": true,\n" +
            "  \"deathdateApprox\": false,\n" +
            "  \"gender\": \"Male\",\n" +
            "  \"_id\": \"f187d396-c25e-4dd7-adc8-dc921c1f8ae4\",\n" +
            "  \"_rev\": \"v1\"\n" +
            "}";

    private String eventString = "{\n" +
            "  \"type\": \"Event\",\n" +
            "  \"dateCreated\": \"2019-11-20T15:38:53.593+07:00\",\n" +
            "  \"serverVersion\": 1574239133593,\n" +
            "  \"identifiers\": {},\n" +
            "  \"baseEntityId\": \"a7baf57d-ad31-46d2-8d28-0c81dd306b09\",\n" +
            "  \"locationId\": \"a7baf57d-ad31-46d2-8d28-0c81dd306b09\",\n" +
            "  \"eventDate\": \"2019-10-02T07:00:00.000+07:00\",\n" +
            "  \"eventType\": \"Case Details\",\n" +
            "  \"formSubmissionId\": \"ed521540-5b80-411e-921b-4a1bb9864c66\",\n" +
            "  \"providerId\": \"nifi-user\",\n" +
            "  \"duration\": 0,\n" +
            "  \"obs\": [],\n" +
            "  \"entityType\": \"Case Details\",\n" +
            "  \"version\": 1557860282617,\n" +
            "  \"teamId\": \" \",\n" +
            "  \"_id\": \"010a1a0c-1337-4307-97c5-2ddc8d7bb760\",\n" +
            "  \"_rev\": \"v1\"\n" +
            "}";

    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);
        syncHelper = new ECSyncHelper(RuntimeEnvironment.application, eventClientRepository);
        Whitebox.setInternalState(syncHelper, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        Whitebox.setInternalState(syncHelper, allSharedPreferences, allSharedPreferences);
        clientJson = new JSONObject(clientString);
        clientBaseEntityId = "client-base-entity-id1";
        eventJson = new JSONObject(eventString);
        eventBaseEntityId = "event-base-entity-id1";
    }

    @Test
    public void testSaveAllClientsAndEventsInvokesBatchSaveWithCorrectParams() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();

        JSONArray clientsArray = new JSONArray();
        clientsArray.put("Some Client");

        JSONArray eventsArray = new JSONArray();
        eventsArray.put("Some Event");

        object.put("clients", clientsArray);
        object.put("events", eventsArray);

        boolean result = syncHelperSpy.saveAllClientsAndEvents(object);

        verify(syncHelperSpy).batchSave(eventsArray, clientsArray);
        assertTrue(result);

        result = syncHelperSpy.saveAllClientsAndEvents(object);
        assertTrue(result);
    }

    @Test
    public void testSaveAllClientsAndEventsShouldReturnFalseForNullParam() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        boolean result = syncHelperSpy.saveAllClientsAndEvents(null);
        assertFalse(result);
    }

    @Test
    public void testAllEventClientsInvokesRepositoryFetchEventClientsWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        Mockito.doReturn(null).when(eventClientRepository).fetchEventClients(DUMMY_LONG, DUMMY_LONG);

        List<EventClient> result = syncHelperSpy.allEventClients(DUMMY_LONG, DUMMY_LONG);
        assertNull(result);

        verify(eventClientRepository).fetchEventClients(DUMMY_LONG, DUMMY_LONG);

        //On Exception
        EventClientRepository eventClientRepository = null;
        Whitebox.setInternalState(syncHelperSpy, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        result = syncHelperSpy.allEventClients(DUMMY_LONG, DUMMY_LONG);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    public void testBatchSaveInvokesBatchInsertClientsWithCorrectParams() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();

        JSONArray clientsArray = new JSONArray();
        clientsArray.put("Some Client");

        JSONArray eventsArray = new JSONArray();
        eventsArray.put("Some Event");

        object.put("clients", clientsArray);
        object.put("events", eventsArray);

        syncHelperSpy.batchSave(eventsArray, clientsArray);

        verify(eventClientRepository).batchInsertClients(clientsArray);
    }

    @Test
    public void testUpdateLastSyncTimeStamp() {
        long expectedTimeStamp = 1604387699l;
        syncHelper.updateLastSyncTimeStamp(expectedTimeStamp);
        verify(allSharedPreferences).saveLastSyncDate(expectedTimeStamp);
    }

    @Test
    public void testGetLastCheckTimeStamp() {
        syncHelper.getLastCheckTimeStamp();
        verify(allSharedPreferences).fetchLastCheckTimeStamp();
    }

    @Test
    public void testGetEventsByLastSyncDateAndSyncStatus() {
        Date lastSyncDate = new Date();
        String syncStatus = BaseRepository.TYPE_Synced;
        Event event = new Event();
        event.setBaseEntityId(eventBaseEntityId);
        EventClient eventClient = new EventClient(event);
        when(eventClientRepository.fetchEventClients(lastSyncDate, syncStatus)).thenReturn(Collections.singletonList(eventClient));
        List<EventClient> actualevents = syncHelper.getEvents(lastSyncDate, syncStatus);
        assertEquals(1, actualevents.size());
        assertEquals(eventBaseEntityId, actualevents.get(0).getEvent().getBaseEntityId());
    }

    @Test
    public void testGetEventsByLastSyncDateAndSyncStatusReturnsEmptyArrayWhenExceptionIsThrown() {
        Date lastSyncDate = new Date();
        String syncStatus = BaseRepository.TYPE_Synced;

        doThrow(new NullPointerException()).when(eventClientRepository).fetchEventClients(lastSyncDate, syncStatus);
        List<EventClient> actualevents = syncHelper.getEvents(lastSyncDate, syncStatus);
        assertNotNull(actualevents);
        assertEquals(0, actualevents.size());
    }


    @Test
    public void testGetEventsByFormSubmissionIds() {
        String formsubmissionid = "formsubmissionid1";
        List<String> formSubmissionIds = Collections.singletonList(formsubmissionid);
        Event event = new Event();
        event.setBaseEntityId(eventBaseEntityId);
        EventClient eventClient = new EventClient(event);
        when(eventClientRepository.fetchEventClients(formSubmissionIds)).thenReturn(Collections.singletonList(eventClient));
        List<EventClient> actualevents = syncHelper.getEvents(formSubmissionIds);
        assertEquals(1, actualevents.size());
        assertEquals(eventBaseEntityId, actualevents.get(0).getEvent().getBaseEntityId());
    }

    @Test
    public void testGetEventsByFormSubmissionIdsReturnsEmptyArrayWhenExceptionIsThrown() {
        String formsubmissionid = "formsubmissionid1";
        List<String> formSubmissionIds = Collections.singletonList(formsubmissionid);
        doThrow(new NullPointerException()).when(eventClientRepository).fetchEventClients(formSubmissionIds);
        List<EventClient> actualevents = syncHelper.getEvents(formSubmissionIds);
        assertNotNull(actualevents);
        assertEquals(0, actualevents.size());
    }

    @Test
    public void testGetClient() {
        when(eventClientRepository.getClientByBaseEntityId(clientBaseEntityId)).thenReturn(clientJson);
        JSONObject actualClient = syncHelper.getClient(clientBaseEntityId);
        verify(eventClientRepository).getClientByBaseEntityId(clientBaseEntityId);
        assertEquals(clientJson,actualClient);
    }

    @Test
    public void testAddClient() {
        syncHelper.addClient(clientBaseEntityId, clientJson);
        verify(eventClientRepository).addorUpdateClient(clientBaseEntityId, clientJson);

    }

    @Test
    public void testAddEvent() {
        syncHelper.addEvent(eventBaseEntityId, eventJson);
        verify(eventClientRepository).addEvent(eventBaseEntityId, eventJson);

    }

    @Test
    public void testAddEventWithSyncStatus() {
        syncHelper.addEvent(eventBaseEntityId, eventJson, BaseRepository.TYPE_Synced);
        verify(eventClientRepository).addEvent(eventBaseEntityId, eventJson, BaseRepository.TYPE_Synced);
    }

    @Test
    public void testAllEvents() {
        long startSyncTimeStamp = 1604995368l;
        long lastSyncTimeStamp = 2604995368l;
        syncHelper.allEventClients(startSyncTimeStamp, lastSyncTimeStamp);
        verify(eventClientRepository).fetchEventClients(startSyncTimeStamp, lastSyncTimeStamp);
    }

    @Test
    public void testBatchInsertClients() {
        JSONArray clientsArray = new JSONArray();
        clientsArray.put(clientJson);
        syncHelper.batchInsertClients(clientsArray);
        verify(eventClientRepository).batchInsertClients(clientsArray);
    }

    @Test
    public void testBatchInsertEvents() {
        JSONArray eventsArray = new JSONArray();
        eventsArray.put(eventJson);
        long lastSyncTimeStamp = 2604995368l;
        when(allSharedPreferences.fetchLastSyncDate(0)).thenReturn(lastSyncTimeStamp);
        syncHelper.batchInsertEvents(eventsArray);
        verify(eventClientRepository).batchInsertEvents(eventsArray,lastSyncTimeStamp);
    }

    @Test
    public void testDeleteClient(){
        syncHelper.deleteClient(clientBaseEntityId);
        verify(eventClientRepository).deleteClient(clientBaseEntityId);
    }

}
