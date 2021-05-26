package org.smartregister.receiver;

import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.domain.FetchStatus;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 28-07-2020.
 */
public class SyncStatusBroadcastReceiverTest extends BaseRobolectricUnitTest {

    private SyncStatusBroadcastReceiver syncStatusBroadcastReceiver;

    @Before
    public void setUp() throws Exception {
        syncStatusBroadcastReceiver = Mockito.spy(new SyncStatusBroadcastReceiver());
    }

    @Test
    public void onReceiveShouldCallListenerOnSyncStartWhenIsFetchStatusStarted() {
        // Add listener
        SyncStatusBroadcastReceiver.SyncStatusListener listener = Mockito.mock(SyncStatusBroadcastReceiver.SyncStatusListener.class);
        syncStatusBroadcastReceiver.addSyncStatusListener(listener);

        FetchStatus fetchStatus = FetchStatus.fetchStarted;
        Intent intent = new Intent();
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);

        // Call the method under test
        syncStatusBroadcastReceiver.onReceive(RuntimeEnvironment.application, intent);


        // Verify onSyncStart
        Mockito.verify(listener).onSyncStart();
        Assert.assertTrue(syncStatusBroadcastReceiver.isSyncing());
        Assert.assertEquals(0, (long) ReflectionHelpers.getField(syncStatusBroadcastReceiver, "lastFetchedTimestamp"));
    }

    @Test
    public void onReceiveShouldCallListenerOnSyncCompleteWhenIsFetchStatusNothingFetchedAndCompleteStatusIsTrue() {
        // Add listener
        SyncStatusBroadcastReceiver.SyncStatusListener listener = Mockito.mock(SyncStatusBroadcastReceiver.SyncStatusListener.class);
        syncStatusBroadcastReceiver.addSyncStatusListener(listener);

        // Disable calling start extended sync
        Mockito.doNothing().when(syncStatusBroadcastReceiver).startExtendedSync();

        FetchStatus fetchStatus = FetchStatus.nothingFetched;
        Intent intent = new Intent();
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, true);

        // Call the method under test
        syncStatusBroadcastReceiver.onReceive(RuntimeEnvironment.application, intent);


        // Verify expected behaviours and properties
        Mockito.verify(listener).onSyncComplete(fetchStatus);
        Assert.assertFalse(syncStatusBroadcastReceiver.isSyncing());
        Assert.assertEquals(0, (long) ReflectionHelpers.getField(syncStatusBroadcastReceiver, "lastFetchedTimestamp"));
        Mockito.verify(syncStatusBroadcastReceiver).startExtendedSync();
    }

    @Test
    public void onReceiveShouldCallListenerOnSyncInProgressWhenIsFetchStatusProgressAndCompleteStatusIsFalse() {
        // Add listener
        SyncStatusBroadcastReceiver.SyncStatusListener listener = Mockito.mock(SyncStatusBroadcastReceiver.SyncStatusListener.class);
        syncStatusBroadcastReceiver.addSyncStatusListener(listener);

        // Disable calling start extended sync
        Mockito.doNothing().when(syncStatusBroadcastReceiver).startExtendedSync();

        FetchStatus fetchStatus = FetchStatus.fetchProgress;
        Intent intent = new Intent();
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, false);

        // Call the method under test
        syncStatusBroadcastReceiver.onReceive(RuntimeEnvironment.application, intent);


        // Verify expected behaviours and properties
        Mockito.verify(listener).onSyncInProgress(fetchStatus);
        Assert.assertTrue(syncStatusBroadcastReceiver.isSyncing());

        long timeMillis = System.currentTimeMillis();
        Assert.assertEquals(timeMillis, (long) ReflectionHelpers.getField(syncStatusBroadcastReceiver, "lastFetchedTimestamp"), 4000);
    }

    @Test
    public void onReceiveShouldNotCallListenerOnSyncInProgressWhenIsFetchStatusProgressAndCompleteStatusIsFalseAndLastFetchedTimestampWasLessThan2Minutes() {
        // Add listener
        SyncStatusBroadcastReceiver.SyncStatusListener listener = Mockito.mock(SyncStatusBroadcastReceiver.SyncStatusListener.class);
        syncStatusBroadcastReceiver.addSyncStatusListener(listener);

        // Disable calling start extended sync
        Mockito.doNothing().when(syncStatusBroadcastReceiver).startExtendedSync();

        // Set last timestamp to less than 2 minutes ago
        long lastFetchedTimestamp = System.currentTimeMillis();
        ReflectionHelpers.setField(syncStatusBroadcastReceiver, "lastFetchedTimestamp", lastFetchedTimestamp);

        FetchStatus fetchStatus = FetchStatus.fetchProgress;
        Intent intent = new Intent();
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, false);

        // Call the method under test
        syncStatusBroadcastReceiver.onReceive(RuntimeEnvironment.application, intent);


        // Verify expected behaviours and properties
        Mockito.verify(listener, Mockito.times(0)).onSyncInProgress(fetchStatus);
        Assert.assertTrue(syncStatusBroadcastReceiver.isSyncing());
        Assert.assertEquals(lastFetchedTimestamp, (long) ReflectionHelpers.getField(syncStatusBroadcastReceiver, "lastFetchedTimestamp"));
    }
}