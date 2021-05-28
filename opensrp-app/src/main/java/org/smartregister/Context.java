package org.smartregister;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonPersonObjectClients;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.commonregistry.CommonRepositoryInformationHolder;
import org.smartregister.domain.ColumnDetails;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.AllAlerts;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.AllReports;
import org.smartregister.repository.AllServicesProvided;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.AllTimelineEvents;
import org.smartregister.repository.CampaignRepository;
import org.smartregister.repository.ChildRepository;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.ClientRelationshipRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.EligibleCoupleRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.repository.FormsVersionRepository;
import org.smartregister.repository.ImageRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.LocationTagRepository;
import org.smartregister.repository.ManifestRepository;
import org.smartregister.repository.MotherRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.ReportRepository;
import org.smartregister.repository.ServiceProvidedRepository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.repository.TimelineEventRepository;
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
import org.smartregister.service.formsubmissionhandler.ANCCloseHandler;
import org.smartregister.service.formsubmissionhandler.ANCInvestigationsHandler;
import org.smartregister.service.formsubmissionhandler.ANCRegistrationHandler;
import org.smartregister.service.formsubmissionhandler.ANCRegistrationOAHandler;
import org.smartregister.service.formsubmissionhandler.ANCVisitHandler;
import org.smartregister.service.formsubmissionhandler.ChildCloseHandler;
import org.smartregister.service.formsubmissionhandler.ChildIllnessHandler;
import org.smartregister.service.formsubmissionhandler.ChildImmunizationsHandler;
import org.smartregister.service.formsubmissionhandler.ChildRegistrationECHandler;
import org.smartregister.service.formsubmissionhandler.ChildRegistrationOAHandler;
import org.smartregister.service.formsubmissionhandler.DeliveryOutcomeHandler;
import org.smartregister.service.formsubmissionhandler.DeliveryPlanHandler;
import org.smartregister.service.formsubmissionhandler.ECCloseHandler;
import org.smartregister.service.formsubmissionhandler.ECEditHandler;
import org.smartregister.service.formsubmissionhandler.ECRegistrationHandler;
import org.smartregister.service.formsubmissionhandler.FPChangeHandler;
import org.smartregister.service.formsubmissionhandler.FPComplicationsHandler;
import org.smartregister.service.formsubmissionhandler.FormSubmissionRouter;
import org.smartregister.service.formsubmissionhandler.HBTestHandler;
import org.smartregister.service.formsubmissionhandler.IFAHandler;
import org.smartregister.service.formsubmissionhandler.PNCCloseHandler;
import org.smartregister.service.formsubmissionhandler.PNCRegistrationOAHandler;
import org.smartregister.service.formsubmissionhandler.PNCVisitHandler;
import org.smartregister.service.formsubmissionhandler.RenewFPProductHandler;
import org.smartregister.service.formsubmissionhandler.TTHandler;
import org.smartregister.service.formsubmissionhandler.VitaminAHandler;
import org.smartregister.sync.SaveANMLocationTask;
import org.smartregister.sync.SaveANMTeamTask;
import org.smartregister.sync.SaveUserInfoTask;
import org.smartregister.util.AppProperties;
import org.smartregister.util.Cache;
import org.smartregister.util.Session;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.ANCClients;
import org.smartregister.view.contract.ECClients;
import org.smartregister.view.contract.FPClients;
import org.smartregister.view.contract.HomeContext;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.contract.Villages;
import org.smartregister.view.contract.pnc.PNCClients;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.ANMLocationController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class Context {

    ///////////////////common bindtypes///////////////

    public static ArrayList<CommonRepositoryInformationHolder> bindtypes;
    private static Context context = new Context();
    protected DristhiConfiguration configuration;
    private android.content.Context applicationContext;
    private EligibleCoupleRepository eligibleCoupleRepository;
    private AlertRepository alertRepository;
    private SettingsRepository settingsRepository;
    private ChildRepository childRepository;
    private DetailsRepository detailsRepository;
    private MotherRepository motherRepository;
    private TimelineEventRepository timelineEventRepository;
    private ReportRepository reportRepository;
    private FormDataRepository formDataRepository;
    private ServiceProvidedRepository serviceProvidedRepository;
    private FormsVersionRepository formsVersionRepository;
    private AllSettings allSettings;
    private AllSharedPreferences allSharedPreferences;
    private AllAlerts allAlerts;
    private AllEligibleCouples allEligibleCouples;
    private AllBeneficiaries allBeneficiaries;
    private AllTimelineEvents allTimelineEvents;
    private AllReports allReports;
    private AllServicesProvided allServicesProvided;
    private AllCommonsRepository allCommonPersonObjectsRepository;
    private ImageRepository imageRepository;
    private DrishtiService drishtiService;
    private ActionService actionService;
    private FormSubmissionService formSubmissionService;
    private FormSubmissionSyncService formSubmissionSyncService;
    private ZiggyService ziggyService;
    private UserService userService;
    private AlertService alertService;
    private EligibleCoupleService eligibleCoupleService;
    private MotherService motherService;
    private ChildService childService;
    private ANMService anmService;
    private BeneficiaryService beneficiaryService;
    private ServiceProvidedService serviceProvidedService;
    private PendingFormSubmissionService pendingFormSubmissionService;
    private AllFormVersionSyncService allFormVersionSyncService;
    private Session session;
    private Cache<String> listCache;
    private Cache<SmartRegisterClients> smartRegisterClientsCache;
    private Cache<HomeContext> homeContextCache;
    private Cache<ECClients> ecClientsCache;
    private Cache<FPClients> fpClientsCache;
    private Cache<ANCClients> ancClientsCache;
    private Cache<PNCClients> pncClientsCache;
    private Cache<Villages> villagesCache;
    private Cache<Typeface> typefaceCache;
    private Cache<CommonPersonObjectClients> personObjectClientsCache;
    private HTTPAgent httpAgent;
    private ZiggyFileLoader ziggyFileLoader;
    private FormSubmissionRouter formSubmissionRouter;
    private ECRegistrationHandler ecRegistrationHandler;
    private FPComplicationsHandler fpComplicationsHandler;
    private FPChangeHandler fpChangeHandler;
    private RenewFPProductHandler renewFPProductHandler;
    private ECCloseHandler ecCloseHandler;
    private ANCRegistrationHandler ancRegistrationHandler;
    private ANCRegistrationOAHandler ancRegistrationOAHandler;
    private ANCVisitHandler ancVisitHandler;
    private ANCCloseHandler ancCloseHandler;
    private TTHandler ttHandler;
    private IFAHandler ifaHandler;
    private HBTestHandler hbTestHandler;
    private DeliveryOutcomeHandler deliveryOutcomeHandler;
    private DeliveryPlanHandler deliveryPlanHandler;
    private PNCRegistrationOAHandler pncRegistrationOAHandler;
    private PNCCloseHandler pncCloseHandler;
    private PNCVisitHandler pncVisitHandler;
    private ChildImmunizationsHandler childImmunizationsHandler;
    private ChildRegistrationECHandler childRegistrationECHandler;
    private ChildRegistrationOAHandler childRegistrationOAHandler;
    private ChildCloseHandler childCloseHandler;
    private ChildIllnessHandler childIllnessHandler;
    private VitaminAHandler vitaminAHandler;
    private ECEditHandler ecEditHandler;
    private ANCInvestigationsHandler ancInvestigationsHandler;
    private SaveANMLocationTask saveANMLocationTask;
    private SaveUserInfoTask saveUserInfoTask;
    private SaveANMTeamTask saveANMTeamTask;
    private ANMController anmController;
    private ANMLocationController anmLocationController;
    private CommonFtsObject commonFtsObject;
    private Map<String, String> customHumanReadableConceptResponse;
    private HashMap<String, CommonRepository> MapOfCommonRepository;
    private EventClientRepository eventClientRepository;
    private EventClientRepository foreignEventClientRepository;
    private UniqueIdRepository uniqueIdRepository;
    private CampaignRepository campaignRepository;
    private TaskRepository taskRepository;
    private TaskNotesRepository taskNotesRepository;
    private LocationRepository locationRepository;
    private StructureRepository structureRepository;
    private PlanDefinitionRepository planDefinitionRepository;
    private AppProperties appProperties;
    private LocationTagRepository locationTagRepository;
    private ManifestRepository manifestRepository;
    private ClientFormRepository clientFormRepository;
    private ClientRelationshipRepository clientRelationshipRepository;

    private static final String SHARED_PREFERENCES_FILENAME = "%s_preferences";

    /////////////////////////////////////////////////

    protected Context() {
    }

    public static Context getInstance() {
        if (context == null) {
            context = new Context();
        }
        return context;
    }

    public static Context setInstance(Context mContext) {
        if (mContext != null) {
            context = mContext;
            return context;
        }
        return null;
    }

    public static void destroyInstance() {
        context = null;
    }

    public android.content.Context applicationContext() {
        if (applicationContext != null) {//Fix to enable Multi language support for this context
            try {
                Configuration configuration = applicationContext.getResources().getConfiguration();
                configuration.setLocale(configuration.locale);
                return applicationContext.createConfigurationContext(configuration);
            } catch (Exception e) {
                return applicationContext;
            }
        } else {
            return null;
        }
    }

    public BeneficiaryService beneficiaryService() {
        if (beneficiaryService == null) {
            beneficiaryService = new BeneficiaryService(allEligibleCouples(), allBeneficiaries());
        }
        return beneficiaryService;
    }

    public Context updateApplicationContext(android.content.Context applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    protected DrishtiService drishtiService() {
        if (drishtiService == null) {
            drishtiService = new DrishtiService(httpAgent(), configuration().dristhiBaseURL());
        }
        return drishtiService;
    }

    public ActionService actionService() {
        if (actionService == null) {
            actionService = new ActionService(drishtiService(), allSettings(),
                    allSharedPreferences(), allReports());
        }
        return actionService;
    }

    public FormSubmissionService formSubmissionService() {
        if (formSubmissionService == null) {
            if (commonFtsObject != null) {
                formSubmissionService = new FormSubmissionService(ziggyService(),
                        formDataRepository(), allSettings(), allCommonsRepositoryMap());
            } else {
                formSubmissionService = new FormSubmissionService(ziggyService(),
                        formDataRepository(), allSettings());
            }
        }
        return formSubmissionService;
    }

    public AllFormVersionSyncService allFormVersionSyncService() {
        if (allFormVersionSyncService == null) {
            allFormVersionSyncService = new AllFormVersionSyncService(httpAgent(), configuration(),
                    formsVersionRepository());
        }
        return allFormVersionSyncService;
    }

    public FormSubmissionRouter formSubmissionRouter() {
        if (formSubmissionRouter == null) {
            formSubmissionRouter = new FormSubmissionRouter(formDataRepository(),
                    ecRegistrationHandler(), fpComplicationsHandler(), fpChangeHandler(),
                    renewFPProductHandler(), ecCloseHandler(), ancRegistrationHandler(),
                    ancRegistrationOAHandler(), ancVisitHandler(), ancCloseHandler(), ttHandler(),
                    ifaHandler(), hbTestHandler(), deliveryOutcomeHandler(),
                    pncRegistrationOAHandler(), pncCloseHandler(), pncVisitHandler(),
                    childImmunizationsHandler(), childRegistrationECHandler(),
                    childRegistrationOAHandler(), childCloseHandler(), childIllnessHandler(),
                    vitaminAHandler(), deliveryPlanHandler(), ecEditHandler(),
                    ancInvestigationsHandler());
        }
        return formSubmissionRouter;
    }

    private ChildCloseHandler childCloseHandler() {
        if (childCloseHandler == null) {
            childCloseHandler = new ChildCloseHandler(childService());
        }
        return childCloseHandler;
    }

    private ECRegistrationHandler ecRegistrationHandler() {
        if (ecRegistrationHandler == null) {
            ecRegistrationHandler = new ECRegistrationHandler(eligibleCoupleService());
        }
        return ecRegistrationHandler;
    }

    private FPComplicationsHandler fpComplicationsHandler() {
        if (fpComplicationsHandler == null) {
            fpComplicationsHandler = new FPComplicationsHandler(eligibleCoupleService());
        }
        return fpComplicationsHandler;
    }

    private FPChangeHandler fpChangeHandler() {
        if (fpChangeHandler == null) {
            fpChangeHandler = new FPChangeHandler(eligibleCoupleService());
        }
        return fpChangeHandler;
    }

    private RenewFPProductHandler renewFPProductHandler() {
        if (renewFPProductHandler == null) {
            renewFPProductHandler = new RenewFPProductHandler(eligibleCoupleService());
        }
        return renewFPProductHandler;
    }

    private ECCloseHandler ecCloseHandler() {
        if (ecCloseHandler == null) {
            ecCloseHandler = new ECCloseHandler(eligibleCoupleService());
        }
        return ecCloseHandler;
    }

    private ANCRegistrationHandler ancRegistrationHandler() {
        if (ancRegistrationHandler == null) {
            ancRegistrationHandler = new ANCRegistrationHandler(motherService());
        }
        return ancRegistrationHandler;
    }

    private ANCRegistrationOAHandler ancRegistrationOAHandler() {
        if (ancRegistrationOAHandler == null) {
            ancRegistrationOAHandler = new ANCRegistrationOAHandler(motherService());
        }
        return ancRegistrationOAHandler;
    }

    private ANCVisitHandler ancVisitHandler() {
        if (ancVisitHandler == null) {
            ancVisitHandler = new ANCVisitHandler(motherService());
        }
        return ancVisitHandler;
    }

    private ANCCloseHandler ancCloseHandler() {
        if (ancCloseHandler == null) {
            ancCloseHandler = new ANCCloseHandler(motherService());
        }
        return ancCloseHandler;
    }

    private TTHandler ttHandler() {
        if (ttHandler == null) {
            ttHandler = new TTHandler(motherService());
        }
        return ttHandler;
    }

    private IFAHandler ifaHandler() {
        if (ifaHandler == null) {
            ifaHandler = new IFAHandler(motherService());
        }
        return ifaHandler;
    }

    private HBTestHandler hbTestHandler() {
        if (hbTestHandler == null) {
            hbTestHandler = new HBTestHandler(motherService());
        }
        return hbTestHandler;
    }

    private DeliveryOutcomeHandler deliveryOutcomeHandler() {
        if (deliveryOutcomeHandler == null) {
            deliveryOutcomeHandler = new DeliveryOutcomeHandler(motherService(), childService());
        }
        return deliveryOutcomeHandler;
    }

    private DeliveryPlanHandler deliveryPlanHandler() {
        if (deliveryPlanHandler == null) {
            deliveryPlanHandler = new DeliveryPlanHandler(motherService());
        }
        return deliveryPlanHandler;
    }

    private PNCRegistrationOAHandler pncRegistrationOAHandler() {
        if (pncRegistrationOAHandler == null) {
            pncRegistrationOAHandler = new PNCRegistrationOAHandler(childService());
        }
        return pncRegistrationOAHandler;
    }

    private PNCCloseHandler pncCloseHandler() {
        if (pncCloseHandler == null) {
            pncCloseHandler = new PNCCloseHandler(motherService());
        }
        return pncCloseHandler;
    }

    private PNCVisitHandler pncVisitHandler() {
        if (pncVisitHandler == null) {
            pncVisitHandler = new PNCVisitHandler(motherService(), childService());
        }
        return pncVisitHandler;
    }

    private ChildImmunizationsHandler childImmunizationsHandler() {
        if (childImmunizationsHandler == null) {
            childImmunizationsHandler = new ChildImmunizationsHandler(childService());
        }
        return childImmunizationsHandler;
    }

    private ChildIllnessHandler childIllnessHandler() {
        if (childIllnessHandler == null) {
            childIllnessHandler = new ChildIllnessHandler(childService());
        }
        return childIllnessHandler;
    }

    private VitaminAHandler vitaminAHandler() {
        if (vitaminAHandler == null) {
            vitaminAHandler = new VitaminAHandler(childService());
        }
        return vitaminAHandler;
    }

    private ChildRegistrationECHandler childRegistrationECHandler() {
        if (childRegistrationECHandler == null) {
            childRegistrationECHandler = new ChildRegistrationECHandler(childService());
        }
        return childRegistrationECHandler;
    }

    private ChildRegistrationOAHandler childRegistrationOAHandler() {
        if (childRegistrationOAHandler == null) {
            childRegistrationOAHandler = new ChildRegistrationOAHandler(childService());
        }
        return childRegistrationOAHandler;
    }

    private ECEditHandler ecEditHandler() {
        if (ecEditHandler == null) {
            ecEditHandler = new ECEditHandler();
        }
        return ecEditHandler;
    }

    private ANCInvestigationsHandler ancInvestigationsHandler() {
        if (ancInvestigationsHandler == null) {
            ancInvestigationsHandler = new ANCInvestigationsHandler();
        }
        return ancInvestigationsHandler;
    }

    public ZiggyService ziggyService() {
        if (ziggyService == null) {
            ziggyService = new ZiggyService(ziggyFileLoader(), formDataRepository(),
                    formSubmissionRouter());
        }
        return ziggyService;
    }

    public ZiggyFileLoader ziggyFileLoader() {
        if (ziggyFileLoader == null) {
            ziggyFileLoader = new ZiggyFileLoader("www/ziggy", "www/form",
                    applicationContext().getAssets());
        }
        return ziggyFileLoader;
    }

    public FormSubmissionSyncService formSubmissionSyncService() {
        if (formSubmissionSyncService == null) {
            formSubmissionSyncService = new FormSubmissionSyncService(formSubmissionService(),
                    httpAgent(), formDataRepository(), allSettings(), allSharedPreferences(),
                    configuration());
        }
        return formSubmissionSyncService;
    }

    public HTTPAgent httpAgent() {
        if (httpAgent == null) {
            httpAgent = new HTTPAgent(applicationContext, allSharedPreferences(), configuration());
        }
        return httpAgent;
    }

    public ArrayList<DrishtiRepository> sharedRepositories() {
        assignbindtypes();
        ArrayList<DrishtiRepository> drishtiRepositoryList = new ArrayList<DrishtiRepository>();
        drishtiRepositoryList.add(settingsRepository());
        drishtiRepositoryList.add(alertRepository());
        drishtiRepositoryList.add(eligibleCoupleRepository());
        drishtiRepositoryList.add(childRepository());
        drishtiRepositoryList.add(timelineEventRepository());
        drishtiRepositoryList.add(motherRepository());
        drishtiRepositoryList.add(reportRepository());
        drishtiRepositoryList.add(formDataRepository());
        drishtiRepositoryList.add(serviceProvidedRepository());
        drishtiRepositoryList.add(formsVersionRepository());
        drishtiRepositoryList.add(imageRepository());
        drishtiRepositoryList.add(detailsRepository());
        for (int i = 0; i < bindtypes.size(); i++) {
            drishtiRepositoryList.add(commonrepository(bindtypes.get(i).getBindtypename()));
        }
        return drishtiRepositoryList;

    }

    public DrishtiRepository[] sharedRepositoriesArray() {
        ArrayList<DrishtiRepository> drishtiRepositories = sharedRepositories();
        DrishtiRepository[] drishtiRepositoryArray = drishtiRepositories
                .toArray(new DrishtiRepository[drishtiRepositories.size()]);
        return drishtiRepositoryArray;
    }

    public AllEligibleCouples allEligibleCouples() {
        if (allEligibleCouples == null) {
            allEligibleCouples = new AllEligibleCouples(eligibleCoupleRepository(),
                    alertRepository(), timelineEventRepository());
        }
        return allEligibleCouples;
    }

    public AllAlerts allAlerts() {
        if (allAlerts == null) {
            allAlerts = new AllAlerts(alertRepository());
        }
        return allAlerts;
    }

    public AllSettings allSettings() {
        if (allSettings == null) {
            allSettings = new AllSettings(allSharedPreferences(), settingsRepository());
        }
        return allSettings;
    }

    public AllSharedPreferences allSharedPreferences() {
        if (allSharedPreferences == null) {
            allSharedPreferences = new AllSharedPreferences(createSharedPreferences(this.applicationContext));
        }
        return allSharedPreferences;
    }

    private SharedPreferences createSharedPreferences(android.content.Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Utils.getBooleanProperty(AllConstants.PROPERTY.ENCRYPT_SHARED_PREFERENCES)) {

            return createEncryptedSharedPreferences(context);
        } else {
            return getDefaultSharedPreferences(context);
        }
    }

    private SharedPreferences createEncryptedSharedPreferences(android.content.Context context) {
        SharedPreferences sharedPreferences = null;

        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            sharedPreferences = EncryptedSharedPreferences.create(
                    String.format(SHARED_PREFERENCES_FILENAME, context.getPackageName()),
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Timber.e(e, "Error creating encrypted SharedPreferences");

            // fall back to unencrypted SharedPreferences
            sharedPreferences = getDefaultSharedPreferences(context);
        }

        return sharedPreferences;
    }

    public AllBeneficiaries allBeneficiaries() {
        if (allBeneficiaries == null) {
            allBeneficiaries = new AllBeneficiaries(motherRepository(), childRepository(),
                    alertRepository(), timelineEventRepository());
        }
        return allBeneficiaries;
    }

    public AllTimelineEvents allTimelineEvents() {
        if (allTimelineEvents == null) {
            allTimelineEvents = new AllTimelineEvents(timelineEventRepository());
        }
        return allTimelineEvents;
    }

    public AllReports allReports() {
        if (allReports == null) {
            allReports = new AllReports(reportRepository());
        }
        return allReports;
    }

    public AllServicesProvided allServicesProvided() {
        if (allServicesProvided == null) {
            allServicesProvided = new AllServicesProvided(serviceProvidedRepository());
        }
        return allServicesProvided;
    }

    private EligibleCoupleRepository eligibleCoupleRepository() {
        if (eligibleCoupleRepository == null) {
            eligibleCoupleRepository = new EligibleCoupleRepository();
        }
        return eligibleCoupleRepository;
    }

    protected AlertRepository alertRepository() {
        if (alertRepository == null) {
            alertRepository = new AlertRepository();
        }
        return alertRepository;
    }

    protected SettingsRepository settingsRepository() {
        if (settingsRepository == null) {
            settingsRepository = new SettingsRepository();
        }
        return settingsRepository;
    }

    private ChildRepository childRepository() {
        if (childRepository == null) {
            childRepository = new ChildRepository();
        }
        return childRepository;
    }

    public DetailsRepository detailsRepository() {
        if (detailsRepository == null) {
            detailsRepository = new DetailsRepository();
        }
        return detailsRepository;
    }

    private MotherRepository motherRepository() {
        if (motherRepository == null) {
            motherRepository = new MotherRepository();
        }
        return motherRepository;
    }

    protected TimelineEventRepository timelineEventRepository() {
        if (timelineEventRepository == null) {
            timelineEventRepository = new TimelineEventRepository();
        }
        return timelineEventRepository;
    }

    private ReportRepository reportRepository() {
        if (reportRepository == null) {
            reportRepository = new ReportRepository();
        }
        return reportRepository;
    }

    public FormDataRepository formDataRepository() {
        if (formDataRepository == null) {
            formDataRepository = new FormDataRepository();
        }
        return formDataRepository;
    }

    protected ServiceProvidedRepository serviceProvidedRepository() {
        if (serviceProvidedRepository == null) {
            serviceProvidedRepository = new ServiceProvidedRepository();
        }
        return serviceProvidedRepository;
    }

    protected FormsVersionRepository formsVersionRepository() {
        if (formsVersionRepository == null) {
            formsVersionRepository = new FormsVersionRepository();
        }
        return formsVersionRepository;
    }

    public ImageRepository imageRepository() {
        if (imageRepository == null) {
            imageRepository = new ImageRepository();
        }
        return imageRepository;
    }

    public UserService userService() {
        if (userService == null) {
            userService = new UserService(allSettings(), allSharedPreferences(),
                    httpAgent(), session(), configuration(), saveANMLocationTask(),
                    saveUserInfoTask(), saveANMTeamTask());
        }
        return userService;
    }

    private SaveANMLocationTask saveANMLocationTask() {
        if (saveANMLocationTask == null) {
            saveANMLocationTask = new SaveANMLocationTask(allSettings());
        }
        return saveANMLocationTask;
    }

    private SaveUserInfoTask saveUserInfoTask() {
        if (saveUserInfoTask == null) {
            saveUserInfoTask = new SaveUserInfoTask(allSettings());
        }
        return saveUserInfoTask;
    }

    private SaveANMTeamTask saveANMTeamTask() {
        if (saveANMTeamTask == null) {
            saveANMTeamTask = new SaveANMTeamTask(allSettings());
        }
        return saveANMTeamTask;
    }

    public AlertService alertService() {
        if (alertService == null) {
            if (commonFtsObject() != null) {
                alertService = new AlertService(alertRepository(), commonFtsObject(),
                        allCommonsRepositoryMap());
            } else {
                alertService = new AlertService(alertRepository());
            }
        }
        return alertService;
    }

    public ServiceProvidedService serviceProvidedService() {
        if (serviceProvidedService == null) {
            serviceProvidedService = new ServiceProvidedService(allServicesProvided());
        }
        return serviceProvidedService;
    }

    public EligibleCoupleService eligibleCoupleService() {
        if (eligibleCoupleService == null) {
            eligibleCoupleService = new EligibleCoupleService(allEligibleCouples(),
                    allTimelineEvents(), allBeneficiaries());
        }
        return eligibleCoupleService;
    }

    public MotherService motherService() {
        if (motherService == null) {
            motherService = new MotherService(allBeneficiaries(), allEligibleCouples(),
                    allTimelineEvents(), serviceProvidedService());
        }
        return motherService;
    }

    public ChildService childService() {
        if (childService == null) {
            childService = new ChildService(allBeneficiaries(), motherRepository(),
                    childRepository(), allTimelineEvents(), serviceProvidedService(), allAlerts());
        }
        return childService;
    }

    public Session session() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    public ANMService anmService() {
        if (anmService == null) {
            anmService = new ANMService(allSharedPreferences(), allBeneficiaries(),
                    allEligibleCouples());
        }
        return anmService;
    }

    public Cache<String> listCache() {
        if (listCache == null) {
            listCache = new Cache<String>();
        }
        return listCache;
    }

    public Cache<SmartRegisterClients> smartRegisterClientsCache() {
        if (smartRegisterClientsCache == null) {
            smartRegisterClientsCache = new Cache<SmartRegisterClients>();
        }
        return smartRegisterClientsCache;
    }

    public Cache<HomeContext> homeContextCache() {
        if (homeContextCache == null) {
            homeContextCache = new Cache<HomeContext>();
        }
        return homeContextCache;
    }

    public Boolean IsUserLoggedOut() {
        return userService().hasSessionExpired();
    }

    public DristhiConfiguration configuration() {
        if (configuration == null) {
            configuration = new DristhiConfiguration();
        }
        return configuration;
    }

    public PendingFormSubmissionService pendingFormSubmissionService() {
        if (pendingFormSubmissionService == null) {
            pendingFormSubmissionService = new PendingFormSubmissionService(formDataRepository());
        }
        return pendingFormSubmissionService;
    }

    public ANMController anmController() {
        if (anmController == null) {
            anmController = new ANMController(anmService(), listCache(), homeContextCache());
        }
        return anmController;
    }

    public ANMLocationController anmLocationController() {
        if (anmLocationController == null) {
            anmLocationController = new ANMLocationController(allSettings(), listCache());
        }
        return anmLocationController;
    }

    //#TODO: Refactor to use one cache object
    public Cache<ECClients> ecClientsCache() {
        if (ecClientsCache == null) {
            ecClientsCache = new Cache<ECClients>();
        }
        return ecClientsCache;

    }

    //#TODO: Refactor to use one cache object

    //#TODO: Refactor to use one cache object
    public Cache<FPClients> fpClientsCache() {
        if (fpClientsCache == null) {
            fpClientsCache = new Cache<FPClients>();
        }
        return fpClientsCache;

    }

    public Cache<ANCClients> ancClientsCache() {
        if (ancClientsCache == null) {
            ancClientsCache = new Cache<ANCClients>();
        }
        return ancClientsCache;
    }

    public Cache<PNCClients> pncClientsCache() {
        if (pncClientsCache == null) {
            pncClientsCache = new Cache<PNCClients>();
        }
        return pncClientsCache;
    }

    public Cache<Villages> villagesCache() {
        if (villagesCache == null) {
            villagesCache = new Cache<Villages>();
        }
        return villagesCache;
    }

    public Cache<Typeface> typefaceCache() {
        if (typefaceCache == null) {
            typefaceCache = new Cache<Typeface>();
        }
        return typefaceCache;
    }

    public String getStringResource(int id) {
        return applicationContext().getResources().getString(id);
    }

    public int getColorResource(int id) {
        return applicationContext().getResources().getColor(id);
    }

    public Drawable getDrawable(int id) {
        return applicationContext().getResources().getDrawable(id);
    }

    public Drawable getDrawableResource(int id) {
        return applicationContext().getResources().getDrawable(id);
    }

    ///////////////////////////////// common methods ///////////////////////////////
    public Cache<CommonPersonObjectClients> personObjectClientsCache() {
        this.personObjectClientsCache = null;
        personObjectClientsCache = new Cache<CommonPersonObjectClients>();
        return personObjectClientsCache;
    }

    public AllCommonsRepository allCommonsRepositoryobjects(String tablename) {
        allCommonPersonObjectsRepository = new AllCommonsRepository(commonrepository(tablename),
                alertRepository(), timelineEventRepository());
        return allCommonPersonObjectsRepository;
    }

    public long countofcommonrepositroy(String tablename) {
        return commonrepository(tablename).count();
    }

    public CommonRepository commonrepository(String tablename) {
        if (MapOfCommonRepository == null) {
            MapOfCommonRepository = new HashMap<String, CommonRepository>();
        }
        if (MapOfCommonRepository.get(tablename) == null) {
            for (CommonRepositoryInformationHolder bindType : bindtypes) {
                if (bindType.getBindtypename().equalsIgnoreCase(tablename)) {
                    if (commonFtsObject != null && commonFtsObject.containsTable(tablename)) {
                        MapOfCommonRepository.put(bindType.getBindtypename(),
                                new CommonRepository(commonFtsObject, bindType.getBindtypename(),
                                        bindType.getColumnNames()));
                        break;
                    } else {
                        MapOfCommonRepository.put(bindType.getBindtypename(),
                                new CommonRepository(bindType.getBindtypename(),
                                        bindType.getColumnNames()));
                        break;
                    }
                }
            }

        }
        return MapOfCommonRepository.get(tablename);
    }

    public void assignbindtypes() {
        bindtypes = new ArrayList<CommonRepositoryInformationHolder>();
        // create common repository definition for the ec models
        getEcBindtypes();
        try {
            if (this.applicationContext() == null) {
                return;
            }
            String str = ReadFromfile("bindtypes.json", this.applicationContext());
            if (StringUtils.isBlank(str)) {
                return;
            }
            JSONObject jsonObject = new JSONObject(str);
            JSONArray bindtypeObjects = jsonObject.getJSONArray("bindobjects");

            for (int i = 0; i < bindtypeObjects.length(); i++) {
                String bindname = bindtypeObjects.getJSONObject(i).getString(AllConstants.ClientProcessing.NAME);
                ColumnDetails[] columNames = new ColumnDetails[bindtypeObjects.getJSONObject(i)
                        .getJSONArray(AllConstants.ClientProcessing.COLUMNS).length()];

                for (int j = 0; j < columNames.length; j++) {
                    JSONObject columnObject = bindtypeObjects.getJSONObject(i).getJSONArray(AllConstants.ClientProcessing.COLUMNS)
                            .getJSONObject(j);

                    String name = columnObject.getString(AllConstants.ClientProcessing.NAME);
                    String dataType = columnObject.has(AllConstants.ClientProcessing.DATA_TYPES) ? columnObject.getString(AllConstants.ClientProcessing.DATA_TYPES) : AllConstants.ClientProcessing.VARCHAR;
                    columNames[j] =
                            ColumnDetails.builder().name(name).dataType(StringUtils.isBlank(dataType) ? AllConstants.ClientProcessing.VARCHAR : dataType).build();

                }
                bindtypes.add(new CommonRepositoryInformationHolder(bindname, columNames));
                Timber.v("bind type logs %s", bindtypeObjects.getJSONObject(i).getString(AllConstants.ClientProcessing.NAME));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void getEcBindtypes() {
        try {
            if (this.applicationContext() == null) {
                return;
            }
            String str = ReadFromfile(CoreLibrary.getInstance().getEcClientFieldsFile(), this.applicationContext());
            if (StringUtils.isBlank(str)) {
                return;
            }
            JSONObject jsonObject = new JSONObject(str);
            JSONArray bindtypeObjects = jsonObject.getJSONArray("bindobjects");

            for (int i = 0; i < bindtypeObjects.length(); i++) {
                JSONObject columnDefinitionObject = bindtypeObjects.getJSONObject(i);
                String bindname = columnDefinitionObject.getString("name");
                JSONArray columnsJsonArray = columnDefinitionObject.getJSONArray("columns");
                List<ColumnDetails> columnNames = new ArrayList<>();

                // This adds the ability to have multiple mappings for one column and at the same time
                // Prevents the app from crashing when creating the common repository
                HashMap<String, ColumnDetails> uniqueColumnNames = new HashMap<>();

                for (int j = 0; j < columnsJsonArray.length(); j++) {
                    JSONObject columnObject = columnsJsonArray.getJSONObject(j);
                    String colName = columnObject.getString("column_name");
                    String colType = columnObject.has("data_type") ? columnObject.getString("data_type") : "VARCHAR";

                    if (!uniqueColumnNames.containsKey(colName)) {
                        ColumnDetails details = ColumnDetails.builder().name(colName).dataType(StringUtils.isBlank(colType) ? "VARCHAR" : colType).build();
                        uniqueColumnNames.put(colName, details);
                        columnNames.add(details);
                    }
                }
                bindtypes.add(new CommonRepositoryInformationHolder(bindname, columnNames.toArray(new ColumnDetails[0])));
                Timber.v("bind type logs %s", bindname);
            }
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    public String ReadFromfile(String fileName, android.content.Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, android.content.Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (fIn != null) {
                    fIn.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception e2) {
                Timber.e(e2);
            }
        }
        return returnString.toString();
    }

    protected void setApplicationContext(android.content.Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public HTTPAgent getHttpAgent() {
        return httpAgent();
    }

    public Context updateCommonFtsObject(CommonFtsObject commonFtsObject) {
        this.commonFtsObject = commonFtsObject;
        return this;
    }

    public CommonFtsObject commonFtsObject() {
        return commonFtsObject;
    }

    /**
     * Linking generated concept with human readable values
     *
     * @param customHumanReadableConceptResponse
     * @return
     */
    public Context updateCustomHumanReadableConceptResponse(Map<String, String>
                                                                    customHumanReadableConceptResponse) {
        this.customHumanReadableConceptResponse = customHumanReadableConceptResponse;
        return this;
    }

    public Map<String, String> customHumanReadableConceptResponse() {
        if (customHumanReadableConceptResponse == null) {
            return new HashMap<>();
        }
        return customHumanReadableConceptResponse;
    }

    public Map<String, AllCommonsRepository> allCommonsRepositoryMap() {
        Map<String, AllCommonsRepository> allCommonsRepositoryMap = new HashMap<String,
                AllCommonsRepository>();
        for (String ftsTable : commonFtsObject.getTables()) {
            AllCommonsRepository allCommonsRepository = allCommonsRepositoryobjects(ftsTable);
            allCommonsRepositoryMap.put(ftsTable, allCommonsRepository);
        }
        return allCommonsRepositoryMap;
    }

    public void setDetailsRepository(DetailsRepository _detailsRepository) {
        detailsRepository = _detailsRepository;
    }

    public EventClientRepository getEventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository();
        }
        return eventClientRepository;
    }

    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }

    public CampaignRepository getCampaignRepository() {
        if (campaignRepository == null) {
            campaignRepository = new CampaignRepository();
        }
        return campaignRepository;
    }

    public TaskRepository getTaskRepository() {
        if (taskRepository == null) {
            taskNotesRepository = new TaskNotesRepository();
            taskRepository = new TaskRepository(taskNotesRepository);
        }
        return taskRepository;
    }

    public EventClientRepository getForeignEventClientRepository() {
        if (foreignEventClientRepository == null) {
            foreignEventClientRepository = new EventClientRepository(EventClientRepository.Table.foreignClient, EventClientRepository.Table.foreignEvent);
        }
        return foreignEventClientRepository;
    }

    public boolean hasForeignEvents() {
        return DrishtiApplication.getInstance().getP2PClassifier() != null;
    }

    public LocationRepository getLocationRepository() {
        if (locationRepository == null) {
            locationRepository = new LocationRepository();
        }
        return locationRepository;
    }

    public LocationTagRepository getLocationTagRepository() {
        if (locationTagRepository == null) {
            locationTagRepository = new LocationTagRepository();
        }
        return locationTagRepository;
    }

    public StructureRepository getStructureRepository() {
        if (structureRepository == null) {
            structureRepository = new StructureRepository();
        }
        return structureRepository;
    }

    public PlanDefinitionRepository getPlanDefinitionRepository() {
        if (planDefinitionRepository == null) {
            planDefinitionRepository = new PlanDefinitionRepository();
        }
        return planDefinitionRepository;
    }

    public AppProperties getAppProperties() {
        if (appProperties == null) {
            appProperties = Utils.getProperties(this.applicationContext);
        }
        return appProperties;
    }

    public ManifestRepository getManifestRepository() {
        if (manifestRepository == null) {
            manifestRepository = new ManifestRepository();
        }
        return manifestRepository;
    }

    public ClientFormRepository getClientFormRepository() {
        if (clientFormRepository == null) {
            clientFormRepository = new ClientFormRepository();
        }
        return clientFormRepository;
    }

    public ClientRelationshipRepository getClientRelationshipRepository() {
        if (clientRelationshipRepository == null) {
            clientRelationshipRepository = new ClientRelationshipRepository();
        }
        return clientRelationshipRepository;
    }

    ///////////////////////////////////////////////////////////////////////////////
}
