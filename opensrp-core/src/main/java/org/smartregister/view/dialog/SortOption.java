package org.smartregister.view.dialog;

import org.smartregister.view.contract.SmartRegisterClients;

public interface SortOption extends DialogOption {
    SmartRegisterClients sort(SmartRegisterClients allClients);
}
