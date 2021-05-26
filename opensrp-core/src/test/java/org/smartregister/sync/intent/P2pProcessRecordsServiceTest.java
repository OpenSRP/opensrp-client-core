package org.smartregister.sync.intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.TestP2pApplication;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 25-08-2020.
 */
@Config(application = TestP2pApplication.class)
public class P2pProcessRecordsServiceTest extends BaseRobolectricUnitTest {

    private P2pProcessRecordsService p2pProcessRecordsService;


    private EventClientRepository eventClientRepository;
    private ClientProcessorForJava clientProcessorForJava;
    private AllSharedPreferences allSharedPreferences;

    @Before
    public void setUp() throws Exception {
        p2pProcessRecordsService = Mockito.spy(Robolectric.buildService(P2pProcessRecordsService.class)
                .create()
                .get());

        eventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "eventClientRepository", eventClientRepository);

        clientProcessorForJava = Mockito.spy(DrishtiApplication.getInstance().getClientProcessor());
        ReflectionHelpers.setStaticField(ClientProcessorForJava.class, "instance", clientProcessorForJava);


        allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().allSharedPreferences());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", allSharedPreferences);
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(ClientProcessorForJava.class, "instance", null);
        initCoreLibrary();
    }

    @Test
    public void onHandleIntentShouldNotProcessEventsIfPeerToPeerUnprocessedEventsReturnsFalse() throws Exception {
        // Mock dependencies
        int maxEventClientRowId = 20;
        CoreLibrary.getInstance().context().allSharedPreferences().setLastPeerToPeerSyncProcessedEvent(maxEventClientRowId);
        List<EventClient> eventClientList = new ArrayList<>();
        eventClientList.add(new EventClient(null, null));
        Mockito.doReturn(new P2pProcessRecordsService.EventClientQueryResult(maxEventClientRowId, eventClientList)).when(eventClientRepository).fetchEventClientsByRowId(maxEventClientRowId);
        Mockito.doReturn(maxEventClientRowId).when(eventClientRepository).getMaxRowId(EventClientRepository.Table.event);
        Mockito.doNothing().when(clientProcessorForJava).processClient(eventClientList);

        // Call method under test
        p2pProcessRecordsService.onHandleIntent(null);

        // Verifications and assertions
        Mockito.verify(eventClientRepository, Mockito.times(1)).getMaxRowId(EventClientRepository.Table.event);
        Mockito.verify(eventClientRepository, Mockito.times(1)).fetchEventClientsByRowId(maxEventClientRowId);
        Mockito.verify(clientProcessorForJava, Mockito.times(1)).processClient(eventClientList);
        Mockito.verify(allSharedPreferences, Mockito.times(1)).resetLastPeerToPeerSyncProcessedEvent();
        Assert.assertTrue(CoreLibrary.getInstance().isPeerToPeerProcessing());
        Mockito.verify(p2pProcessRecordsService, Mockito.times(1)).sendSyncStatusBroadcastMessage(FetchStatus.fetched);
    }


    @Test
    public void onHandleIntentShouldProcessEventsIfPeerToPeerUnprocessedEventsReturnsTrue() throws Exception {

        p2pProcessRecordsService.onHandleIntent(null);

        Mockito.verify(eventClientRepository, Mockito.never()).getMaxRowId(Mockito.any(EventClientRepository.Table.class));
        Mockito.verify(clientProcessorForJava, Mockito.never()).processClient(ArgumentMatchers.<List< EventClient>>any());
    }

    @Test
    public void onDestroyShouldSetPeerProcessingToFalse() {
        CoreLibrary.getInstance().setPeerToPeerProcessing(true);
        Assert.assertTrue(CoreLibrary.getInstance().isPeerToPeerProcessing());

        // Call the method
        p2pProcessRecordsService.onDestroy();

        Assert.assertFalse(CoreLibrary.getInstance().isPeerToPeerProcessing());
    }
}