package org.smartregister.util;

import org.ei.drishti.dto.Action;
import org.ei.drishti.dto.ActionData;
import org.ei.drishti.dto.AlertStatus;
import org.ei.drishti.dto.BeneficiaryType;
import org.joda.time.DateTime;

import java.util.HashMap;

import static org.ei.drishti.dto.ActionData.createAlert;
import static org.ei.drishti.dto.ActionData.markAlertAsClosed;

public class ActionBuilder {
    public static Action actionForCreateAlert(String caseID, String alertStatus, String beneficiaryType, String scheduleName, String visitCode, String startDate, String expiryDate, String index) {
        return new Action(caseID, "alert", "createAlert", createAlert(BeneficiaryType.from(beneficiaryType), scheduleName, visitCode, AlertStatus.from(alertStatus), new DateTime(startDate), new DateTime(expiryDate)).data(), index, true, new HashMap<String, String>());
    }

    public static Action actionForCloseAlert(String caseID, String visitCode, String completionDate, String index) {
        return new Action(caseID, "alert", "closeAlert", markAlertAsClosed(visitCode, completionDate).data(), index, true, new HashMap<String, String>());
    }

    public static Action actionForCloseMother(String caseID) {
        return new Action(caseID, "mother", "close", ActionData.closeMother("close reason").data(), "0", true, new HashMap<String, String>());
    }

    public static Action actionForDeleteAllAlert(String caseID) {
        return new Action(caseID, "alert", "deleteAllAlerts", new HashMap<String, String>(), "0", true, new HashMap<String, String>());
    }

    public static Action actionForReport(String indicator, String annualTarget) {
        ActionData actionData = ActionData.reportForIndicator(indicator, annualTarget, "some-month-summary-json");
        return new Action("", "report", indicator, actionData.data(), "2012-01-01", true, new HashMap<String, String>());
    }
}
