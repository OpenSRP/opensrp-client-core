package org.smartregister.view.contract;

import junit.framework.Assert;

import org.junit.Test;
import org.smartregister.util.EasyMap;
import org.smartregister.view.contract.pnc.PNCClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PNCClientTest {
    @Test
    public void shouldAddPNCAlertToServiceToVisitMap() throws Exception {
        AlertDTO pncAlert = new AlertDTO("PNC", "urgent", "2013-02-01");
        PNCClient pncClient = new PNCClient("entityId",
                "village",
                "name",
                "thayi",
                "2013-01-30"
        ).withAlerts(Arrays.asList(pncAlert)).withServicesProvided(new ArrayList<ServiceProvidedDTO>());

        PNCClient preprocessedClients = pncClient.withPreProcess();

        Visits visits = new Visits();
        visits.toProvide = pncAlert;
        Map<String, Visits> serviceToVisitsMap = EasyMap.create("pnc", visits).map();

        Assert.assertEquals(preprocessedClients.serviceToVisitsMap(), serviceToVisitsMap);

    }

    @Test
    public void shouldAddPNCServiceProvidedToServiceToVisitMap() throws Exception {
        ServiceProvidedDTO servicesProvided = new ServiceProvidedDTO("PNC",
                "2013-02-01",
                new HashMap<String, String>());
        PNCClient pncClient = new PNCClient("entityId",
                "village",
                "name",
                "thayi",
                "2013-01-30").withAlerts(new ArrayList<AlertDTO>())
                .withServicesProvided(Arrays.asList(
                        servicesProvided));

        PNCClient preprocessedClients = pncClient.withPreProcess();

        Visits visits = new Visits();
        visits.provided = servicesProvided;
        Map<String, Visits> serviceToVisitsMap = EasyMap.create("pnc", visits).map();

        Assert.assertEquals(preprocessedClients.serviceToVisitsMap(), serviceToVisitsMap);
    }

}