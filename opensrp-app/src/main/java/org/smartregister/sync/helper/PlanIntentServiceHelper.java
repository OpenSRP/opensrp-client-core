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

import java.text.MessageFormat;
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
    protected static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .disableHtmlEscaping()
            .create();

    protected final Context context;
    protected static PlanIntentServiceHelper instance;

    public static final String SYNC_PLANS_URL = "/rest/plans/sync";
    public static final String PLAN_LAST_SYNC_DATE = "plan_last_sync_date";
    private long totalRecords;
    private SyncProgress syncProgress;

    private Trace planSyncTrace;

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

            addAttribute(planSyncTrace, COUNT, String.valueOf(plans.size()));
            stopTrace(planSyncTrace);
            for (PlanDefinition plan : plans) {
                try {
                    planDefinitionRepository.addOrUpdate(plan);
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
