package org.smartregister.view.dialog;

import org.smartregister.view.contract.SmartRegisterClient;

public interface FilterOption extends DialogOption {
    boolean filter(SmartRegisterClient client);
}
