package org.smartregister.util;

import org.ei.drishti.dto.Action;
import org.ei.drishti.dto.AlertStatus;
import org.ei.drishti.dto.BeneficiaryType;
import org.joda.time.DateTime;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.service.DrishtiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.drishti.dto.ActionData.createAlert;

public class FakeDrishtiService extends DrishtiService {
    private List<Expectation> expectations;
    private Response<List<Action>> defaultActions;

    public FakeDrishtiService(String defaultSuffix) {
        super(null, null);
        setSuffix(defaultSuffix);
        this.expectations = new ArrayList<Expectation>();
    }

    @Override
    public Response<List<Action>> fetchNewActions(String anmIdentifier, String previouslyFetchedIndex) {
        for (Expectation expectation : expectations) {
            if (expectation.matches(anmIdentifier, previouslyFetchedIndex)) {
                return expectation.alertActions();
            }
        }
        return defaultActions;
    }

    public void setSuffix(String suffix) {
        this.defaultActions = actionsFor(suffix);
    }

    public Response<List<Action>> actionsFor(String suffix) {
        Action deleteXAction = new Action("Case X", "alert", "deleteAllAlerts", new HashMap<String, String>(), "123456", true, new HashMap<String, String>());
        Action deleteYAction = new Action("Case Y", "alert", "deleteAllAlerts", new HashMap<String, String>(), "123456", true, new HashMap<String, String>());
        Action firstAction = new Action("Case X", "alert", "createAlert", dataForCreateAction("BCG", "BCG", "2012-01-01"), "123456", true, new HashMap<String, String>());
        Action secondAction = new Action("Case Y", "alert", "createAlert", dataForCreateAction("OPV", "OPV 1", "2100-04-09"), "123456", true, new HashMap<String, String>());

        return new Response<List<Action>>(ResponseStatus.success, new ArrayList<Action>(Arrays.asList(deleteXAction, deleteYAction, firstAction, secondAction)));
    }

    private static Map<String, String> dataForCreateAction(String scheduleName, String visitCode, String dueDate) {
        return createAlert(BeneficiaryType.mother, scheduleName, visitCode, AlertStatus.normal, new DateTime(dueDate), new DateTime("2012-01-11")).data();
    }

    private class Expectation {
        private String anmId;
        private String previousIndex;
        private Response<List<Action>> alertsToProvide;

        public Expectation(String anmId, String previousIndex, Response<List<Action>> alertsToProvide) {
            this.anmId = anmId;
            this.previousIndex = previousIndex;
            this.alertsToProvide = alertsToProvide;
        }

        public Response<List<Action>> alertActions() {
            return alertsToProvide;
        }

        public boolean matches(String anmIdentifier, String previouslyFetchedIndex) {
            return anmId.equals(anmIdentifier) && previousIndex.equals(previouslyFetchedIndex);
        }
    }
}
