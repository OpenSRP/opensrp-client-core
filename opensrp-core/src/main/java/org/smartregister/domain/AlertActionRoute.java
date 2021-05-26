package org.smartregister.domain;

import org.ei.drishti.dto.Action;
import org.smartregister.CoreLibrary;

public enum AlertActionRoute {
    CREATE_ALERT("createAlert") {
        @Override
        public void direct(Action action) {
            CoreLibrary.getInstance().context().alertService().create(action);
        }
    }, CLOSE_ALERT("closeAlert") {
        @Override
        public void direct(Action action) {
            CoreLibrary.getInstance().context().alertService().close(action);
        }
    }, DELETE_ALL_ALERTS("deleteAllAlerts") {
        @Override
        public void direct(Action action) {
            CoreLibrary.getInstance().context().alertService().deleteAll(action);
        }
    };

    private String identifier;

    AlertActionRoute(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }

    public abstract void direct(Action action);
}
