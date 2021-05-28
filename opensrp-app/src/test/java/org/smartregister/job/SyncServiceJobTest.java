package org.smartregister.job;

import org.smartregister.sync.intent.SyncIntentService;

/**
 * Created by ndegwamartin on 10/09/2018.
 */
public class SyncServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.sync.intent.SyncIntentService";
    }

    @Override
    protected BaseJob getJob() {
        return new SyncServiceJob(SyncIntentService.class);
    }
}
