package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Action;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.job.PlanPeriodicEvaluationJob;
import org.smartregister.pathevaluator.TriggerType;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.sync.helper.PeriodicTriggerEvaluationHelper;

import timber.log.Timber;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlanPeriodicPlanEvaluationService extends IntentService {

    private DateTime timeNow;
    private PeriodicTriggerEvaluationHelper periodicTriggerEvaluationHelper;

    public PlanPeriodicPlanEvaluationService() {
        super("PlanPeriodicPlanEvaluationService");
        periodicTriggerEvaluationHelper =  new PeriodicTriggerEvaluationHelper();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            String planId = intent.getStringExtra(AllConstants.INTENT_KEY.PLAN_ID);
            String actionIdentifier = intent.getStringExtra(AllConstants.INTENT_KEY.ACTION_IDENTIFIER);
            String actionCode = intent.getStringExtra(AllConstants.INTENT_KEY.ACTION_CODE);
            String actionJsonString = intent.getStringExtra(AllConstants.INTENT_KEY.ACTION);

            if (TextUtils.isEmpty(planId) || TextUtils.isEmpty(actionJsonString)
                    || TextUtils.isEmpty(actionIdentifier) || TextUtils.isEmpty(actionCode)) {
                Timber.e(new Exception(), "Periodic action was not evaluated since planId, action, action-identifier OR action-code was empty");
                return;
            }

            Action action = PlanPeriodicEvaluationJob.gson.fromJson(actionJsonString, Action.class);
            if (action != null) {
                PlanDefinitionRepository planDefinitionRepository = CoreLibrary.getInstance().context()
                        .getPlanDefinitionRepository();
                PlanDefinition planDefinition = planDefinitionRepository.findPlanDefinitionById(planId);


                if ((planDefinition.getEffectivePeriod() != null && planDefinition.getEffectivePeriod().getEnd().isBefore(now()))
                        || (action.getTimingPeriod() != null && action.getTimingPeriod().getEnd().isBefore(now()))) {
                    periodicTriggerEvaluationHelper.cancelJobsForAction(actionIdentifier, actionCode);
                } else {
                    AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
                    PlanEvaluator planEvaluator = new PlanEvaluator(allSharedPreferences.fetchRegisteredANM());
                    Jurisdiction jurisdiction = new Jurisdiction(allSharedPreferences.fetchDefaultLocalityId(allSharedPreferences.fetchRegisteredANM()));

                    // TODO: Change this to evaluate a single action
                    planEvaluator.evaluatePlanAction(planDefinition, TriggerType.PERIODIC, jurisdiction, null, action);
                }

                return;
            }
        }

        Timber.e(new Exception(), "An error occurred and the service did not evaluate the plan/action");

    }


    protected DateTime now() {
        return timeNow != null ? timeNow : DateTime.now();
    }

    protected void setNow(DateTime timeNow) {
        this.timeNow = timeNow;
    }
}