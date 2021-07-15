package org.smartregister.sync.helper;

import androidx.annotation.NonNull;

import com.evernote.android.job.JobManager;

import org.joda.time.DateTime;
import org.smartregister.domain.Action;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Timing;
import org.smartregister.domain.TimingRepeat;
import org.smartregister.domain.Trigger;
import org.smartregister.job.PlanPeriodicEvaluationJob;
import org.smartregister.pathevaluator.TriggerType;
import org.smartregister.view.activity.DrishtiApplication;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 11-03-2021.
 */
public class PeriodicTriggerEvaluationHelper {


    private DateTime timeNow;

    public void reschedulePeriodicPlanEvaluations(List<PlanDefinition> plans) {
        for (PlanDefinition plan: plans) {
            List<Action> actions = plan.getActions();
            if (actions != null && actions.size() > 0) {
                for (Action action : actions) {
                    Set<Trigger> triggers = action.getTrigger();
                    if (triggers != null) {
                        for (Trigger trigger: triggers) {
                            // This assumes that the action has only one periodic trigger
                            if (triggers != null && trigger.getType() != null
                                    && TriggerType.PERIODIC.value().equals(trigger.getType())
                                    && isValidDailyTriggerSchedule(trigger)) {
                                // Check if the jobs for the action have been scheduled
                                // Delete & reschedule the job using the action.code as the job ID
                                int cancelledJobs = cancelJobsForAction(action.getIdentifier(), action.getCode());
                                Timber.i("Cancelled %d jobs for action-code [%s] and action-identifier [%s]"
                                        , cancelledJobs, action.getCode(), action.getIdentifier());

                                // Reschedule the job again using the timing
                                scheduleActionJob(action, trigger.getTimingTiming(), plan.getIdentifier());

                                break;
                            }
                        }
                    }
                }
            }

        }
    }

    protected boolean scheduleActionJob(@NonNull Action action, @NonNull Timing timing, @NonNull String planId) {
        List<DateTime> eventLists = timing.getEvent();
        TimingRepeat timingRepeat = timing.getRepeat();

        boolean scheduled = false;

        for (DateTime dateTime: eventLists) {
            if (dateTime.isBefore(now()) && timingRepeat != null && timingRepeat.getFrequency() == 1
                    && timingRepeat.getPeriodUnit().equals(TimingRepeat.DurationCode.d)) {
                List<Time> timesOfDay = timingRepeat.getTimeOfDay();

                // Schedule a job everyday for each time
                for (Time timeOfDay: timesOfDay) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(timeOfDay);

                    String jobTag = PlanPeriodicEvaluationJob.generateJobTag(action.getIdentifier(), action.getCode());
                    PlanPeriodicEvaluationJob.scheduleEverydayAt(jobTag, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), action, planId);
                    scheduled = true;
                }
            }
        }

        return scheduled;
    }

    public boolean isValidDailyTriggerSchedule(Trigger trigger) {
        Timing timing = trigger.getTimingTiming();
        List<DateTime> eventLists = timing.getEvent();

        for (DateTime dateTime: eventLists) {
            if (dateTime.isBefore(now())) {
                TimingRepeat repeat = timing.getRepeat();
                if (repeat != null && repeat.getFrequency() == 1 && repeat.getPeriodUnit().equals(TimingRepeat.DurationCode.d)) {
                    return true;
                }
            }
        }

        return false;
    }

    public int cancelJobsForAction(String actionIdentifier, String actionCode) {
        int jobsCancelled = 0;

        String jobTag = PlanPeriodicEvaluationJob.generateJobTag(actionIdentifier, actionCode);

        JobManager jobManager = JobManager.create(DrishtiApplication.getInstance());
        jobsCancelled = jobManager.cancelAllForTag(jobTag);

        return jobsCancelled;
    }

    protected DateTime now() {
        return timeNow != null ? timeNow : DateTime.now();
    }

    protected void setNow(DateTime timeNow) {
        this.timeNow = timeNow;
    }
}
