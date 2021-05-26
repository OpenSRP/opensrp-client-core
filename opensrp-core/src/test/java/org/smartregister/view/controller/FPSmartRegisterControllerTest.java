package org.smartregister.view.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.service.AlertService;
import org.smartregister.util.Cache;
import org.smartregister.util.EasyMap;
import org.smartregister.view.contract.AlertDTO;
import org.smartregister.view.contract.FPClient;
import org.smartregister.view.contract.FPClients;
import org.smartregister.view.contract.RefillFollowUps;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class FPSmartRegisterControllerTest extends BaseUnitTest {
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllBeneficiaries allBeneficiaries;
    @Mock
    private AlertService alertService;
    @Mock
    private Context context;

    private FPSmartRegisterController controller;
    private Map<String, String> emptyDetails;

    public static final String[] EC_ALERTS = new String[]{
            "OCP Refill",
            "Condom Refill",
            "DMPA Injectable Refill",
            "Female sterilization Followup 1",
            "Female sterilization Followup 2",
            "Female sterilization Followup 3",
            "Male sterilization Followup 1",
            "Male sterilization Followup 2",
            "IUD Followup 1",
            "IUD Followup 2",
            "FP Followup",
            "FP Referral Followup"
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context);
        emptyDetails = Collections.emptyMap();
        controller = new FPSmartRegisterController(allEligibleCouples, allBeneficiaries, alertService, new Cache<String>(), new Cache<FPClients>());
    }

    private FPClient createFPClient(String entityId, String name, String husbandName, String village, String ecNumber) {
        return new FPClient(entityId, name, husbandName, village, ecNumber).withPhotoPath("../../img/woman-placeholder.png").withAlerts(Collections.<AlertDTO>emptyList());
    }

    @Test
    public void shouldMapECToFPClient() throws Exception {
        Map<String, String> details = EasyMap.create("wifeAge", "22")
                .put("currentMethod", "condom")
                .put("familyPlanningMethodChangeDate", "2013-01-02")
                .put("sideEffects", "sideEffects 1")
                .put("numberOfPregnancies", "2")
                .put("parity", "2")
                .put("numberOfLivingChildren", "1")
                .put("numberOfStillBirths", "1")
                .put("numberOfAbortions", "0")
                .put("isHighPriority", Boolean.toString(false))
                .put("isYoungestChildUnderTwo", "yes")
                .put("youngestChildAge", "3")
                .put("complicationDate", "2011-05-05")
                .put("caste", "sc")
                .put("economicStatus", "bpl")
                .put("fpFollowupDate", "2013-03-04")
                .put("iudPlace", "iudPlace")
                .put("iudPerson", "iudPerson")
                .put("numberOfCondomsSupplied", "numberOfCondomsSupplied")
                .put("numberOfCentchromanPillsDelivered", "numberOfCentchromanPillsDelivered")
                .put("numberOfOCPDelivered", "numberOfOCPDelivered")
                .put("condomSideEffect", "condom side effect")
                .put("iudSidEffect", "iud side effect")
                .put("ocpSideEffect", "ocp side effect")
                .put("sterilizationSideEffect", "sterilization side effect")
                .put("injectableSideEffect", "injectable side effect")
                .put("otherSideEffect", "other side effect")
                .put("highPriorityReason", "high priority reason")
                .map();
        EligibleCouple ec = new EligibleCouple("EC Case 1", "Woman A", "Husband A", "EC Number 1", "Bherya", "Bherya SC", details)
                .withPhotoPath("new photo path");
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec));
        FPClient expectedFPClient = new FPClient("EC Case 1", "Woman A", "Husband A", "Bherya", "EC Number 1")
                .withAge("22")
                .withFPMethod("condom")
                .withFamilyPlanningMethodChangeDate("2013-01-02")
                .withComplicationDate("2011-05-05")
                .withIUDPlace("iudPlace")
                .withIUDPerson("iudPerson")
                .withNumberOfCondomsSupplied("numberOfCondomsSupplied")
                .withNumberOfCentchromanPillsDelivered("numberOfCentchromanPillsDelivered")
                .withNumberOfOCPDelivered("numberOfOCPDelivered").withFPMethodFollowupDate("2013-03-04")
                .withCaste("sc")
                .withEconomicStatus("bpl")
                .withNumberOfPregnancies("2")
                .withParity("2")
                .withNumberOfLivingChildren("1")
                .withNumberOfStillBirths("1")
                .withNumberOfAbortions("0")
                .withIsYoungestChildUnderTwo(true)
                .withYoungestChildAge("3")
                .withIsHighPriority(false)
                .withPhotoPath("new photo path")
                .withCondomSideEffect("condom side effect")
                .withIUDSidEffect("iud side effect")
                .withOCPSideEffect("ocp side effect")
                .withSterilizationSideEffect("sterilization side effect")
                .withInjectableSideEffect("injectable side effect")
                .withOtherSideEffect("other side effect")
                .withHighPriorityReason("high priority reason")
                .withAlerts(Collections.<AlertDTO>emptyList());

        FPClients actualClients = controller.getClients();

        Assert.assertEquals(Arrays.asList(expectedFPClient), actualClients);
    }

    @Test
    public void shouldCreateFPClientsWithOCPRefillAlert() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "Woman C", "Husband C", "EC Number 3", "Bherya", "Bherya SC", emptyDetails);
        Alert ocpRefillAlert = new Alert("entity id 1", "OCP Refill", "OCP Refill", AlertStatus.normal, "2013-01-01", "2013-02-01");
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec));
        Mockito.when(alertService.findByEntityIdAndAlertNames("entity id 1", EC_ALERTS)).thenReturn(Arrays.asList(ocpRefillAlert));

        FPClients actualClients = controller.getClients();

        Mockito.verify(alertService).findByEntityIdAndAlertNames("entity id 1", EC_ALERTS);
        AlertDTO expectedAlertDto = new AlertDTO("OCP Refill", "normal", "2013-01-01");
        FPClient expectedEC = createFPClient("entity id 1", "Woman C", "Husband C", "Bherya", "EC Number 3").withAlerts(Arrays.asList(expectedAlertDto)).withNumberOfAbortions("0").withNumberOfPregnancies("0").withNumberOfStillBirths("0").withNumberOfLivingChildren("0").withParity("0");
        Assert.assertEquals(Arrays.asList(expectedEC), actualClients);
    }

    @Test
    public void shouldCreateFPClientsWithRefillFollowUps() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "Woman C", "Husband C", "EC Number 3", "Bherya", "Bherya SC", EasyMap.create("currentMethod", "condom").map());
        Alert condomRefillAlert = new Alert("entity id 1", "Condom Refill", "Condom Refill", AlertStatus.urgent, "2013-01-01", "2013-02-01");
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec));
        Mockito.when(alertService.findByEntityIdAndAlertNames("entity id 1", EC_ALERTS)).thenReturn(Arrays.asList(condomRefillAlert));
        Mockito.when(context.getStringResource(R.string.str_refill)).thenReturn("refill");
        CoreLibrary.reset(context);

        FPClients clients = controller.getClients();

        Mockito.verify(alertService).findByEntityIdAndAlertNames("entity id 1", EC_ALERTS);

        AlertDTO expectedAlertDto = new AlertDTO("Condom Refill", "urgent", "2013-01-01");
        FPClient expectedEC = createFPClient("entity id 1", "Woman C", "Husband C", "Bherya", "EC Number 3")
                .withAlerts(Arrays.asList(expectedAlertDto))
                .withNumberOfAbortions("0")
                .withNumberOfPregnancies("0")
                .withNumberOfStillBirths("0")
                .withNumberOfLivingChildren("0")
                .withParity("0")
                .withFPMethod("condom")
                .withRefillFollowUps(new RefillFollowUps("Condom Refill", expectedAlertDto, "refill"));
        Assert.assertEquals(Arrays.asList(expectedEC), clients);
    }

    @Test
    public void shouldNotIncludePregnantECsWhenFPClientsIsCreated() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "Woman C", "Husband C", "EC Number 3", "Bherya", "Bherya SC", emptyDetails);
        EligibleCouple pregnantEC = new EligibleCouple("entity id 2", "Woman D", "Husband D", "EC Number 4", "Bherya", "Bherya SC", emptyDetails);
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec, pregnantEC));
        Mockito.when(allBeneficiaries.isPregnant("entity id 1")).thenReturn(false);
        Mockito.when(allBeneficiaries.isPregnant("entity id 2")).thenReturn(true);

        FPClients actualClients = controller.getClients();

        Mockito.verify(allBeneficiaries).isPregnant("entity id 1");
        Mockito.verify(allBeneficiaries).isPregnant("entity id 2");
        FPClient expectedEC = createFPClient("entity id 1", "Woman C", "Husband C", "Bherya", "EC Number 3").withNumberOfAbortions("0").withNumberOfPregnancies("0").withNumberOfStillBirths("0").withNumberOfLivingChildren("0").withParity("0");
        Assert.assertEquals(Arrays.asList(expectedEC), actualClients);
    }
}
