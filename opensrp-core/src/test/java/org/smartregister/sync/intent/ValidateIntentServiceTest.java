package org.smartregister.sync.intent;

import android.content.Intent;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.SyncConfiguration;
import org.smartregister.TestApplication;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Vincent Karuri on 22/09/2020
 */
public class ValidateIntentServiceTest extends BaseUnitTest {

    private ValidateIntentService validateIntentService;

    @Mock
    private HTTPAgent httpAgent;
    @Mock
    private Context openSRPContext;
    @Mock
    private EventClientRepository eventClientRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMethods();
        validateIntentService = Robolectric.buildIntentService(ValidateIntentService.class).get();
        ReflectionHelpers.setField(validateIntentService, "context", RuntimeEnvironment.application);
        ReflectionHelpers.setField(validateIntentService, "httpAgent", httpAgent);
        ReflectionHelpers.setField(validateIntentService, "openSRPContext", openSRPContext);
    }

    @Test
    public void testOnHandleIntent() {

        Client client = new Client("client_id1");
        Event event = new Event();
        event.setEventId("event_id1");

        when(eventClientRepository.fetchClientByBaseEntityIds(any())).thenReturn(Collections.singletonList(client));
        when(eventClientRepository.getEventsByEventIds(any())).thenReturn(Collections.singletonList(event));

        validateIntentService.onHandleIntent(new Intent());

        Mockito.verify(eventClientRepository).markClientValidationStatus(getClientIds().get(0), false);
        Mockito.verify(eventClientRepository).markClientValidationStatus(getClientIds().get(1), true);

        Mockito.verify(eventClientRepository).markEventValidationStatus(getEventIds().get(0), false);
        Mockito.verify(eventClientRepository).markEventValidationStatus(getEventIds().get(1), true);
    }

    @Test
    public void testOnHandleIntentWithArchivedEventAndClient() {

        Client client = new Client("client_id1");
        client.setDateVoided(DateTime.now());

        Event event = new Event();
        event.setEventId("event_id1");
        event.setDateVoided(DateTime.now());

        when(eventClientRepository.fetchClientByBaseEntityIds(any())).thenReturn(Collections.singletonList(client));
        when(eventClientRepository.getEventsByEventIds(any())).thenReturn(Collections.singletonList(event));

        validateIntentService.onHandleIntent(new Intent());

        Mockito.verify(eventClientRepository).markClientValidationStatus(getClientIds().get(0), true);
        Mockito.verify(eventClientRepository).markClientValidationStatus(getClientIds().get(1), true);

        Mockito.verify(eventClientRepository).markEventValidationStatus(getEventIds().get(0), true);
        Mockito.verify(eventClientRepository).markEventValidationStatus(getEventIds().get(1), true);
    }


    @After
    public void tearDown() {
        TestApplication.getInstance().initCoreLibrary();
    }

    private void mockMethods() throws JSONException {
        CoreLibrary coreLibrary = mock(CoreLibrary.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        doReturn(openSRPContext).when(coreLibrary).context();
        doReturn(mock(HTTPAgent.class)).when(openSRPContext).httpAgent();
        doReturn(mock(SyncConfiguration.class)).when(coreLibrary).getSyncConfiguration();

        doReturn(eventClientRepository).when(openSRPContext).getEventClientRepository();
        doReturn(getClientIds()).when(eventClientRepository)
                .getUnValidatedClientBaseEntityIds(ArgumentMatchers.anyInt());
        doReturn(getEventIds()).when(eventClientRepository)
                .getUnValidatedEventFormSubmissionIds(ArgumentMatchers.anyInt());

        DristhiConfiguration dristhiConfiguration = mock(DristhiConfiguration.class);
        doReturn("/").when(dristhiConfiguration).dristhiBaseURL();
        doReturn(dristhiConfiguration).when(openSRPContext).configuration();

        JSONObject results = new JSONObject();
        JSONArray invalidClients = new JSONArray(Collections.singletonList("client_id1"));
        results.put(AllConstants.KEY.CLIENTS, invalidClients);

        JSONArray invalidEvents = new JSONArray(Collections.singletonList("event_id1"));
        results.put(AllConstants.KEY.EVENTS, invalidEvents);

        Response<String> response = new Response<>(ResponseStatus.success, results.toString());
        doReturn(response).when(httpAgent).postWithJsonResponse(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    private List<String> getEventIds() {
        return new ArrayList(Arrays.asList("event_id1", "event_id2"));
    }

    private List<String> getClientIds() {
        return new ArrayList(Arrays.asList("client_id1", "client_id2"));
    }
}