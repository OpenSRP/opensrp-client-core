package org.smartregister.repository;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.smartregister.domain.Alert;
import org.smartregister.util.Session;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.smartregister.domain.AlertStatus.normal;

public class RepositoryTest extends AndroidTestCase {
    public void testShouldCheckPassword() throws Exception {
        AlertRepository alertRepository = new AlertRepository();
        Session session = new Session().setPassword("password").setRepositoryName("drishti.db" + new Date().getTime());
        Repository repository = new Repository(new RenamingDelegatingContext(getContext(), "test_"), session, alertRepository);

        List<Alert> makeCallJustToInitializeRepository = alertRepository.allAlerts();

        assertTrue(repository.canUseThisPassword("password"));
        assertFalse(repository.canUseThisPassword("SOMETHING-ELSE"));
    }

    public void testShouldDeleteDatabaseCompletely() throws Exception {
        AlertRepository alertRepository = new AlertRepository();
        Session session = new Session().setPassword("password").setRepositoryName("drishti.db" + new Date().getTime());
        Repository repository = new Repository(new RenamingDelegatingContext(getContext(), "test_"), session, alertRepository);

        alertRepository.createAlert(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11"));
        List<Alert> alerts = alertRepository.allAlerts();
        assertTrue(repository.canUseThisPassword("password"));
        assertEquals(alerts, asList(new Alert("Case X", "Ante Natal Care - Normal", "ANC 1", normal, "2012-01-01", "2012-01-11")));

        repository.deleteRepository();

        alerts = alertRepository.allAlerts();
        assertTrue(alerts.isEmpty());
        assertTrue(repository.canUseThisPassword("password"));
    }
}
