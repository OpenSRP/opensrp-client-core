package org.smartregister.job;

public class CampaignServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.sync.intent.CampaignIntentService";
    }

    @Override
    protected BaseJob getJob() {
        return new CampaignServiceJob();
    }
}
