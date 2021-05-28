package org.smartregister.receiver;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.domain.SyncProgress;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 04-08-2020.
 */
public class SyncProgressBroadcastReceiverTest extends BaseRobolectricUnitTest {

    private SyncProgressBroadcastReceiver syncProgressBroadcastReceiver;

    @Mock
    private SyncProgressBroadcastReceiver.SyncProgressListener syncProgressListener;

    @Before
    public void setUp() throws Exception {
        syncProgressBroadcastReceiver = new SyncProgressBroadcastReceiver(syncProgressListener);
    }

    @Test
    public void onReceiveShouldCallListenerOnSyncProgress() {
        SyncProgress syncProgress = Mockito.mock(SyncProgress.class);

        Intent intent = new Intent();
        intent.putExtra(AllConstants.SyncProgressConstants.SYNC_PROGRESS_DATA, syncProgress);

        // Call the method under test
        syncProgressBroadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        // Verify that the listener was called
        Mockito.verify(syncProgressListener).onSyncProgress(syncProgress);
    }

    @Test
    public void onReceiveShouldNotCallListenerOnSyncProgressWhenBundleIsNull() {
        Intent intent = new Intent();

        // Call the method under test
        syncProgressBroadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        // Verify that the listener was called
        Mockito.verify(syncProgressListener, Mockito.never()).onSyncProgress(Mockito.nullable(SyncProgress.class));
    }
}