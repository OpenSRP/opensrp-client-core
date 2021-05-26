package org.smartregister.view.dialog;

import org.smartregister.view.contract.SmartRegisterClient;

public interface EditOption extends DialogOption {
    void doEdit(SmartRegisterClient client);

    void doEditWithMetadata(SmartRegisterClient client, String metadata);
}
