package org.smartregister.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.util.EasyMap;

import java.util.HashMap;
import java.util.Map;

public class TimelineEventTest {

    private HashMap<String, String> detailsWithData;
    private Map<String, String> detailsWithoutData;

    @Before
    public void setUp() throws Exception {
        detailsWithData = new HashMap<String, String>();
        detailsWithData.put("bpSystolic", "120");
        detailsWithData.put("bpDiastolic", "80");
        detailsWithData.put("temperature", "98");
        detailsWithData.put("weight", "48");
        detailsWithData.put("hbLevel", "11");
        detailsWithData.put("motherTemperature", "98");
        detailsWithData.put("childTemperature", "98");
        detailsWithData.put("childWeight", "4");
        detailsWithData.put("dateOfDelivery", "2012-08-01");
        detailsWithData.put("placeOfDelivery", "Govt Hospital");

        detailsWithoutData = new HashMap<String, String>();
    }

    @Test
    public void shouldCreateTimelineEventForANCVisitWithDetails() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forANCCareProvided("CASE A",
                "1",
                "2012-01-01",
                detailsWithData);

        Assert.assertTrue(timelineEvent.detail1().contains("BP: 120/80"));
        Assert.assertTrue(timelineEvent.detail1().contains("Temp: 98 °F"));
        Assert.assertTrue(timelineEvent.detail1().contains("Weight: 48 kg"));
        Assert.assertTrue(timelineEvent.detail1().contains("Hb Level: 11"));
    }

    @Test
    public void shouldCreateTimelineEventForStartOfPregnancyWithReferenceDateAndRegistrationDate
            () throws
            Exception {
        TimelineEvent timelineEvent = TimelineEvent.forStartOfPregnancy("CASE A",
                "2012-01-02",
                "2012-01-01");

        Assert.assertEquals(LocalDate.parse("2012-01-02"), timelineEvent.referenceDate());
        Assert.assertTrue(timelineEvent.detail1().contains("LMP Date: 01-01-2012"));
    }

    @Test
    public void
    shouldCreateTimelineEventForStartOfPregnancyForECWithReferenceDateAndRegistrationDate() throws
            Exception {
        TimelineEvent timelineEvent = TimelineEvent.forStartOfPregnancyForEC("CASE A",
                "1234567",
                "2012-01-02",
                "2012-01-01");

        Assert.assertEquals(LocalDate.parse("2012-01-02"), timelineEvent.referenceDate());
        Assert.assertTrue(timelineEvent.detail1().contains("LMP Date: 01-01-2012"));
    }

    @Test
    public void shouldCreateTimelineEventForANCVisitExcludingThoseDetailsWhichDoNotHaveValue()
            throws
            Exception {
        TimelineEvent timelineEvent = TimelineEvent.forANCCareProvided("CASE A",
                "1",
                "2012-01-01",
                detailsWithoutData);

        Assert.assertFalse(timelineEvent.detail1().contains("BP:"));
        Assert.assertFalse(timelineEvent.detail1().contains("Temp:"));
        Assert.assertFalse(timelineEvent.detail1().contains("Weight:"));
        Assert.assertFalse(timelineEvent.detail1().contains("Hb Level:"));
    }

    @Test
    public void shouldCreateTimelineEventForMotherPNCVisitWithDetails() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forMotherPNCVisit("CASE A",
                "1",
                "2012-01-01",
                "120",
                "80",
                "98",
                "11");

        Assert.assertTrue(timelineEvent.detail1().contains("BP: 120/80"));
        Assert.assertTrue(timelineEvent.detail1().contains("Temp: 98 °F"));
        Assert.assertTrue(timelineEvent.detail1().contains("Hb Level: 11"));
    }

    @Test
    public void
    shouldCreateTimelineEventForMotherPNCVisitExcludingThoseDetailsWhichDoNotHaveValue() throws
            Exception {
        TimelineEvent timelineEvent = TimelineEvent.forMotherPNCVisit("CASE A",
                "1",
                "2012-01-01",
                null,
                null,
                null,
                null);

        Assert.assertFalse(timelineEvent.detail1().contains("BP:"));
        Assert.assertFalse(timelineEvent.detail1().contains("Temp:"));
        Assert.assertFalse(timelineEvent.detail1().contains("Hb Level:"));
    }

    @Test
    public void shouldCreateTimelineEventForChildPNCVisitWithDetails() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildPNCVisit("CASE A",
                "1",
                "2012-01-01",
                "4",
                "98");

        Assert.assertTrue(timelineEvent.detail1().contains("Temp: 98 °F"));
        Assert.assertTrue(timelineEvent.detail1().contains("Weight: 4 kg"));
    }

    @Test
    public void shouldCreateTimelineEventForChildPNCVisitExcludingThoseDetailsWhichDoNotHaveValue
            () throws
            Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildPNCVisit("CASE A",
                "1",
                "2012-01-01",
                null,
                null);

        Assert.assertFalse(timelineEvent.detail1().contains("Temp:"));
        Assert.assertFalse(timelineEvent.detail1().contains("Weight:"));
    }

    @Test
    public void shouldCreateTimelineEventForChildBirthInMotherProfileWithDetails() throws
            Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildBirthInMotherProfile("CASE A",
                "2012-08-01",
                "male",
                "2012-08-01",
                "Govt Hospital");

        Assert.assertTrue(timelineEvent.detail1().contains("On: 01-08-2012"));
        Assert.assertTrue(timelineEvent.detail1().contains("At: Govt Hospital"));
    }

    @Test
    public void shouldCreateTimelineEventWithTitleBasedOnSex() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildBirthInMotherProfile("CASE A",
                "2012-08-01",
                "male",
                "2012-08-01",
                "Govt Hospital");

        Assert.assertTrue(timelineEvent.title().contains("Boy Delivered"));

        timelineEvent = TimelineEvent.forChildBirthInMotherProfile("CASE A",
                "2012-08-01",
                "female",
                "2012-08-01",
                "Govt Hospital");

        Assert.assertTrue(timelineEvent.title().contains("Girl Delivered"));
    }

    @Test
    public void
    shouldCreateTimelineEventForChildBirthInMotherProfileExcludingThoseDetailsWhichDoNotHaveValue
            () throws
            Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildBirthInMotherProfile("CASE A",
                "2012-01-01",
                "male",
                null,
                null);

        Assert.assertFalse(timelineEvent.detail1().contains("On:"));
        Assert.assertFalse(timelineEvent.detail1().contains("At:"));
    }

    @Test
    public void shouldCreateTimelineEventForChildBirthInECProfileWithDetails() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildBirthInECProfile("CASE A",
                "2012-08-01",
                "male",
                "2012-08-01");

        Assert.assertTrue(timelineEvent.detail1().contains("On: 01-08-2012"));
    }

    @Test
    public void
    shouldCreateTimelineEventForChildBirthInECProfileExcludingThoseDetailsWhichDoNotHaveValue()
            throws
            Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildBirthInMotherProfile("CASE A",
                "2012-01-01",
                "male",
                null,
                null);

        Assert.assertFalse(timelineEvent.detail1().contains("On:"));
    }

    @Test
    public void shouldCreateTimelineEventForChildBirthInChildProfileWithDetails() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildBirthInChildProfile("CASE A",
                "2012-08-01",
                "4",
                "bcg opv_0 hepb_0");

        Assert.assertTrue(timelineEvent.detail1().contains("Weight: 4 kg"));
        Assert.assertTrue(timelineEvent.detail1().contains("Immunizations: BCG, OPV 0, HepB 0"));
    }

    @Test
    public void
    shouldCreateTimelineEventForChildBirthInChildProfileExcludingThoseDetailsWhichDoNotHaveValue
            () throws
            Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildBirthInChildProfile("CASE A",
                "2012-01-01",
                null,
                null);

        Assert.assertFalse(timelineEvent.detail1().contains("Weight:"));
        Assert.assertFalse(timelineEvent.detail1().contains("Immunizations:"));
    }

    @Test
    public void shouldCreateTimelineEventForUpdateImmunization() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildImmunization("CASE A", "bcg opv_0", "2012-08-01");

        Assert.assertTrue(timelineEvent.detail1().contains("BCG, OPV 0"));
    }

    @Test
    public void shouldCreateTimelineEventForFPCondomRenew() throws Exception {
        Map<String, String> details = EasyMap.create("Key 1", "Value 1").put("currentMethod", "condom")
                .put("familyPlanningMethodChangeDate",
                        "2012-03-03")
                .put("fpUpdate", "renew_fp_product")
                .put("numberOfCondomsSupplied",
                        "30")
                .map();

        TimelineEvent timelineEvent = TimelineEvent.forFPCondomRenew("CASE A", details);

        Assert.assertTrue(timelineEvent.detail1().contains("Condoms given: 30"));
    }

    @Test
    public void shouldCreateTimelineEventForFPOCPRenew() throws Exception {
        Map<String, String> details = EasyMap.create("Key 1", "Value 1").put("currentMethod", "ocp")
                .put("familyPlanningMethodChangeDate",
                        "2012-03-03")
                .put("fpUpdate", "renew_fp_product")
                .put("numberOfOCPDelivered", "2")
                .map();

        TimelineEvent timelineEvent = TimelineEvent.forFPOCPRenew("CASE A", details);

        Assert.assertTrue(timelineEvent.detail1().contains("OCP cycles given: 2"));
    }

    @Test
    public void shouldCreateTimelineEventForFPIUDRenew() throws Exception {
        Map<String, String> details = EasyMap.create("Key 1", "Value 1").put("currentMethod", "iud")
                .put("familyPlanningMethodChangeDate",
                        "2012-03-03")
                .put("fpUpdate", "renew_fp_product")
                .map();

        TimelineEvent timelineEvent = TimelineEvent.forFPIUDRenew("CASE A", details);

        Assert.assertTrue(timelineEvent.detail1().contains("New IUD insertion date: 2012-03-03"));
    }

    @Test
    public void shouldCreateTimelineEventForFPDMPARenew() throws Exception {
        Map<String, String> details = EasyMap.create("Key 1", "Value 1").put("currentMethod", "dmpa")
                .put("familyPlanningMethodChangeDate",
                        "2012-03-03")
                .put("fpUpdate", "renew_fp_product")
                .map();

        TimelineEvent timelineEvent = TimelineEvent.forFPDMPARenew("CASE A", details);

        Assert.assertTrue(timelineEvent.detail1().contains("DMPA injection date: 2012-03-03"));
    }

    @Test
    public void shouldCreateTimelineEventForDeliveryPlan() throws Exception {

        TimelineEvent timelineEvent = TimelineEvent.forDeliveryPlan("Case 1",
                "Delivery Facility Name",
                "Transportation Plan",
                "Birth Companion",
                "1234567890",
                "Contact Number",
                "High Risk Reason",
                "2012-03-03");
        Assert.assertTrue(timelineEvent.detail1().contains("High Risk Reason: "));
        Assert.assertTrue(timelineEvent.detail1().contains("Phone Number: "));
        Assert.assertTrue(timelineEvent.detail1().contains("Asha Phone Number: "));
        Assert.assertTrue(timelineEvent.detail1().contains("Birth Companion: "));
        Assert.assertTrue(timelineEvent.detail1().contains("Transportation Plan: "));
        Assert.assertTrue(timelineEvent.detail1().contains("Delivery Facility Name: "));
    }
}
