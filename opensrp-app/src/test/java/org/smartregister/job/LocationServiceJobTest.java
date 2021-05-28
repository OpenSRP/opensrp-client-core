package org.smartregister.job;

public class LocationServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.sync.intent.LocationIntentService";
    }

    @Override
    protected BaseJob getJob() {
        return new LocationStructureServiceJob();
    }
}
