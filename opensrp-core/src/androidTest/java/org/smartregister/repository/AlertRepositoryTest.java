package org.smartregister.repository;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.joda.time.LocalDate;
import org.smartregister.domain.Alert;
import org.smartregister.util.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.smartregister.domain.AlertStatus.complete;
import static org.smartregister.domain.AlertStatus.inProcess;
import static org.smartregister.domain.AlertStatus.normal;
import static org.smartregister.domain.AlertStatus.urgent;

public class AlertRepositoryTest extends AndroidTestCase {
    private AlertRepository alertRepository;

    @Override
    protected void setUp() throws Exception {
        alertRepository = new AlertRepository();
        Session session = new Session().setPassword("password").setRepositoryName("drishti.db" + new Date().getTime());
        new Repository(new RenamingDelegatingContext(getContext(), "test_"), session, alertRepository);
        alertRepository.deleteAllAlerts();
    }

    public void testShouldSaveAnAlert() throws Exception {
        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11"));

        List<Alert> alerts = alertRepository.allAlerts();
        assertEquals(asList(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11")), alerts);
    }

    public void testShouldUpdateAnExistingAlertInTheRepositoryOnlyIfThePriorityChanges() throws Exception {
        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11"));

        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11"));
        assertEquals(asList(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11")), alertRepository.allAlerts());

        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", urgent, "2012-01-01", "2012-01-11"));
        assertEquals(asList(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", urgent, "2012-01-01", "2012-01-11")), alertRepository.allAlerts());

        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11"));
        assertEquals(asList(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11")), alertRepository.allAlerts());
    }

    public void testShouldUpdateAnExistingAlertInTheRepositoryWhenNewAlertComesForAGivenSchedule() throws Exception {
        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11"));

        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 2", normal, "2012-01-01", "2012-01-11"));

        assertEquals(asList(new Alert("Case X", "Ante Natal Care - Normal", "ANC 2", normal, "2012-01-01", "2012-01-11")), alertRepository.allAlerts());
    }

    public void testShouldFetchAllAlerts() throws Exception {
        Alert alert1 = new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11");
        Alert alert2 = new Alert("Case Y", "Ante Natal Care - Normal", "ANC 2", complete, "2012-01-01", "2012-01-11");
        Alert alert3 = new Alert("Case X", "TT", "TT 1", normal, "2012-01-01", "2012-01-11");
        Alert alert4 = new Alert("Case Y", "IFA", "IFA 1", complete, "2012-01-01", "2012-01-11");

        alertRepository.createAlert(alert1);
        alertRepository.createAlert(alert2);
        alertRepository.createAlert(alert3);
        alertRepository.createAlert(alert4);

        assertEquals(asList(alert1, alert2, alert3, alert4), alertRepository.allAlerts());
    }

    public void testShouldFetchNonExpiredAlertsAndAlertsWithRecentCompletionAsActiveAlerts() throws Exception {
        LocalDate today = LocalDate.now();
        Alert alert1 = new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11");
        Alert alert2 = new Alert("Case X", "Ante Natal Care - Normal", "ANC 2", complete, "2012-01-01", "2012-01-11").withCompletionDate(today.toString());
        Alert alert3 = new Alert("Case X", "TT", "TT 1", normal, "2012-01-01", today.plusDays(30).toString());
        Alert alert4 = new Alert("Case X", "IFA", "IFA 1", complete, "2012-01-01", "2012-01-11").withCompletionDate(today.minusDays(2).toString());
        Alert alert5 = new Alert("Case X", "HEP", "HEP 0", complete, "2012-01-01", "2012-01-11").withCompletionDate(today.minusDays(3).toString());
        alertRepository.createAlert(alert1);
        alertRepository.createAlert(alert2);
        alertRepository.createAlert(alert3);
        alertRepository.createAlert(alert4);
        alertRepository.createAlert(alert5);

        List<Alert> activeAlerts = alertRepository.allActiveAlertsForCase("Case X");

        assertEquals(asList(alert2, alert3, alert4), activeAlerts);
    }

    public void testShouldMarkAlertsAsCompletedBasedOnCaseIDAndVisitCode() throws Exception {
        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11"));
        alertRepository.createAlert(new Alert("Case Y", "Ante Natal Care - Normal", "ANC 2", normal, "2012-01-01", "2012-01-11"));

        alertRepository.markAlertAsClosed("Case X", "ANC 1", "2012-01-01");

        assertEquals(asList(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", complete, "2012-01-01", "2012-01-11").withCompletionDate("2012-01-01"),
                new Alert("Case Y", "Ante Natal Care - Normal", "ANC 2", normal, "2012-01-01", "2012-01-11")), alertRepository.allAlerts());
    }

    public void testShouldNotFailClosingAlertWhenNoAlertExists() throws Exception {
        alertRepository.markAlertAsClosed("Case X", "ANC 1", "2012-01-01");

        assertTrue(alertRepository.allAlerts().isEmpty());
    }

    public void testShouldDeleteAllAlertsForACase() throws Exception {
        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11"));
        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 2", normal, "2012-01-01", "2012-01-11"));
        alertRepository.createAlert(new Alert("Case Y", "Ante Natal Care - Normal", "ANC 2", normal, "2012-01-01", "2012-01-11"));
        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 3", normal, "2012-01-01", "2012-01-11"));

        alertRepository.deleteAllAlertsForEntity("Case X");

        assertEquals(asList(new Alert("Case Y", "Ante Natal Care - Normal", "ANC 2", normal, "2012-01-01", "2012-01-11")), alertRepository.allAlerts());
    }

    public void testShouldDeleteAllAlerts() throws Exception {
        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11"));
        alertRepository.deleteAllAlerts();
        assertEquals(new ArrayList<Alert>(), alertRepository.allAlerts());
    }

    public void testShouldFindByEntityIdAndAlertNames() throws Exception {
        Alert ocpRefillAlert = new Alert("entity id 1", "OCP Refill", "OCP Refill", normal, "2012-01-02", "2012-01-11");
        Alert condomRefillAlert = new Alert("entity id 1", "Condom Refill", "Condom Refill", normal, "2012-01-01", "2012-01-11");
        Alert completedAlert = new Alert("entity id 1", "DMPA Injectable Refill", "DMPA Injectable Refill", complete, "2012-01-01", "2012-01-11");
        Alert ocpRefillAlertForAnotherEntity = new Alert("entity id 2", "OCP Refill", "OCP Refill", normal, "2012-01-01", "2012-01-11");
        Alert notOCPRefillAlert = new Alert("entity id 1", "Not OCP Refill", "Not OCP Refill", normal, "2012-01-01", "2012-01-11");
        alertRepository.createAlert(ocpRefillAlert);
        alertRepository.createAlert(condomRefillAlert);
        alertRepository.createAlert(completedAlert);
        alertRepository.createAlert(ocpRefillAlertForAnotherEntity);
        alertRepository.createAlert(notOCPRefillAlert);

        List<Alert> alerts = alertRepository.findByEntityIdAndAlertNames("entity id 1", "OCP Refill", "Condom Refill", "DMPA Injectable Refill");

        assertEquals(asList(condomRefillAlert, completedAlert, ocpRefillAlert), alerts);
    }

    public void testShouldChangeAlertStatusToInProcessBasedOnEntityIdAndVisitCode() throws Exception {
        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11"));
        alertRepository.createAlert(new Alert("Case Y", "Ante Natal Care - Normal", "ANC 2", urgent, "2012-01-01", "2012-01-11"));

        alertRepository.changeAlertStatusToInProcess("Case X", "ANC 1");

        assertEquals(asList(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", inProcess, "2012-01-01", "2012-01-11"),
                new Alert("Case Y", "Ante Natal Care - Normal", "ANC 2", urgent, "2012-01-01", "2012-01-11")), alertRepository.allAlerts());
    }

    public void testShouldNotFailChangingAlertStatusWhenNoAlertExists() throws Exception {
        alertRepository.changeAlertStatusToInProcess("Non existent alert", "ANC 1");

        assertTrue(alertRepository.allAlerts().isEmpty());
    }
}
