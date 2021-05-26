package org.smartregister.job;

import org.smartregister.sync.intent.SyncTaskIntentService;

public class SyncTaskServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.sync.intent.SyncTaskIntentService";
    }

    @Override
    protected BaseJob getJob() {
        return new SyncTaskServiceJob(SyncTaskIntentService.class);
    }
}
