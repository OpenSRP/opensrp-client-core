package org.smartregister.view.fragment.mock;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.dialog.EditOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

/**
 * Created by kaderchowdhury on 14/11/17.
 */

public class MockFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

    @Override
    protected void onCreation() {

    }

    @Override
    public EditText getSearchView() {
        return super.getSearchView();
    }

    @Override
    public View getSearchCancelView() {
        return super.getSearchCancelView();
    }

    @Override
    public FilterOption getCurrentVillageFilter() {
        return super.getCurrentVillageFilter();
    }

    @Override
    public FilterOption getCurrentSearchFilter() {
        return super.getCurrentSearchFilter();
    }

    @Override
    public void setCurrentSearchFilter(FilterOption currentSearchFilter) {
        super.setCurrentSearchFilter(currentSearchFilter);
    }

    @Override
    public SortOption getCurrentSortOption() {
        return super.getCurrentSortOption();
    }

    @Override
    public ServiceModeOption getCurrentServiceModeOption() {
        return super.getCurrentServiceModeOption();
    }

    @Override
    public SmartRegisterPaginatedAdapter getClientsAdapter() {
        return super.getClientsAdapter();
    }

    @Override
    public void setClientsAdapter(SmartRegisterPaginatedAdapter clientsAdapter) {
        super.setClientsAdapter(clientsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setupViews(View view) {
        super.setupViews(view);
    }

    @Override
    public void refreshListView() {
        super.refreshListView();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
    }

    @Override
    protected void setServiceModeViewDrawableRight(Drawable drawable) {
        super.setServiceModeViewDrawableRight(drawable);
    }

    @Override
    public void setupSearchView(View view) {
        super.setupSearchView(view);
    }

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return super.adapter();
    }

    @Override
    protected void onServiceModeSelection(ServiceModeOption serviceModeOption, View view) {
        super.onServiceModeSelection(serviceModeOption, view);
    }

    @Override
    public void onSortSelection(SortOption sortBy) {
        super.onSortSelection(sortBy);
    }

    @Override
    public void onFilterSelection(FilterOption filter) {
        super.onFilterSelection(filter);
    }

    @Override
    protected void onEditSelection(EditOption editOption, SmartRegisterClient client) {
        super.onEditSelection(editOption, client);
    }

    @Override
    protected void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        super.showFragmentDialog(dialogOptionModel, tag);
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    protected void startRegistration() {

    }

    @Override
    public void gotoNextPage() {
        super.gotoNextPage();
    }

    @Override
    public void goBackToPreviousPage() {
        super.goBackToPreviousPage();
    }

    @Override
    public boolean isRefreshList() {
        return super.isRefreshList();
    }

    @Override
    public void setRefreshList(boolean refreshList) {
        super.setRefreshList(refreshList);
    }
}
