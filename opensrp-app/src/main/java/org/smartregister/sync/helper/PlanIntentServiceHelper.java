package org.smartregister.sync.helper;

import android.content.Context;

import com.google.firebase.perf.metrics.Trace;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Response;
import org.smartregister.domain.SyncEntity;
import org.smartregister.domain.SyncProgress;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;
import org.smartregister.util.Utils;
import org.smartregister.utils.TimingRepeatTimeTypeConverter;

import java.sql.Time;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.AllConstants.COUNT;
import static org.smartregister.AllConstants.PerformanceMonitoring.ACTION;
import static org.smartregister.AllConstants.PerformanceMonitoring.FETCH;
import static org.smartregister.AllConstants.PerformanceMonitoring.PLAN_SYNC;
import static org.smartregister.AllConstants.PerformanceMonitoring.TEAM;
import static org.smartregister.util.PerformanceMonitoringUtils.addAttribute;
import static org.smartregister.util.PerformanceMonitoringUtils.initTrace;
import static org.smartregister.util.PerformanceMonitoringUtils.startTrace;
import static org.smartregister.util.PerformanceMonitoringUtils.stopTrace;

/**
 * Created by Vincent Karuri on 08/05/2019
 */
public class PlanIntentServiceHelper extends BaseHelper {

    private final PlanDefinitionRepository planDefinitionRepository;
    private final AllSharedPreferences allSharedPreferences;
    protected static Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .registerTypeAdapter(Time.class, new TimingRepeatTimeTypeConverter())
            .disableHtmlEscaping()
            .create();

    protected final Context context;
    protected static PlanIntentServiceHelper instance;

    public static final String SYNC_PLANS_URL = "/rest/plans/sync";
    public static final String PLAN_LAST_SYNC_DATE = "plan_last_sync_date";
    private long totalRecords;
    private SyncProgress syncProgress;

    private Trace planSyncTrace;
    private ArrayList<PlanDefinition> planIdsToEvaluate = new ArrayList<>();
    private PeriodicTriggerEvaluationHelper periodicTriggerEvaluationHelper;

    public static PlanIntentServiceHelper getInstance() {
        if (instance == null) {
            instance = new PlanIntentServiceHelper(CoreLibrary.getInstance().context().getPlanDefinitionRepository());
        }
        return instance;
    }

    private PlanIntentServiceHelper(PlanDefinitionRepository planRepository) {
        this.context = CoreLibrary.getInstance().context().applicationContext();
        this.planDefinitionRepository = planRepository;
        this.planSyncTrace  = initTrace(PLAN_SYNC);
        this.allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        periodicTriggerEvaluationHelper = new PeriodicTriggerEvaluationHelper();
    }

    public void syncPlans() {
        syncProgress = new SyncProgress();
        syncProgress.setSyncEntity(SyncEntity.PLANS);
        syncProgress.setTotalRecords(totalRecords);

        int batchFetchCount = batchFetchPlansFromServer(true);

        syncProgress.setPercentageSynced(Utils.calculatePercentage(totalRecords, batchFetchCount));
        sendSyncProgressBroadcast(syncProgress, context);
    }

