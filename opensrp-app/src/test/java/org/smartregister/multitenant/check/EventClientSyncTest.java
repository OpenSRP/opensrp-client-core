package org.smartregister.multitenant.check;

import android.app.Application;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.List;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 30-03-2021.
 */
public class EventClientSyncTest extends BaseRobolectricUnitTest {

    private EventClientSync eventClientSync;

    @Before
    public void setUp() throws Exception {
        eventClientSync = Mockito.spy(new EventClientSync(ApplicationProvider.getApplicationContext()));
    }

    @Test
    public void performSync() {
        Assert.assertNull(eventClientSync.getHttpAgent());

        eventClientSync.performSync();

        // Verify that protected init() was called
        Assert.assertNotNull(eventClientSync.getHttpAgent());
        // Verify that protected handleSync() was called
        List<Intent> broadcastIntents = Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).getBroadcastIntents();

        Intent targetIntent = null;
        for (Intent intent: broadcastIntents) {
            if (intent.getAction().equals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS)
                    && intent.getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS)
                    .equals(FetchStatus.fetchStarted)) {
                targetIntent = intent;
            }
        }

        Assert.assertEquals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, targetIntent.getAction());
        Assert.assertEquals(FetchStatus.fetchStarted, targetIntent.getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS));
    }

    @Test
    public void pullECFromServerShouldDoNothing() {
        eventClientSync.pullECFromServer();

        // Assert that no broadcast was sent - of SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS
        List<Intent> broadcastIntents = Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).getBroadcastIntents();
        Intent targetIntent = null;
        for (Intent intent: broadcastIntents) {
            if (intent.getAction().equals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS)) {
                targetIntent = intent;
            }
        }

        Assert.assertNull(targetIntent);
    }

}