package org.smartregister.shadows;

import android.content.Context;
import androidx.annotation.NonNull;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobManagerCreateException;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 22-04-2020.
 */
@Implements(JobManager.class)
public class ShadowJobManager {

    public static JobManager mockJobManager;

    @Implementation
    public static JobManager create(@NonNull Context context) throws JobManagerCreateException {

        if (mockJobManager == null) {
            mockJobManager = Mockito.mock(JobManager.class);
        }

        return mockJobManager;
    }
}
