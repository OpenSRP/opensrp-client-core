package org.smartregister.job;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.evernote.android.job.Job;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;

/**
 * Created by Vincent Karuri on 17/11/2020
 */
public abstract class ServiceJobTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Mock
    private ComponentName componentName;

    private BaseJob jobSpy;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initializeMocks();
    }

    private void initializeMocks() {
        Mockito.doReturn(context).when(getJobSpy()).getApplicationContext();
        Mockito.doReturn(componentName).when(context).startService(ArgumentMatchers.any(Intent.class));
    }

    protected abstract String getServiceId();

    protected abstract BaseJob getJob();

    private BaseJob getJobSpy() {
        if (jobSpy == null) {
            jobSpy = Mockito.spy(getJob());
        }
        return jobSpy;
    }

    @Test
    public void testOnRunJobStartsCorrectService() {
        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);
        ReflectionHelpers.callInstanceMethod(getJobSpy(), "onRunJob",
                ReflectionHelpers.ClassParameter.from(Job.Params.class, null));
        Mockito.verify(context).startService(intent.capture());
        Assert.assertEquals(getServiceId(), intent.getValue().getComponent().getClassName());
    }
}
