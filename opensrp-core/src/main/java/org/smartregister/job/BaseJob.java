package org.smartregister.job;

import android.content.Context;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import org.smartregister.AllConstants;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 05/09/2018.
 */
public abstract class BaseJob extends Job {


    public static void scheduleJob(String jobTag, Long start, Long flex) {

        if (JobManager.instance().getAllJobRequestsForTag(jobTag).isEmpty()) {

            boolean toReschedule = start < TimeUnit.MINUTES.toMillis(15); //evernote doesn't allow less than 15 mins periodic schedule, keep flag ref for workaround

            PersistableBundleCompat extras = new PersistableBundleCompat();
            extras.putBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, toReschedule);

            JobRequest.Builder jobRequest = new JobRequest.Builder(jobTag).setExtras(extras);

            jobRequest.setPeriodic(TimeUnit.MINUTES.toMillis(start), TimeUnit.MINUTES.toMillis(flex));

            try {

                int jobId = jobRequest.build().schedule();
                Timber.d("Scheduling job with name " + jobTag + " : JOB ID " + jobId + " periodically every " + start + " minutes and flex value of " + flex + " minutes");

            } catch (Exception e) {
                Timber.e(e);
            }
        } else {
            Timber.d("Skipping schedule for job with name " + jobTag + " : Already Exists!");

        }
    }

    /**
     * For jobs that need to be started immediately
     */
    public static void scheduleJobImmediately(String jobTag) {

        int jobId = new JobRequest.Builder(jobTag)
                .startNow()
                .build()
                .schedule();

        Timber.d("Scheduling job with name " + jobTag + " immediately with JOB ID " + jobId);

    }

    @Override
    protected void onReschedule(int newJobId) {

        Timber.d("Rescheduling job with name " + this.getParams().getTag() + " JOB ID " + newJobId);
    }

    //Custom app context method to aid unit testing
    public Context getApplicationContext() {
        return super.getContext();
    }
}
