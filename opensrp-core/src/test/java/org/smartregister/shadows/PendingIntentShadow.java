package org.smartregister.shadows;

import android.app.PendingIntent;
import android.content.Intent;

import org.mockito.Mockito;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowPendingIntent;

/**
 * Created by Raihan Ahmed on 17/12/17.
 */
@Implements(PendingIntent.class)
public class PendingIntentShadow extends ShadowPendingIntent {

    public static PendingIntent realPendingIntentObject;

    public PendingIntentShadow() {

    }

    public static PendingIntent getBroadcast(android.content.Context context, int requestCode,
                                             Intent intent, int flags) {
        realPendingIntentObject = Mockito.mock(PendingIntent.class);
        return realPendingIntentObject;
    }

}
