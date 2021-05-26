package org.smartregister.job;

import android.content.Context;

import com.evernote.android.job.JobRequest;
import com.evernote.android.job.ShadowJobManager;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 10-11-2020.
 */
public class BaseJobTest extends BaseRobolectricUnitTest {

    @Test
    public void scheduleJobShouldAddJobToJobManagerAndRescheduleWhenLessThan15Minutes() {
        ArgumentCaptor<JobRequest> jobRequestArgumentCaptor = ArgumentCaptor.forClass(JobRequest.class);

        BaseJob.scheduleJob("my-job", TimeUnit.MINUTES.toMillis(10), TimeUnit.MINUTES.toMillis(5));

        Mockito.verify(ShadowJobManager.mockJobManager).schedule(jobRequestArgumentCaptor.capture());

        JobRequest jobRequest = jobRequestArgumentCaptor.getValue();
        Assert.assertTrue(jobRequest.getExtras()
                .getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false));
    }

    @Test
    public void getApplicationContextShouldReturnSameContextInstance() {
        BaseJob baseJob =Mockito.mock(BaseJob.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(baseJob, "mContextReference", new WeakReference<Context>(RuntimeEnvironment.application));
        Assert.assertEquals(RuntimeEnvironment.application, baseJob.getApplicationContext());
    }
}