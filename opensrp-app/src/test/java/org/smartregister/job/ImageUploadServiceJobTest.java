package org.smartregister.job;

/**
 * Created by Vincent Karuri on 17/11/2020
 */
public class ImageUploadServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.service.ImageUploadSyncService";
    }

    @Override
    protected BaseJob getJob() {
        return new ImageUploadServiceJob();
    }
}