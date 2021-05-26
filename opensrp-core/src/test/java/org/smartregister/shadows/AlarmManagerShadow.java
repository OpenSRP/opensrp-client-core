package org.smartregister.shadows;

import android.app.AlarmManager;
import android.app.PendingIntent;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowAlarmManager;

/**
 * Created by Raihan Ahmed on 17/12/17.
 */
@Implements(AlarmManager.class)
public class AlarmManagerShadow extends ShadowAlarmManager {


    public AlarmManagerShadow() {

    }

    @Implementation
    public void setRepeating(int type, long triggerAtMillis,
                             long intervalMillis, PendingIntent operation) {

    }

}
