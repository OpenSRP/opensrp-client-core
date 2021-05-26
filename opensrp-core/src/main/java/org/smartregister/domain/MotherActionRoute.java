package org.smartregister.domain;

import org.ei.drishti.dto.Action;
import org.smartregister.CoreLibrary;
import org.smartregister.event.Event;

public enum MotherActionRoute {
    CLOSE("close") {
        @Override
        public void direct(Action action) {
            CoreLibrary.getInstance().context().motherService()
                    .close(action.caseID(), action.get("reasonForClose"));
            Event.ACTION_HANDLED.notifyListeners("Mother closed");
        }
    };

    private String identifier;

    MotherActionRoute(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }

    public abstract void direct(Action action);
}