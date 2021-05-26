package org.smartregister.view.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.Child;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.domain.ServiceProvided;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.service.AlertService;
import org.smartregister.service.ServiceProvidedService;
import org.smartregister.util.Cache;
import org.smartregister.util.EasyMap;
import org.smartregister.view.contract.AlertDTO;
import org.smartregister.view.contract.ChildClient;
import org.smartregister.view.contract.ServiceProvidedDTO;
import org.smartregister.view.contract.SmartRegisterClients;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ChildSmartRegisterControllerTest extends BaseUnitTest {

    public static final String[] CHILD_ALERTS = new String[]{
            "bcg",
            "measles", "measlesbooster",
            "opv_0", "opv_1", "opv_2", "opv_3", "opvbooster",
            "dptbooster_1", "dptbooster_2",
            "pentavalent_1", "pentavalent_2", "pentavalent_3",
            "hepb_0",
            "je",
            "mmr"
    };
    private static final String[] CHILD_SERVICES = new String[]{
            "bcg",
            "measles", "measlesbooster",
            "opv_0", "opv_1", "opv_2", "opv_3", "opvbooster",
            "dptbooster_1", "dptbooster_2",
            "pentavalent_1", "pentavalent_2", "pentavalent_3",
            "hepb_0",
            "je",
            "mmr", "Vitamin A", "Illness Visit", "PNC"
    };

    private final Map<String, String> emptyMap = Collections.emptyMap();
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllBeneficiaries allBeneficiaries;
    @Mock
    private AlertService alertService;
    @Mock
    private ServiceProvidedService serviceProvidedService;

    private ChildSmartRegisterController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new ChildSmartRegisterController(serviceProvidedService, alertService, allBeneficiaries, new Cache<String>(), new Cache<SmartRegisterClients>());
    }

    @Test
    public void shouldSortChildrenByMotherName() throws Exception {
        EligibleCouple ec1 = new EligibleCouple("ec id 1", "amma", "appa", "ec no 1", "chikkamagalur", null, emptyMap).asOutOfArea();
        Mother mother1 = new Mother("mother id 1", "ec id 1", "thayi no 1", "2013-01-01").withDetails(emptyMap);
        Child child1 = new Child("child id 1", "mother id 1", "male", emptyMap).withDateOfBirth("2013-01-01").withMother(mother1).withEC(ec1);
        EligibleCouple ec2 = new EligibleCouple("ec id 2", "thayi", "appa", "ec no 2", "chikkamagalur", null, emptyMap).asOutOfArea();
        Mother mother2 = new Mother("mother id 2", "ec id 2", "thayi no 2", "2013-01-01").withDetails(emptyMap);
        Child child2 = new Child("child id 2", "mother id 2", "male", emptyMap).withDateOfBirth("2013-01-01").withMother(mother2).withEC(ec2);
        Mockito.when(allBeneficiaries.allChildrenWithMotherAndEC()).thenReturn(Arrays.asList(child2, child1));
        ChildClient expectedClient1 = createChildClient("child id 1", "thayi no 1", "amma", "ec no 1");
        ChildClient expectedClient2 = createChildClient("child id 2", "thayi no 2", "thayi", "ec no 2");

        String clients = controller.get();

        List<ChildClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ChildClient>>() {
        }.getType());
        Assert.assertEquals(Arrays.asList(expectedClient1, expectedClient2), actualClients);
    }

    @Test
    public void shouldMapPNCToPNCClient() throws Exception {
        Map<String, String> ecDetails = EasyMap.create("wifeAge", "26")
                .put("caste", "others")
                .put("economicStatus", "apl")
                .map();
        Map<String, String> childDetails = EasyMap.create("weight", "3")
                .put("name", "chinnu")
                .put("isChildHighRisk", "yes")
                .map();
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "amma", "appa", "ec no 1", "chikkamagalur", null, ecDetails).asOutOfArea();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi no 1", "2013-01-01").withDetails(emptyMap);
        Child child = new Child("child id 1", "mother id 1", "female", childDetails).withDateOfBirth("2013-01-01").withMother(mother).withEC(eligibleCouple);
        Mockito.when(allBeneficiaries.allChildrenWithMotherAndEC()).thenReturn(Arrays.asList(child));
        ChildClient expectedPNCClient = new ChildClient("child id 1", "female", "3", "thayi no 1")
                .withEntityIdToSavePhoto("child id 1")
                .withName("chinnu")
                .withMotherName("amma")
                .withDOB("2013-01-01")
                .withMotherAge("26")
                .withFatherName("appa")
                .withVillage("chikkamagalur")
                .withOutOfArea(true)
                .withEconomicStatus("apl")
                .withCaste("others")
                .withIsHighRisk(true)
                .withPhotoPath("../../img/icons/child-girlinfant@3x.png")
                .withEntityIdToSavePhoto("child id 1")
                .withECNumber("ec no 1")
                .withAlerts(Collections.<AlertDTO>emptyList())
                .withServicesProvided(Collections.<ServiceProvidedDTO>emptyList());

        String clients = controller.get();

        List<ChildClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ChildClient>>() {
        }.getType());
        Assert.assertEquals(Arrays.asList(expectedPNCClient), actualClients);
    }

    @Test
    public void shouldCreateChildClientsWithAlerts() throws Exception {
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "amma", "appa", "ec no 1", "chikkamagalur", null, emptyMap).asOutOfArea();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi no 1", "2013-01-01").withDetails(emptyMap);
        Child child = new Child("child id 1", "mother id 1", "male", emptyMap).withDateOfBirth("2013-01-01").withMother(mother).withEC(eligibleCouple);
        Alert bcgAlert = new Alert("child id 1", "BCG", "bcg", AlertStatus.normal, "2013-01-01", "2013-02-01");
        Mockito.when(allBeneficiaries.allChildrenWithMotherAndEC()).thenReturn(Arrays.asList(child));
        Mockito.when(alertService.findByEntityIdAndAlertNames("child id 1", CHILD_ALERTS)).thenReturn(Arrays.asList(bcgAlert));

        String clients = controller.get();

        List<ChildClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ChildClient>>() {
        }.getType());
        Mockito.verify(alertService).findByEntityIdAndAlertNames("child id 1", CHILD_ALERTS);
        AlertDTO expectedAlertDto = new AlertDTO("bcg", "normal", "2013-01-01");
        ChildClient expectedPNCClient = new ChildClient("child id 1", "male", null, "thayi no 1")
                .withEntityIdToSavePhoto("child id 1")
                .withMotherName("amma")
                .withDOB("2013-01-01")
                .withFatherName("appa")
                .withVillage("chikkamagalur")
                .withOutOfArea(true)
                .withPhotoPath("../../img/icons/child-infant@3x.png")
                .withEntityIdToSavePhoto("child id 1")
                .withECNumber("ec no 1")
                .withAlerts(Arrays.asList(expectedAlertDto))
                .withServicesProvided(Collections.<ServiceProvidedDTO>emptyList());
        Assert.assertEquals(Arrays.asList(expectedPNCClient), actualClients);
    }

    @Test
    public void shouldCreateChildClientsWithServicesProvided() throws Exception {
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "amma", "appa", "ec no 1", "chikkamagalur", null, emptyMap).asOutOfArea();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi no 1", "2013-01-01").withDetails(emptyMap);
        Child child = new Child("child id 1", "mother id 1", "male", emptyMap).withDateOfBirth("2013-01-01").withMother(mother).withEC(eligibleCouple);
        Mockito.when(allBeneficiaries.allChildrenWithMotherAndEC()).thenReturn(Arrays.asList(child));
        Mockito.when(alertService.findByEntityIdAndAlertNames("child id 1", CHILD_ALERTS)).thenReturn(Collections.<Alert>emptyList());
        Mockito.when(serviceProvidedService.findByEntityIdAndServiceNames("child id 1", CHILD_SERVICES))
                .thenReturn(Arrays.asList(new ServiceProvided("entity id 1", "bcg", "2013-01-01", null)));

        String clients = controller.get();

        List<ChildClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ChildClient>>() {
        }.getType());
        Mockito.verify(serviceProvidedService).findByEntityIdAndServiceNames("child id 1", CHILD_SERVICES);
        List<ServiceProvidedDTO> expectedServicesProvided = Arrays.asList(new ServiceProvidedDTO("bcg", "2013-01-01", null));
        ChildClient expectedPNCClient = createChildClient("child id 1", "thayi no 1", "amma", "ec no 1").withServicesProvided(expectedServicesProvided);
        Assert.assertEquals(Arrays.asList(expectedPNCClient), actualClients);
    }

    private ChildClient createChildClient(String childId, String thayiCardNumber, String motherName, String ecNumber) {
        return new ChildClient(childId, "male", null, thayiCardNumber)
                .withEntityIdToSavePhoto(childId)
                .withMotherName(motherName)
                .withDOB("2013-01-01")
                .withFatherName("appa")
                .withVillage("chikkamagalur")
                .withOutOfArea(true)
                .withPhotoPath("../../img/icons/child-infant@3x.png")
                .withEntityIdToSavePhoto(childId)
                .withECNumber(ecNumber)
                .withAlerts(Collections.<AlertDTO>emptyList())
                .withServicesProvided(Collections.<ServiceProvidedDTO>emptyList());
    }
}
