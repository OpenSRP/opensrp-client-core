package org.smartregister.provider;

import android.view.View;
import android.view.ViewGroup;

import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

public interface SmartRegisterClientsProvider {

    View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup);

    SmartRegisterClients getClients();

    SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption);

    void onServiceModeSelected(ServiceModeOption serviceModeOption);

    OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData);
}
