package org.smartregister.job;

public class SyncLocationsByLevelAndTagsServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.sync.intent.SyncLocationsByLevelAndTagsIntentService";
    }

    @Override
    protected BaseJob getJob() {
        return new SyncLocationsByLevelAndTagsServiceJob();
    }
}
