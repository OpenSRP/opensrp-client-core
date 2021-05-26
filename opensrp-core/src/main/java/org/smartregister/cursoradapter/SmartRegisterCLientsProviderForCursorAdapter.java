package org.smartregister.cursoradapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;

import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

/**
 * Created by raihan on 3/9/16.
 */
public interface SmartRegisterCLientsProviderForCursorAdapter {
    void getView(Cursor cursor, SmartRegisterClient client, View view);

    SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption);

    void onServiceModeSelected(ServiceModeOption serviceModeOption);

    OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData);

    LayoutInflater inflater();

    View inflatelayoutForCursorAdapter();
}
