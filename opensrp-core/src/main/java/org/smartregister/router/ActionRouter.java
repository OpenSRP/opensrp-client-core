package org.smartregister.router;

import org.ei.drishti.dto.Action;
import org.smartregister.domain.AlertActionRoute;
import org.smartregister.domain.MotherActionRoute;

import static org.smartregister.util.Log.logWarn;

public class ActionRouter {
    public void directAlertAction(Action action) {
        AlertActionRoute[] alertActionRoutes = AlertActionRoute.values();
        for (AlertActionRoute alertActionRoute : alertActionRoutes) {
            if (alertActionRoute.identifier().equals(action.type())) {
                alertActionRoute.direct(action);
                return;
            }
        }
        logWarn("Unknown type in Alert action: " + action);
    }

    public void directMotherAction(Action action) {
        MotherActionRoute[] motherActionRoutes = MotherActionRoute.values();
        for (MotherActionRoute motherActionRoute : motherActionRoutes) {
            if (motherActionRoute.identifier().equals(action.type())) {
                motherActionRoute.direct(action);
                return;
            }
        }
        logWarn("Unknown type in Mother action: " + action);
    }
}
