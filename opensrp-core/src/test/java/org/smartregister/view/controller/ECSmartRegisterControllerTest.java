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
import org.smartregister.domain.Child;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.util.Cache;
import org.smartregister.util.EasyMap;
import org.smartregister.view.contract.ECChildClient;
import org.smartregister.view.contract.ECClient;
import org.smartregister.view.contract.ECClients;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ECSmartRegisterControllerTest extends BaseUnitTest {
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllBeneficiaries allBeneficiaries;

    private ECSmartRegisterController controller;
    private Map<String, String> emptyDetails;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        emptyDetails = Collections.emptyMap();
        controller = new ECSmartRegisterController(allEligibleCouples, allBeneficiaries, new Cache<String>(), new Cache<ECClients>());
    }

    @Test
    public void shouldSortECsByName() throws Exception {
        EligibleCouple ec2 = new EligibleCouple("entity id 2", "Woman B", "Husband B", "2", "kavalu_hosur", "Bherya SC", emptyDetails);
        EligibleCouple ec3 = new EligibleCouple("entity id 3", "Woman C", "Husband C", "3", "Bherya", "Bherya SC", emptyDetails);
        EligibleCouple ec1 = new EligibleCouple("entity id 1", "Woman A", "Husband A", "1", "Bherya", null, emptyDetails);
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec2, ec3, ec1));
        ECClient expectedClient1 = createECClient("entity id 1", "Woman A", "Husband A", "Bherya", 1);
        ECClient expectedClient2 = createECClient("entity id 2", "Woman B", "Husband B", "kavalu_hosur", 2);
        ECClient expectedClient3 = createECClient("entity id 3", "Woman C", "Husband C", "Bherya", 3);

        String clients = controller.get();

        List<ECClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ECClient>>() {
        }.getType());
        Assert.assertEquals(Arrays.asList(expectedClient1, expectedClient2, expectedClient3), actualClients);
    }

    private ECClient createECClient(String entityId, String name, String husbandName, String village, Integer ecNumber) {
        return new ECClient(entityId, name, husbandName, village, ecNumber)
                .withPhotoPath("../../img/woman-placeholder.png")
                .withIsOutOfArea(false)
                .withStatus(EasyMap.mapOf("type", "ec"));
    }

    @Test
    public void shouldMapECToECClient() throws Exception {
        Map<String, String> details = EasyMap.create("wifeAge", "22")
                .put("womanDOB", "1984-01-01")
                .put("currentMethod", "condom")
                .put("familyPlanningMethodChangeDate", "2013-01-02")
                .put("numberOfPregnancies", "2")
                .put("parity", "2")
                .put("numberOfLivingChildren", "1")
                .put("numberOfStillBirths", "1")
                .put("numberOfAbortions", "0")
                .put("isHighPriority", Boolean.toString(false))
                .put("caste", "sc")
                .put("economicStatus", "bpl")
                .put("iudPlace", "iudPlace")
                .put("iudPerson", "iudPerson")
                .put("numberOfCondomsSupplied", "numberOfCondomsSupplied")
                .put("numberOfCentchromanPillsDelivered", "numberOfCentchromanPillsDelivered")
                .put("numberOfOCPDelivered", "numberOfOCPDelivered")
                .put("highPriorityReason", "high priority reason")
                .map();
        EligibleCouple ec = new EligibleCouple("entity id 1", "Woman A", "Husband A", "1", "Bherya", "Bherya SC", details)
                .withPhotoPath("new photo path").asOutOfArea();
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec));
        ECClient expectedECClient = new ECClient("entity id 1", "Woman A", "Husband A", "Bherya", 1)
                .withDateOfBirth("1984-01-01")
                .withFPMethod("condom")
                .withFamilyPlanningMethodChangeDate("2013-01-02")
                .withIUDPlace("iudPlace")
                .withIUDPerson("iudPerson")
                .withNumberOfCondomsSupplied("numberOfCondomsSupplied")
                .withNumberOfCentchromanPillsDelivered("numberOfCentchromanPillsDelivered")
                .withNumberOfOCPDelivered("numberOfOCPDelivered")
                .withCaste("sc")
                .withEconomicStatus("bpl")
                .withNumberOfPregnancies("2")
                .withParity("2")
                .withNumberOfLivingChildren("1")
                .withNumberOfStillBirths("1")
                .withNumberOfAbortions("0")
                .withIsHighPriority(false)
                .withPhotoPath("new photo path")
                .withHighPriorityReason("high priority reason")
                .withStatus(EasyMap.create("date", "2013-01-02").put("type", "fp").map())
                .withIsOutOfArea(ec.isOutOfArea());

        String clients = controller.get();

        List<ECClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ECClient>>() {
        }.getType());
        Assert.assertEquals(Arrays.asList(expectedECClient), actualClients);
    }

    @Test
    public void shouldAddYoungestTwoChildrenToECClient() throws Exception {
        EligibleCouple ec1 = new EligibleCouple("entity id 1", "Woman A", "Husband A", "1", "Bherya", null, emptyDetails);
        Child firstChild = new Child("child id 1", "mother id 1", "1234567", "2010-01-01", "female", emptyDetails);
        Child secondChild = new Child("child id 2", "mother id 1", "1234568", "2011-01-01", "female", emptyDetails);
        Child thirdChild = new Child("child id 3", "mother id 1", "1234569", "2012-01-01", "male", emptyDetails);
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec1));
        Mockito.when(allBeneficiaries.findAllChildrenByECId("entity id 1")).thenReturn(Arrays.asList(firstChild, secondChild, thirdChild));
        ECClient expectedClient1 = createECClient("entity id 1", "Woman A", "Husband A", "Bherya", 1)
                .withChildren(Arrays.asList(new ECChildClient("child id 2", "female", "2011-01-01"), new ECChildClient("child id 3", "male", "2012-01-01")));

        String clients = controller.get();

        List<ECClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ECClient>>() {
        }.getType());
        Assert.assertEquals(Arrays.asList(expectedClient1), actualClients);
    }

    @Test
    public void shouldAddStatusToECClientAsECWhenNoMotherAndNoFPMethod() throws Exception {
        EligibleCouple ec1 = new EligibleCouple("entity id 1", "Woman A", "Husband A", "1", "Bherya", null,
                EasyMap.create("registrationDate", "2013-02-02").put("currentMethod", "none").map());
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec1));
        Mockito.when(allBeneficiaries.findMotherWithOpenStatusByECId("entity id 1")).thenReturn(null);
        ECClient expectedClient1 = createECClient("entity id 1", "Woman A", "Husband A", "Bherya", 1)
                .withFPMethod("none")
                .withStatus(EasyMap.create("type", "ec").put("date", "2013-02-02").map());

        String clients = controller.get();

        List<ECClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ECClient>>() {
        }.getType());
        Assert.assertEquals(Arrays.asList(expectedClient1), actualClients);
    }

    @Test
    public void shouldAddStatusToECClientAsECWhenNoMotherAndHasFPMethod() throws Exception {
        EligibleCouple ec1 = new EligibleCouple("entity id 1", "Woman A", "Husband A", "1", "Bherya", null,
                EasyMap.create("familyPlanningMethodChangeDate", "2013-02-02").put("currentMethod", "condom").map());
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec1));
        Mockito.when(allBeneficiaries.findMotherWithOpenStatusByECId("entity id 1")).thenReturn(null);
        ECClient expectedClient1 = createECClient("entity id 1", "Woman A", "Husband A", "Bherya", 1)
                .withFamilyPlanningMethodChangeDate("2013-02-02")
                .withFPMethod("condom")
                .withStatus(EasyMap.create("type", "fp").put("date", "2013-02-02").map());

        String clients = controller.get();

        List<ECClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ECClient>>() {
        }.getType());
        Assert.assertEquals(Arrays.asList(expectedClient1), actualClients);
    }

    @Test
    public void shouldAddStatusToECClientAsANCWhenMotherIsActiveAndIsInANCState() throws Exception {
        EligibleCouple ec1 = new EligibleCouple("entity id 1", "Woman A", "Husband A", "1", "Bherya", null, emptyDetails);
        Mother mother = new Mother("mother id 1", "entity id 1", "thayi card 1", "2013-01-01").withType("anc").withDetails(EasyMap.mapOf("edd", "Sat, 12 Oct 2013 00:00:00 GMT"));
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec1));
        Mockito.when(allBeneficiaries.findMotherWithOpenStatusByECId("entity id 1")).thenReturn(mother);
        ECClient expectedClient1 = createECClient("entity id 1", "Woman A", "Husband A", "Bherya", 1)
                .withStatus(EasyMap.create("date", "2013-01-01").put("edd", "2013-10-12").put("type", "anc").map());

        String clients = controller.get();

        List<ECClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ECClient>>() {
        }.getType());
        Assert.assertEquals(Arrays.asList(expectedClient1), actualClients);
    }

    @Test
    public void shouldAddStatusToECClientAsPNCWhenMotherIsActiveAndIsInPNCStateAndHasNoFP() throws Exception {
        EligibleCouple ec1 = new EligibleCouple("entity id 1", "Woman A", "Husband A", "1", "Bherya", null, EasyMap.mapOf("currentMethod", "none"));
        Mother mother = new Mother("mother id 1", "entity id 1", "thayi card 1", "2013-01-01").withType("pnc");
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec1));
        Mockito.when(allBeneficiaries.findMotherWithOpenStatusByECId("entity id 1")).thenReturn(mother);
        ECClient expectedClient1 = createECClient("entity id 1", "Woman A", "Husband A", "Bherya", 1).withFPMethod("none")
                .withStatus(EasyMap.create("date", "2013-01-01").put("type", "pnc").map());

        String clients = controller.get();

        List<ECClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ECClient>>() {
        }.getType());
        Assert.assertEquals(Arrays.asList(expectedClient1), actualClients);
    }

    @Test
    public void shouldAddStatusToECClientAsPNCWhenMotherIsActiveAndIsInPNCStateAndHasFPMethod() throws Exception {
        EligibleCouple ec1 = new EligibleCouple("entity id 1", "Woman A", "Husband A", "1", "Bherya", null,
                EasyMap.create("familyPlanningMethodChangeDate", "2013-01-01").put("currentMethod", "condom").map());
        Mother mother = new Mother("mother id 1", "entity id 1", "thayi card 1", "2013-01-01").withType("pnc");
        Mockito.when(allEligibleCouples.all()).thenReturn(Arrays.asList(ec1));
        Mockito.when(allBeneficiaries.findMotherWithOpenStatusByECId("entity id 1")).thenReturn(mother);
        ECClient expectedClient1 = createECClient("entity id 1", "Woman A", "Husband A", "Bherya", 1).withFPMethod("condom")
                .withFamilyPlanningMethodChangeDate("2013-01-01")
                .withFPMethod("condom")
                .withStatus(EasyMap.create("type", "pnc/fp")
                        .put("date", "2013-01-01")
                        .put("fpMethodDate", "2013-01-01")
                        .map());

        String clients = controller.get();

        List<ECClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<ECClient>>() {
        }.getType());
        Assert.assertEquals(Arrays.asList(expectedClient1), actualClients);
    }
}