    private int batchFetchPlansFromServer(boolean returnCount) {
        int batchFetchCount = 0;
        try {
            long serverVersion = 0;
            try {
                serverVersion = Long.parseLong(allSharedPreferences.getPreference(PLAN_LAST_SYNC_DATE));
            } catch (NumberFormatException e) {
                Timber.e(e, "EXCEPTION %s", e.toString());
            }
            if (serverVersion > 0) {
                serverVersion += 1;
            }
            // fetch and save plans
            Long maxServerVersion = 0l;

            String organizationIds = allSharedPreferences.getPreference(AllConstants.ORGANIZATION_IDS);

            startPlanTrace(FETCH);

            String plansResponse = fetchPlans(Arrays.asList(organizationIds.split(",")), serverVersion, returnCount);
            List<PlanDefinition> plans = gson.fromJson(plansResponse, new TypeToken<List<PlanDefinition>>() {
            }.getType());

            ArrayList<PlanDefinition> fixedPlans = new ArrayList<>();

            for (PlanDefinition planDefinition: plans) {
                if ("2d12e224-401d-4d23-80fd-7b0f37e56fc1".equals(planDefinition.getIdentifier())) {
                    String planString = "{\"identifier\":\"2d12e224-401d-4d23-80fd-7b0f37e56fc1\",\"version\":\"1\",\"name\":\"Goldsmith_Supervisor_Template\",\"status\":\"active\",\"date\":\"2021-03-11\",\"effectivePeriod\":{\"start\":\"2020-03-11\",\"end\":\"2025-03-11\"},\"useContext\":[{\"code\":\"interventionType\",\"valueCodableConcept\":\"Linked-PNC\"},{\"code\":\"taskGenerationStatus\",\"valueCodableConcept\":\"internal\"}],\"jurisdiction\":[{\"code\":\"ac7ba751-35e8-4b46-9e53-3cbaad193697\"}],\"goal\":[{\"id\":\"Day_2_Visit\",\"description\":\"Complete the Day 2 Visit form for each child.\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of children with completed Day 2 Visit form completed.\",\"detail\":{\"detailQuantity\":{\"value\":80,\"comparator\":\"&amp;amp;gt;=\",\"unit\":\"Percent\"}},\"due\":\"2021-10-19\"}]}],\"action\":[{\"identifier\":\"5df40280-ea48-4b08-ad9e-4cb937c31110\",\"prefix\":1,\"title\":\"Create PNC Supervisor Follow-up Tasks\",\"description\":\"Create Follow up tasks when PNC is not completed in 48 hours\",\"code\":\"PNC Task Follow Up\",\"timingPeriod\":{\"start\":\"2020-06-04\",\"end\":\"2025-10-01\"},\"reason\":\"Routine\",\"goalId\":\"PNC-Task-Follow-Up\",\"subjectCodableConcept\":{\"text\":\"Global.Task\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"},{\"type\":\"periodic\",\"timingTiming\":{\"event\":[\"2020-03-04\"],\"repeat\":{\"frequency\":1,\"periodUnit\":\"d\",\"timeOfDay\":[\"04:00:00\"]}}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"expression\":\"Task.code.text.value.startsWith('PNC Day') and Task.code.text.value.endsWith('Visit') and Task.status.value = 'ready' and Task.authoredOn < today() - 2 'days'\"}}],\"definitionUri\":\"\",\"type\":\"create\"},{\"identifier\":\"19deb463-7728-4bad-b62a-ccc6a7abb286\",\"prefix\":2,\"title\":\"Create ANC Supervisor Follow-up Tasks\",\"description\":\"Create Follow up tasks when an ANC task is not completed in 48 hours\",\"code\":\"ANC Task Follow Up\",\"timingPeriod\":{\"start\":\"2020-06-04\",\"end\":\"2025-10-01\"},\"reason\":\"Routine\",\"goalId\":\"ANC-Task-Follow-Up\",\"subjectCodableConcept\":{\"text\":\"Global.Task\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"},{\"type\":\"periodic\",\"timingTiming\":{\"event\":[\"2020-03-04\"],\"repeat\":{\"frequency\":1,\"periodUnit\":\"d\",\"timeOfDay\":[\"04:00:00\"]}}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"expression\":\"Task.code.text.value.startsWith('ANC Contact') and Task.status.value = 'ready' and Task.authoredOn < today() - 2 'days'\"}}],\"definitionUri\":\"\",\"type\":\"create\"}],\"experimental\":false,\"serverVersion\":19}";
                    planDefinition = gson.fromJson(planString, PlanDefinition.class);
                }

                fixedPlans.add(planDefinition);
            }

            plans = fixedPlans;

            addAttribute(planSyncTrace, COUNT, String.valueOf(plans.size()));
            stopTrace(planSyncTrace);
            for (PlanDefinition plan : plans) {
                try {
                    planDefinitionRepository.addOrUpdate(plan);
                    planIdsToEvaluate.add(plan);
                } catch (Exception e) {
                    Timber.e(e, "EXCEPTION %s", e.toString());
                }
            }

            // update most recent server version
            if (!Utils.isEmptyCollection(plans)) {
                batchFetchCount = plans.size();
                allSharedPreferences.savePreference(PLAN_LAST_SYNC_DATE, String.valueOf(getPlanDefinitionMaxServerVersion(plans, maxServerVersion)));

                syncProgress.setPercentageSynced(Utils.calculatePercentage(totalRecords, batchFetchCount));
                sendSyncProgressBroadcast(syncProgress, context);

                // retry fetch since there were items synced from the server
                batchFetchPlansFromServer(false);
            }

            if (!planIdsToEvaluate.isEmpty()) {
                periodicTriggerEvaluationHelper.reschedulePeriodicPlanEvaluations(plans);
            }
        } catch (Exception e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
        }

        return batchFetchCount;
    }


    private void startPlanTrace(String action) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        String team = allSharedPreferences.fetchDefaultTeam(providerId);
        addAttribute(planSyncTrace, TEAM, team);
        addAttribute(planSyncTrace, ACTION, action);
        startTrace(planSyncTrace);
    }

    private String fetchPlans(List<String> organizationIds, long serverVersion, boolean returnCount) throws Exception {
        HTTPAgent httpAgent = getHttpAgent();
        String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        JSONObject request = new JSONObject();
        if (!organizationIds.isEmpty()) {
            request.put("organizations", new JSONArray(organizationIds));
        }
        request.put("serverVersion", serverVersion);
        request.put(AllConstants.RETURN_COUNT, returnCount);

        if (httpAgent == null) {
            context.sendBroadcast(Utils.completeSync(FetchStatus.noConnection));
            throw new IllegalArgumentException(SYNC_PLANS_URL + " http agent is null");
        }

        Response resp = httpAgent.post(
                MessageFormat.format("{0}{1}",
                        baseUrl,
                        SYNC_PLANS_URL),
                request.toString());

        if (resp.isFailure()) {
            context.sendBroadcast(Utils.completeSync(FetchStatus.nothingFetched));
            throw new NoHttpResponseException(SYNC_PLANS_URL + " did not return any data");
        }
        if (returnCount) {
            totalRecords = resp.getTotalRecords();
        }
        return resp.payload().toString();
    }

    private long getPlanDefinitionMaxServerVersion(List<PlanDefinition> planDefinitions, long maxServerVersion) {
        for (PlanDefinition planDefinition : planDefinitions) {
            long serverVersion = planDefinition.getServerVersion();
            if (serverVersion > maxServerVersion) {
                maxServerVersion = serverVersion;
            }
        }
        return maxServerVersion;
    }

    private HTTPAgent getHttpAgent() {
        return CoreLibrary.getInstance().context().getHttpAgent();
    }
}
