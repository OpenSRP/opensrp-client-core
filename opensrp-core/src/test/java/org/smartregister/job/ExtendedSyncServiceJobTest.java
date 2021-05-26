package org.smartregister.job;

/**
 * Created by ndegwamartin on 10/09/2018.
 */
public class ExtendedSyncServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.sync.intent.ExtendedSyncIntentService";
    }

    @Override
    protected BaseJob getJob() {
        return new ExtendedSyncServiceJob();
    }
}
