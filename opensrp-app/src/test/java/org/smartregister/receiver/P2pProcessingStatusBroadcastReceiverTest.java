package org.smartregister.receiver;

import android.content.Intent;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 04-08-2020.
 */
public class P2pProcessingStatusBroadcastReceiverTest extends BaseRobolectricUnitTest {

    @Mock
    private P2pProcessingStatusBroadcastReceiver.StatusUpdate statusUpdate;

    @Test
    public void onReceiveShouldCallListenerStatusUpdateMethodOnStatusUpdate() {
        P2pProcessingStatusBroadcastReceiver p2pProcessingStatusBroadcastReceiver = new P2pProcessingStatusBroadcastReceiver(statusUpdate);

        Intent intent = new Intent();
        intent.putExtra(AllConstants.PeerToPeer.KEY_IS_PROCESSING, true);

        // Call the method under test
        p2pProcessingStatusBroadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        Mockito.verify(statusUpdate).onStatusUpdate(true);
    }


    @Test
    public void onReceiveShouldCallNotCallOnStatusUpdateListenerWhenIntentLacksExtra() {
        P2pProcessingStatusBroadcastReceiver p2pProcessingStatusBroadcastReceiver = new P2pProcessingStatusBroadcastReceiver(statusUpdate);

        Intent intent = new Intent();

        // Call the method under test
        p2pProcessingStatusBroadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        Mockito.verify(statusUpdate, Mockito.never()).onStatusUpdate(Mockito.anyBoolean());
    }
}