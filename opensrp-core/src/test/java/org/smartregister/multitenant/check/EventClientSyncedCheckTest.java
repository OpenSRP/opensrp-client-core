package org.smartregister.multitenant.check;

import com.evernote.android.job.ShadowJobManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.domain.FetchStatus;
import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.view.activity.DrishtiApplication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 22-04-2020.
 */
public class EventClientSyncedCheckTest extends BaseRobolectricUnitTest {

    private EventClientSyncedCheck eventClientSyncedCheck;

    @Before
    public void setUp() throws Exception {
        eventClientSyncedCheck = Mockito.spy(new EventClientSyncedCheck());
    }

    @Test
    public void isCheckOkShouldCallIsEventsClientSynced() {
        Mockito.doReturn(false).when(eventClientSyncedCheck).isEventsClientSynced(Mockito.eq(DrishtiApplication.getInstance()));
        eventClientSyncedCheck.isCheckOk(DrishtiApplication.getInstance());

        Mockito.verify(eventClientSyncedCheck).isEventsClientSynced(Mockito.eq(DrishtiApplication.getInstance()));
    }

    @Test
    public void isEventsClientSyncedShouldReturnTrueWhenUnsyncedEventCountIsZero() {
        EventClientRepository eventClientRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getEventClientRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "eventClientRepository", eventClientRepository);

        Mockito.doReturn(0).when(eventClientRepository).getUnSyncedEventsCount();

        assertTrue(eventClientSyncedCheck.isEventsClientSynced(DrishtiApplication.getInstance()));
        Mockito.verify(eventClientRepository).getUnSyncedEventsCount();
    }

    @Test
    public void isEventsClientSyncedShouldReturnFalseWhenUnsyncedEventCountIsAboveZero() {
        EventClientRepository eventClientRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getEventClientRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "eventClientRepository", eventClientRepository);

        Mockito.doReturn(1).when(eventClientRepository).getUnSyncedEventsCount();

        assertFalse(eventClientSyncedCheck.isEventsClientSynced(DrishtiApplication.getInstance()));
        Mockito.verify(eventClientRepository).getUnSyncedEventsCount();
    }

    @Test
    public void isEventsClientSyncedShouldReturnFalseWhenEventClientRepositoryIsNull() {
        Context mockedContext = Mockito.spy(DrishtiApplication.getInstance().getContext());
        DrishtiApplication mockedDrishtiApplication = Mockito.spy(DrishtiApplication.getInstance());
        Mockito.doReturn(mockedContext).when(mockedDrishtiApplication).getContext();
        Mockito.doReturn(null).when(mockedContext).getEventClientRepository();

        // Perform assertions & call method under test
        assertFalse(eventClientSyncedCheck.isEventsClientSynced(mockedDrishtiApplication));
        assertNull(mockedDrishtiApplication.getContext().getEventClientRepository());
    }

    @Test
    public void performPreResetAppOperations() throws PreResetAppOperationException {
        eventClientSyncedCheck.performPreResetAppOperations(DrishtiApplication.getInstance());

        // Verify that performSync() was called
        Mockito.verify(eventClientSyncedCheck).onSyncStart();
        Mockito.verify(eventClientSyncedCheck).onSyncComplete(FetchStatus.fetchedFailed);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        ShadowJobManager.mockJobManager = null;
    }
}