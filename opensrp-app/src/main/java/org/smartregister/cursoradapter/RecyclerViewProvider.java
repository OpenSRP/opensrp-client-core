package org.smartregister.cursoradapter;

import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

/**
 * Created by keyman on 09/07/18.
 */
public interface RecyclerViewProvider<V extends RecyclerView.ViewHolder> {
    void getView(Cursor cursor, SmartRegisterClient client, V viewHolder);

    void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalCount, boolean hasNextPage, boolean hasPreviousPage);

    SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption);

    void onServiceModeSelected(ServiceModeOption serviceModeOption);

    OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData);

    LayoutInflater inflater();

    V createViewHolder(ViewGroup parent);

    RecyclerView.ViewHolder createFooterHolder(ViewGroup parent);

    boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder);
}
