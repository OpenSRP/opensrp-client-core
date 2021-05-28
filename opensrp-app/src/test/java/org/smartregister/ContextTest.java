package org.smartregister;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.repository.AllAlerts;
import org.smartregister.repository.AllReports;
import org.smartregister.repository.AllServicesProvided;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.AllTimelineEvents;
import org.smartregister.repository.CampaignRepository;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.repository.ImageRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.LocationTagRepository;
import org.smartregister.repository.ManifestRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.service.ANMService;
import org.smartregister.service.ActionService;
import org.smartregister.service.AlertService;
import org.smartregister.service.AllFormVersionSyncService;
import org.smartregister.service.BeneficiaryService;
import org.smartregister.service.ChildService;
import org.smartregister.service.DrishtiService;
import org.smartregister.service.EligibleCoupleService;
import org.smartregister.service.FormSubmissionService;
import org.smartregister.service.FormSubmissionSyncService;
import org.smartregister.service.HTTPAgent;
import org.smartregister.service.MotherService;
import org.smartregister.service.PendingFormSubmissionService;
import org.smartregister.service.ServiceProvidedService;
import org.smartregister.service.UserService;
import org.smartregister.service.ZiggyFileLoader;
import org.smartregister.service.ZiggyService;
import org.smartregister.service.formsubmissionhandler.FormSubmissionRouter;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.ANMLocationController;

import java.util.ArrayList;

