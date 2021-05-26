package org.smartregister.view.contract;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.util.DateUtil;
import org.smartregister.util.EasyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ANCClientTest extends BaseUnitTest {

    private ANCClient getClient() {
        return new ANCClient("entity id 1", "village name 1", "anc name", "1234567", "Tue, 25 Feb 2014 00:00:00 GMT", "2013-05-25");
    }

    @Test
    public void shouldSatisfyCriteriaIfNameStartsWithDr() throws Exception {
        boolean filterMatches = getClient().satisfiesFilter("anc");

        assertTrue(filterMatches);
    }

    @Test
    public void shouldReturnFalseIfCriteriaDoesNotSatisfyWithAnyClientName() throws Exception {
        boolean filterMatches = getClient().satisfiesFilter("xyz");

        assertFalse(filterMatches);
    }

    @Test
    public void shouldSatisfyCriteriaIfThayiStartsWith123() throws Exception {
        boolean filterMatches = getClient().satisfiesFilter("123");

        assertTrue(filterMatches);
    }

    @Test
    public void shouldReturnFalseIfCriteriaDoesNotSatisfyWithAnyClientThayiNumber() throws Exception {
        boolean filterMatches = getClient().satisfiesFilter("456");

        assertFalse(filterMatches);
    }

    @Test
    public void shouldReturnTheDaysBetweenEDDAndToday() throws Exception {
        DateUtil.fakeIt(new LocalDate("2014-02-28"));

        String daysBetween = getClient().pastDueInDays();

        assertEquals("3", daysBetween);
    }

    @Test
    public void shouldReturnTheWeeksBetweenLMPAndToday() throws Exception {
        DateUtil.fakeIt(new LocalDate("2013-06-05"));

        String weeksBetween = getClient().weeksAfterLMP();

        assertEquals("1", weeksBetween);

        DateUtil.fakeIt(new LocalDate("2013-06-15"));

        weeksBetween = getClient().weeksAfterLMP();

        assertEquals("3", weeksBetween);

    }

    @Test
    public void shouldAddANCAlertToServiceToVisitMap() throws Exception {
        AlertDTO ancAlert = new AlertDTO("ANC 1", "urgent", "2013-02-01");
        ANCClient ancClient = getClient().withAlerts(asList(ancAlert))
                .withServicesProvided(new ArrayList<ServiceProvidedDTO>());

        ANCClient preprocessedClients = ancClient.withPreProcess();

        Visits emptyVisit = new Visits();
        Visits visits = new Visits();
        visits.toProvide = ancAlert;
        Map<String, Visits> serviceToVisitsMap = EasyMap.create("tt", emptyVisit).put("pnc", emptyVisit).put("ifa", emptyVisit).put("delivery_plan", emptyVisit).put("anc", visits).put("hb", emptyVisit).map();

        assertEquals(preprocessedClients.serviceToVisitsMap(), serviceToVisitsMap);

    }

    @Test
    public void shouldAddANCServiceProvidedToServiceToVisitMap() throws Exception {
        ServiceProvidedDTO servicesProvided = new ServiceProvidedDTO("ANC 1", "2013-02-01", new HashMap<String, String>());
        ANCClient ancClient = getClient().withAlerts(new ArrayList<AlertDTO>())
                .withServicesProvided(asList(servicesProvided));

        ANCClient preprocessedClients = ancClient.withPreProcess();

        Visits emptyVisit = new Visits();
        Visits visits = new Visits();
        visits.provided = servicesProvided;
        Map<String, Visits> serviceToVisitsMap = EasyMap.create("tt", emptyVisit).put("pnc", emptyVisit).put("ifa", emptyVisit).put("delivery_plan", emptyVisit).put("anc", visits).put("hb", emptyVisit).map();

        assertEquals(preprocessedClients.serviceToVisitsMap(), serviceToVisitsMap);
    }
}