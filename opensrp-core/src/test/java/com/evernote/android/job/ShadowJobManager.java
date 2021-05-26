package com.evernote.android.job;

import android.content.Context;
import androidx.annotation.NonNull;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 22-04-2020.
 */
@Implements(JobManager.class)
public class ShadowJobManager {

    public static JobManager mockJobManager;
    public static JobStorage jobStorage;

    @Implementation
    public static JobManager create(@NonNull Context context) throws JobManagerCreateException {

        return createMockJobManager();
    }

    @Implementation
    public static JobManager instance() {
        return mockJobManager;
    }

    public static JobManager createMockJobManager() {
        if (mockJobManager == null) {
            mockJobManager = Mockito.mock(JobManager.class);
            jobStorage = Mockito.mock(JobStorage.class);
            Mockito.doReturn(jobStorage).when(mockJobManager).getJobStorage();
        }

        return mockJobManager;
    }
}
