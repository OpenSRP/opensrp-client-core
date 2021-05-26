package org.smartregister.view.controller;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.domain.ServiceProvided;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.service.AlertService;
import org.smartregister.service.ServiceProvidedService;
import org.smartregister.util.Cache;
import org.smartregister.util.EasyMap;
import org.smartregister.view.contract.ANCClient;
import org.smartregister.view.contract.ANCClients;
import org.smartregister.view.contract.AlertDTO;
import org.smartregister.view.contract.ServiceProvidedDTO;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.contract.Visits;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ANCSmartRegisterControllerTest extends BaseUnitTest {
    public static final String[] ANC_ALERTS = new String[]{
            "ANC 1",
            "ANC 2",
            "ANC 3",
            "ANC 4",
            "IFA 1",
            "IFA 2",
            "IFA 3",
            "REMINDER",
            "TT 1",
            "TT 2",
            "Hb Test 1",
            "Hb Followup Test",
            "Hb Test 2",
            "Delivery Plan"
    };
    public static final String[] ANC_SERVICES = new String[]{
            "IFA",
            "TT 1",
            "TT 2",
            "TT Booster",
            "Hb Test",
            "ANC 1",
            "ANC 2",
            "ANC 3",
            "ANC 4",
            "Delivery Plan"
    };
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllBeneficiaries allBeneficiaries;
    @Mock
    private AlertService alertService;
    @Mock
    private ServiceProvidedService sericeProvidedService;

    private ANCSmartRegisterController controller;
    private Map<String, String> emptyMap;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        emptyMap = Collections.emptyMap();
        controller = new ANCSmartRegisterController(sericeProvidedService, alertService, allBeneficiaries, new Cache<String>(), new Cache<ANCClients>());
    }

    @Test
    public void shouldSortANCsByWifeName() throws Exception {
        Map<String, String> details = EasyMap.mapOf("edd", "Tue, 25 Feb 2014 00:00:00 GMT");
        EligibleCouple ec2 = new EligibleCouple("EC Case 2", "Woman B", "Husband B", "EC Number 2", "kavalu_hosur", "Bherya SC", emptyMap);
        EligibleCouple ec3 = new EligibleCouple("EC Case 3", "Woman C", "Husband C", "EC Number 3", "Bherya", "Bherya SC", emptyMap);
        EligibleCouple ec1 = new EligibleCouple("EC Case 1", "Woman A", "Husband A", "EC Number 1", "Bherya", null, emptyMap);
        Mother m1 = new Mother("Entity X", "EC Case 2", "thayi 1", "2013-05-25").withDetails(details);
        Mother m2 = new Mother("Entity Y", "EC Case 3", "thayi 2", "2013-05-25").withDetails(details);
        Mother m3 = new Mother("Entity Z", "EC Case 1", "thayi 3", "2013-05-25").withDetails(details);
        Map<String, Visits> serviceToVisitsMap = EasyMap.create("tt", new Visits()).put("pnc", new Visits()).put("ifa", new Visits()).put("delivery_plan", new Visits()).put("anc", new Visits()).put("hb", new Visits()).map();
        ANCClient expectedClient1 = createANCClient("Entity Z", "Woman A", "Bherya", "thayi 3", "Tue, 25 Feb 2014 00:00:00 GMT", "2013-05-25").withECNumber("EC Number 1").withHusbandName("Husband A").withEntityIdToSavePhoto("EC Case 1").withServiceToVisitMap(serviceToVisitsMap).withHighRiskReason("");
        ANCClient expectedClient2 = createANCClient("Entity X", "Woman B", "kavalu_hosur", "thayi 1", "Tue, 25 Feb 2014 00:00:00 GMT", "2013-05-25").withECNumber("EC Number 2").withHusbandName("Husband B").withEntityIdToSavePhoto("EC Case 2").withServiceToVisitMap(serviceToVisitsMap).withHighRiskReason("");
        ANCClient expectedClient3 = createANCClient("Entity Y", "Woman C", "Bherya", "thayi 2", "Tue, 25 Feb 2014 00:00:00 GMT", "2013-05-25").withECNumber("EC Number 3").withHusbandName("Husband C").withEntityIdToSavePhoto("EC Case 3").withServiceToVisitMap(serviceToVisitsMap).withHighRiskReason("");
        Mockito.when(allBeneficiaries.allANCsWithEC()).thenReturn(Arrays.asList(Pair.of(m1, ec2), Pair.of(m2, ec3), Pair.of(m3, ec1)));

        ANCClients actualClients = controller.getClients();

        Assert.assertEquals(Arrays.asList(expectedClient1, expectedClient2, expectedClient3), actualClients);
    }

    @Test
    public void shouldGetANCClientsListWithSortedANCsByWifeName() throws Exception {
        Map<String, String> details = EasyMap.mapOf("edd", "Tue, 25 Feb 2014 00:00:00 GMT");
        EligibleCouple ec2 = new EligibleCouple("EC Case 2", "Woman B", "Husband B", "EC Number 2", "kavalu_hosur", "Bherya SC", emptyMap);
        EligibleCouple ec3 = new EligibleCouple("EC Case 3", "Woman C", "Husband C", "EC Number 3", "Bherya", "Bherya SC", emptyMap);
        EligibleCouple ec1 = new EligibleCouple("EC Case 1", "Woman A", "Husband A", "EC Number 1", "Bherya", null, emptyMap);
        Mother m1 = new Mother("Entity X", "EC Case 2", "thayi 1", "2013-05-25").withDetails(details);
        Mother m2 = new Mother("Entity Y", "EC Case 3", "thayi 2", "2013-05-25").withDetails(details);
        Mother m3 = new Mother("Entity Z", "EC Case 1", "thayi 3", "2013-05-25").withDetails(details);

        Map<String, Visits> serviceToVisitsMap = EasyMap.create("tt", new Visits()).put("pnc", new Visits()).put("ifa", new Visits()).put("delivery_plan", new Visits()).put("anc", new Visits()).put("hb", new Visits()).map();
        ANCClient expectedClient1 = createANCClient("Entity Z", "Woman A", "Bherya", "thayi 3", "Tue, 25 Feb 2014 00:00:00 GMT", "2013-05-25").withECNumber("EC Number 1").withHusbandName("Husband A").withEntityIdToSavePhoto("EC Case 1").withServiceToVisitMap(serviceToVisitsMap).withHighRiskReason("");
        ANCClient expectedClient2 = createANCClient("Entity X", "Woman B", "kavalu_hosur", "thayi 1", "Tue, 25 Feb 2014 00:00:00 GMT", "2013-05-25").withECNumber("EC Number 2").withHusbandName("Husband B").withEntityIdToSavePhoto("EC Case 2").withServiceToVisitMap(serviceToVisitsMap).withHighRiskReason("");
        ANCClient expectedClient3 = createANCClient("Entity Y", "Woman C", "Bherya", "thayi 2", "Tue, 25 Feb 2014 00:00:00 GMT", "2013-05-25").withECNumber("EC Number 3").withHusbandName("Husband C").withEntityIdToSavePhoto("EC Case 3").withServiceToVisitMap(serviceToVisitsMap).withHighRiskReason("");

        Mockito.when(allBeneficiaries.allANCsWithEC()).thenReturn(Arrays.asList(Pair.of(m1, ec2), Pair.of(m2, ec3), Pair.of(m3, ec1)));

        SmartRegisterClients actualClients = controller.getClients();

        Assert.assertEquals(Arrays.asList(expectedClient1, expectedClient2, expectedClient3), actualClients);
    }

    @Test
    public void shouldMapANCToANCClient() throws Exception {
        Map<String, String> details =
                EasyMap.create("edd", "Tue, 25 Feb 2014 00:00:00 GMT")
                        .put("isHighRisk", "yes")
                        .put("ancNumber", "ANC X")
                        .put("highRiskReason", "Headache")
                        .put("riskObservedDuringANC", "Headache")
                        .put("ashaPhoneNumber", "Asha phone number 1")
                        .map();
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "Woman A", "Husband A", "EC Number 1", "Bherya", null,
                EasyMap.create("wifeAge", "23")
                        .put("isHighPriority", Boolean.toString(false))
                        .put("caste", "other")
                        .put("economicStatus", "bpl")
                        .map()
        ).asOutOfArea();
        Mother mother = new Mother("Entity X", "ec id 1", "thayi 1", "2013-05-25").withDetails(details);
        Map<String, Visits> serviceToVisitsMap = EasyMap.create("tt", new Visits()).put("pnc", new Visits()).put("ifa", new Visits()).put("delivery_plan", new Visits()).put("anc", new Visits()).put("hb", new Visits()).map();
        Mockito.when(allBeneficiaries.allANCsWithEC()).thenReturn(Arrays.asList(Pair.of(mother, eligibleCouple)));
        ANCClient expectedANCClient = new ANCClient("Entity X", "Bherya", "Woman A", "thayi 1", "Tue, 25 Feb 2014 00:00:00 GMT", "2013-05-25")
                .withECNumber("EC Number 1")
                .withIsHighPriority(false)
                .withAge("23")
                .withHusbandName("Husband A")
                .withIsOutOfArea(true)
                .withIsHighRisk(true)
                .withCaste("other")
                .withANCNumber("ANC X")
                .withHighRiskReason("Headache")
                .withPhotoPath("../../img/woman-placeholder.png")
                .withEconomicStatus("bpl")
                .withEntityIdToSavePhoto("ec id 1")
                .withAlerts(Collections.<AlertDTO>emptyList())
                .withAshaPhoneNumber("Asha phone number 1")
                .withServicesProvided(Collections.<ServiceProvidedDTO>emptyList())
                .withServiceToVisitMap(serviceToVisitsMap);

        ANCClients actualClients = controller.getClients();

        Assert.assertEquals(Arrays.asList(expectedANCClient), actualClients);
    }

    @Test
    public void shouldCreateANCClientsWithANC1AlertAndDeliveryPlanAlert() throws Exception {
        Map<String, String> details = EasyMap.mapOf("edd", "Tue, 25 Feb 2014 00:00:00 GMT");
        EligibleCouple ec = new EligibleCouple("entity id 1", "Woman C", "Husband C", "EC Number 1", "Bherya", "Bherya SC", emptyMap);
        Mother mother = new Mother("Entity X", "entity id 1", "thayi 1", "2012-10-25").withDetails(details);
        Alert anc1Alert = new Alert("entity id 1", "ANC", "ANC 1", AlertStatus.normal, "2013-01-01", "2013-02-01");
        Alert deliveryPlanAlert = new Alert("entity id 1", "Delivery Plan", "Delivery Plan", AlertStatus.normal, "2012-10-25", "2013-08-25");
        Mockito.when(allBeneficiaries.allANCsWithEC()).thenReturn(Arrays.asList(Pair.of(mother, ec)));
        Mockito.when(alertService.findByEntityIdAndAlertNames("Entity X", ANC_ALERTS)).thenReturn(Arrays.asList(anc1Alert, deliveryPlanAlert));

        ANCClients actualClients = controller.getClients();

        Mockito.verify(alertService).findByEntityIdAndAlertNames("Entity X", ANC_ALERTS);

        AlertDTO expectedANC1AlertDto = new AlertDTO("ANC 1", "normal", "2013-01-01");
        AlertDTO expectedDeliveryPlanAlertDto = new AlertDTO("Delivery Plan", "normal", "2012-10-25");
        Visits emptyVisit = new Visits();
        Visits expectedANCVisit = new Visits();
        expectedANCVisit.toProvide = expectedANC1AlertDto;
        Visits expectedDeliveryPlanVisit = new Visits();
        expectedDeliveryPlanVisit.toProvide = expectedDeliveryPlanAlertDto;
        Map<String, Visits> serviceToVisitsMap = EasyMap.create("tt", emptyVisit).put("pnc", emptyVisit).put("ifa", emptyVisit).put("delivery_plan", expectedDeliveryPlanVisit).put("anc", expectedANCVisit).put("hb", emptyVisit).map();

        ANCClient expectedEC = createANCClient("Entity X", "Woman C", "Bherya", "thayi 1", "Tue, 25 Feb 2014 00:00:00 GMT", "2012-10-25")
                .withECNumber("EC Number 1")
                .withHusbandName("Husband C")
                .withEntityIdToSavePhoto("entity id 1")
                .withAlerts(Arrays.asList(expectedANC1AlertDto, expectedDeliveryPlanAlertDto))
                .withServiceToVisitMap(serviceToVisitsMap)
                .withHighRiskReason("");

        Assert.assertEquals(Arrays.asList(expectedEC), actualClients);
    }

    @Test
    public void shouldCreateANCClientsWithServicesProvided() throws Exception {
        Map<String, String> details = EasyMap.mapOf("edd", "Tue, 25 Feb 2014 00:00:00 GMT");
        EligibleCouple ec = new EligibleCouple("entity id 1", "Woman C", "Husband C", "EC Number 1", "Bherya", "Bherya SC", emptyMap);
        Mother mother = new Mother("Entity X", "entity id 1", "thayi 1", "2013-05-25").withDetails(details);
        Mockito.when(allBeneficiaries.allANCsWithEC()).thenReturn(Arrays.asList(Pair.of(mother, ec)));
        Mockito.when(alertService.findByEntityIdAndAlertNames("Entity X", ANC_ALERTS)).thenReturn(Collections.<Alert>emptyList());
        Mockito.when(sericeProvidedService.findByEntityIdAndServiceNames("Entity X", ANC_SERVICES))
                .thenReturn(Arrays.asList(new ServiceProvided("entity id 1", "IFA", "2013-01-01", EasyMap.mapOf("dose", "100")),
                        new ServiceProvided("entity id 1", "TT 1", "2013-02-01", emptyMap),
                        new ServiceProvided("entity id 1", "Delivery Plan", "2013-02-01", emptyMap)
                ));

        ANCClients actualClients = controller.getClients();

        Mockito.verify(alertService).findByEntityIdAndAlertNames("Entity X", ANC_ALERTS);

        Mockito.verify(sericeProvidedService).findByEntityIdAndServiceNames("Entity X", ANC_SERVICES);

        ServiceProvidedDTO IFAServiceProvidedDTO = new ServiceProvidedDTO("IFA", "2013-01-01", EasyMap.mapOf("dose", "100"));
        ServiceProvidedDTO deliveryPlanServiceProvidedDTO = new ServiceProvidedDTO("Delivery Plan", "2013-02-01", emptyMap);
        ServiceProvidedDTO ttServiceProvidedDTO = new ServiceProvidedDTO("TT 1", "2013-02-01", emptyMap);
        List<ServiceProvidedDTO> expectedServicesProvided = Arrays.asList(IFAServiceProvidedDTO, ttServiceProvidedDTO, deliveryPlanServiceProvidedDTO);
        Visits emptyVisit = new Visits();
        Visits expectedIFAServiceProvided = new Visits();
        expectedIFAServiceProvided.provided = IFAServiceProvidedDTO;
        Visits expectedDeliveryPlanServiceProvided = new Visits();
        expectedDeliveryPlanServiceProvided.provided = deliveryPlanServiceProvidedDTO;
        Visits expectedTTServiceProvided = new Visits();
        expectedTTServiceProvided.provided = ttServiceProvidedDTO;

        Map<String, Visits> serviceToVisitsMap = EasyMap.create("tt", expectedTTServiceProvided).put("pnc", emptyVisit).put("ifa", expectedIFAServiceProvided).put("delivery_plan", expectedDeliveryPlanServiceProvided).put("anc", emptyVisit).put("hb", emptyVisit).map();

        ANCClient expectedEC = createANCClient("Entity X", "Woman C", "Bherya", "thayi 1", "Tue, 25 Feb 2014 00:00:00 GMT", "2013-05-25")
                .withECNumber("EC Number 1")
                .withHusbandName("Husband C")
                .withEntityIdToSavePhoto("entity id 1")
                .withServicesProvided(expectedServicesProvided)
                .withServiceToVisitMap(serviceToVisitsMap)
                .withHighRiskReason("");

        Assert.assertEquals(Arrays.asList(expectedEC), actualClients);
    }

    private ANCClient createANCClient(String entityId, String name, String village, String thayi, String edd, String lmp) {
        return new ANCClient(entityId, village, name, thayi, edd, lmp)
                .withPhotoPath("../../img/woman-placeholder.png")
                .withIsOutOfArea(false)
                .withAlerts(Collections.<AlertDTO>emptyList())
                .withServicesProvided(Collections.<ServiceProvidedDTO>emptyList());
    }
}
