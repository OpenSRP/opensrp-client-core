package org.smartregister.view.dialog;

import org.smartregister.provider.SmartRegisterClientsProvider;

import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public abstract class ServiceModeOption implements DialogOption {

    private SmartRegisterClientsProvider clientsProvider;

    public ServiceModeOption(SmartRegisterClientsProvider clientsProvider) {
        this.clientsProvider = clientsProvider;
    }

    public void apply() {
        clientsProvider.onServiceModeSelected(this);
    }

    public SmartRegisterClientsProvider provider() {
        return clientsProvider;
    }

    public abstract ClientsHeaderProvider getHeaderProvider();


}
