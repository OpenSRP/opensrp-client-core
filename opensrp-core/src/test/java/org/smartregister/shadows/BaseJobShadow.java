package org.smartregister.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.smartregister.job.BaseJob;

/**
 * Created by Vincent Karuri on 19/01/2021
 */

@Implements(BaseJob.class)
public class BaseJobShadow extends Shadow {

    private static MockCounter mockCounter = new MockCounter();

    @Implementation
    public static void scheduleJobImmediately(String jobTag) {
        mockCounter.setCount(1);
    }

    public static MockCounter getMockCounter() {
        return mockCounter;
    }
}
