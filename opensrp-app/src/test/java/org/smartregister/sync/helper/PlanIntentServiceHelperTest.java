package org.smartregister.sync.helper;

import com.google.gson.reflect.TypeToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.SyncEntity;
import org.smartregister.domain.SyncProgress;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.service.HTTPAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.smartregister.CoreLibrary.getInstance;

/**
 * Created by Richard Kareko on 7/17/20.
 */

public class PlanIntentServiceHelperTest extends BaseRobolectricUnitTest {

    @Mock
    private PlanDefinitionRepository planDefinitionRepository;

    @Mock
    private HTTPAgent httpAgent;

    @Captor
    private ArgumentCaptor<PlanDefinition> planDefinitionArgumentCaptor;

    @Captor
    private ArgumentCaptor<SyncProgress> syncProgressArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private PlanIntentServiceHelper planIntentServiceHelper;

    private String planJson = "{\"action\":[{\"code\":\"IRS\",\"description\":\"Visit each structure in the operational area and attempt to spray\",\"goalId\":\"IRS\",\"identifier\":\"29a49ef8-2d04-520b-9c59-56065c06213b\",\"prefix\":1,\"reason\":\"Routine\",\"subjectCodableConcept\":{\"text\":\"Residential_Structure\"},\"taskTemplate\":\"Spray_Structures\",\"timingPeriod\":{\"end\":\"2019-11-12\",\"start\":\"2019-11-05\"},\"title\":\"Spray Structures\"}],\"date\":\"2019-11-05\",\"effectivePeriod\":{\"end\":\"2020-12-31\",\"start\":\"2019-11-05\"},\"experimental\":false,\"goal\":[{\"description\":\"Spray structures in the operational area\",\"id\":\"IRS\",\"priority\":\"medium-priority\",\"target\":[{\"detail\":{\"detailQuantity\":{\"comparator\":\">=\",\"unit\":\"Percent\",\"value\":90.0}},\"due\":\"2019-11-12\",\"measure\":\"Percent of structures sprayed\"}]}],\"identifier\":\"e2d32fdd-ac00-536a-8f80-61755b1f6799\",\"jurisdiction\":[],\"name\":\"IRS-2019-11-05\",\"serverVersion\":1587534318465,\"status\":\"active\",\"title\":\"IRS 2019-11-05 Ona Company Retreat Demo\",\"useContext\":[{\"code\":\"interventionType\",\"valueCodableConcept\":\"IRS\"},{\"code\":\"taskGenerationStatus\",\"valueCodableConcept\":\"False\"}],\"version\":\"2\"}";

    @Before
    public void setUp() {
        Whitebox.setInternalState(PlanIntentServiceHelper.class, "instance", (PlanIntentServiceHelper) null);
        planIntentServiceHelper = PlanIntentServiceHelper.getInstance();
        Whitebox.setInternalState(planIntentServiceHelper, "planDefinitionRepository", planDefinitionRepository);
        CoreLibrary.getInstance().context().allSharedPreferences().getPreferences().edit().clear().apply();
        CoreLibrary.getInstance().context().allSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, "https://sample-stage.smartregister.org/opensrp");
        CoreLibrary.getInstance().context().allSharedPreferences().savePreference(AllConstants.ORGANIZATION_IDS, "org1,org2");
        Whitebox.setInternalState(getInstance().context(), "httpAgent", httpAgent);
    }

    @After
    public void tearDown() {
        initCoreLibrary();
    }

    @Test
    public void testSyncPlansCallsSendSyncProgressDialog() {
        planIntentServiceHelper = spy(planIntentServiceHelper);
        Whitebox.setInternalState(planIntentServiceHelper, "totalRecords", 10l);
        planIntentServiceHelper.syncPlans();
        verify(planIntentServiceHelper).sendSyncProgressBroadcast(syncProgressArgumentCaptor.capture(), any());
        assertEquals(SyncEntity.PLANS, syncProgressArgumentCaptor.getValue().getSyncEntity());
        assertEquals(10l, syncProgressArgumentCaptor.getValue().getTotalRecords());
        assertEquals(0, syncProgressArgumentCaptor.getValue().getPercentageSynced());
    }

    @Test
    public void testBatchFetchPlansFromServer() {

        PlanDefinition expectedPlan = PlanIntentServiceHelper.gson.fromJson(planJson, new TypeToken<PlanDefinition>() {
        }.getType());
        List<PlanDefinition> plans = Collections.singletonList(expectedPlan);

        Mockito.doReturn(new Response<>(ResponseStatus.success,    // returned on first call
                        PlanIntentServiceHelper.gson.toJson(plans)).withTotalRecords(1L),
                new Response<>(ResponseStatus.success,             //returned on second call
                        PlanIntentServiceHelper.gson.toJson(new ArrayList<>())).withTotalRecords(0l))
                .when(httpAgent).post(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        planIntentServiceHelper.syncPlans();

        String syncUrl = stringArgumentCaptor.getAllValues().get(0);
        assertEquals("https://sample-stage.smartregister.org/opensrp/rest/plans/sync", syncUrl);
        String requestString = stringArgumentCaptor.getAllValues().get(1);
        assertEquals("{\"organizations\":[\"org1\",\"org2\"],\"serverVersion\":0,\"return_count\":true}", requestString);

        verify(planDefinitionRepository).addOrUpdate(planDefinitionArgumentCaptor.capture());
        PlanDefinition actualPlan = planDefinitionArgumentCaptor.getValue();
        assertEquals(expectedPlan.getIdentifier(), actualPlan.getIdentifier());
        assertEquals(expectedPlan.getName(), actualPlan.getName());
        assertEquals(expectedPlan.getTitle(), actualPlan.getTitle());
        assertEquals(expectedPlan.getServerVersion(), actualPlan.getServerVersion());
        assertEquals(expectedPlan.getVersion(), actualPlan.getVersion());

    }
}