public class ContextTest extends BaseUnitTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        context = Context.getInstance();
    }

    @Test
    public void testUserService() {
        UserService userService = context.userService();
        Assert.assertNotNull(userService);
    }

    @Test
    public void testImageRepository() {
        ImageRepository imageRepository = context.imageRepository();
        Assert.assertNotNull(imageRepository);
    }

    @Test
    public void testAllAlerts() {
        AllAlerts allAlerts = context.allAlerts();
        Assert.assertNotNull(allAlerts);
    }

    @Test
    public void testHttpAgent() {
        HTTPAgent httpAgent = context.httpAgent();
        Assert.assertNotNull(httpAgent);
    }

    @Test
    public void testAnmService() {
        ANMService anmService = context.anmService();
        Assert.assertNotNull(anmService);
    }

    @Test
    public void testConfiguration() {
        DristhiConfiguration dristhiConfiguration = context.configuration();
        Assert.assertNotNull(dristhiConfiguration);
    }

    @Test
    public void testAnmController() {
        ANMController anmController = context.anmController();
        Assert.assertNotNull(anmController);
    }

    @Test
    public void testAnmLocationController() {
        ANMLocationController anmLocationController = context.anmLocationController();
        Assert.assertNotNull(anmLocationController);
    }

    @Test
    public void testGetHttpAgent() {
        HTTPAgent httpAgent = context.getHttpAgent();
        Assert.assertNotNull(httpAgent);
    }

    @Test
    public void testGetEventClientRepository() {
        EventClientRepository eventClientRepository = context.getEventClientRepository();
        Assert.assertNotNull(eventClientRepository);
    }

    @Test
    public void testGetUniqueIdRepository() {
        UniqueIdRepository uniqueIdRepository = context.getUniqueIdRepository();
        Assert.assertNotNull(uniqueIdRepository);
    }

    @Test
    public void testGetCampaignRepository() {
        CampaignRepository campaignRepository = context.getCampaignRepository();
        Assert.assertNotNull(campaignRepository);
    }

    @Test
    public void testgetTaskRepository() {
        TaskRepository taskRepository = context.getTaskRepository();
        Assert.assertNotNull(taskRepository);
    }

    @Test
    public void testBeneficiaryService() {
        BeneficiaryService beneficiaryService = context.beneficiaryService();
        Assert.assertNotNull(beneficiaryService);
    }

    @Test
    public void testDrishtiService() {
        DrishtiService drishtiService = context.drishtiService();
        Assert.assertNotNull(drishtiService);
    }

    @Test
    public void testActionService() {
        ActionService actionService = context.actionService();
        Assert.assertNotNull(actionService);
    }

    @Test
    public void testFormSubmissionService() {
        context.assignbindtypes();
        FormSubmissionService formSubmissionService = context.formSubmissionService();
        Assert.assertNotNull(formSubmissionService);
    }

    @Test
    public void testAllFormVersionSyncService() {
        AllFormVersionSyncService allFormVersionSyncService = context.allFormVersionSyncService();
        Assert.assertNotNull(allFormVersionSyncService);
    }

    @Test
    public void testFormSubmissionRouter() {
        FormSubmissionRouter formSubmissionRouter = context.formSubmissionRouter();
        Assert.assertNotNull(formSubmissionRouter);
    }

    @Test
    public void testZiggyService() {
        context.assignbindtypes();
        ZiggyService ziggyService = context.ziggyService();
        Assert.assertNotNull(ziggyService);
    }

    @Test
    public void testZiggyFileLoader() {
        ZiggyFileLoader ziggyFileLoader = context.ziggyFileLoader();
        Assert.assertNotNull(ziggyFileLoader);
    }

    @Test
    public void testFormSubmissionSyncService() {
        FormSubmissionSyncService formSubmissionSyncService = context.formSubmissionSyncService();
        Assert.assertNotNull(formSubmissionSyncService);
    }

    @Test
    public void testSharedRepositories() {
        ArrayList<DrishtiRepository> sharedRepositories = context.sharedRepositories();
        Assert.assertEquals(sharedRepositories.size(), 12);
    }

    @Test
    public void testSharedRepositoriesArray() {
        DrishtiRepository[] sharedRepositoriesArray = context.sharedRepositoriesArray();
        Assert.assertEquals(sharedRepositoriesArray.length, 12);
    }

    @Test
    public void testAllTimelineEvents() {
        AllTimelineEvents allTimelineEvents = context.allTimelineEvents();
        Assert.assertNotNull(allTimelineEvents);
    }

    @Test
    public void testAllReports() {
        AllReports allReports = context.allReports();
        Assert.assertNotNull(allReports);
    }

    @Test
    public void testAllServicesProvided() {
        AllServicesProvided allServicesProvided = context.allServicesProvided();
        Assert.assertNotNull(allServicesProvided);
    }

    @Test
    public void testDetailsRepository() {
        DetailsRepository detailsRepository = context.detailsRepository();
        Assert.assertNotNull(detailsRepository);
    }

    @Test
    public void testFormDataRepository() {
        context.assignbindtypes();
        FormDataRepository formDataRepository = context.formDataRepository();
        Assert.assertNotNull(formDataRepository);
    }

    @Test
    public void testAlertService() {
        AlertService alertService = context.alertService();
        Assert.assertNotNull(alertService);
    }

    @Test
    public void testServiceProvidedService() {
        ServiceProvidedService serviceProvidedService = context.serviceProvidedService();
        Assert.assertNotNull(serviceProvidedService);
    }

    @Test
    public void testEligibleCoupleService() {
        EligibleCoupleService eligibleCoupleService = context.eligibleCoupleService();
        Assert.assertNotNull(eligibleCoupleService);
    }

    @Test
    public void testmotherService() {
        MotherService motherService = context.motherService();
        Assert.assertNotNull(motherService);
    }

    @Test
    public void testChildService() {
        ChildService childService = context.childService();
        Assert.assertNotNull(childService);
    }

    @Test
    public void testPendingFormSubmissionService() {
        context.assignbindtypes();
        PendingFormSubmissionService pendingFormSubmissionService = context.pendingFormSubmissionService();
        Assert.assertNotNull(pendingFormSubmissionService);
    }

    @Test
    public void testLocationRepository() {
        LocationRepository locationRepository = context.getLocationRepository();
        Assert.assertNotNull(locationRepository);
    }

    @Test
    public void testGetLocationTagRepository() {
        LocationTagRepository locationTagRepository = context.getLocationTagRepository();
        Assert.assertNotNull(locationTagRepository);
    }

    @Test
    public void testGetStructureRepository() {
        StructureRepository structureRepository = context.getStructureRepository();
        Assert.assertNotNull(structureRepository);
    }

    @Test
    public void testGetPlanDefinitionRepository() {
        PlanDefinitionRepository planDefinitionRepository = context.getPlanDefinitionRepository();
        Assert.assertNotNull(planDefinitionRepository);
    }

    @Test
    public void testGetManifestRepository() {
        ManifestRepository manifestRepository = context.getManifestRepository();
        Assert.assertNotNull(manifestRepository);
    }

    @Test
    public void testGetClientFormRepository() {
        ClientFormRepository clientFormRepository = context.getClientFormRepository();
        Assert.assertNotNull(clientFormRepository);
    }

    @Test
    public void testAllSharedPreferences() {
        AllSharedPreferences allSharedPreferences = context.allSharedPreferences();
        Assert.assertNotNull(allSharedPreferences);
    }
}