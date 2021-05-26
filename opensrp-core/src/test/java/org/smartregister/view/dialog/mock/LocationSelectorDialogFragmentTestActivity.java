package org.smartregister.view.dialog.mock;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.smartregister.R;
import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.dialog.EditOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class LocationSelectorDialogFragmentTestActivity extends SecuredNativeSmartRegisterActivity implements LocationSelectorDialogFragment.OnLocationSelectedListener {
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
    protected void onCreation() {
        super.onCreation();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
    }

    @Override
    protected void setupViews() {
        super.setupViews();
    }

    @Override
    protected void setServiceModeViewDrawableRight(Drawable drawable) {
        super.setServiceModeViewDrawableRight(drawable);
    }

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return super.adapter();
    }

    @Override
    protected void onServiceModeSelection(ServiceModeOption serviceModeOption) {
        super.onServiceModeSelection(serviceModeOption);
    }

    @Override
    protected void onSortSelection(SortOption sortBy) {
        super.onSortSelection(sortBy);
    }

    @Override
    protected void onFilterSelection(FilterOption filter) {
        super.onFilterSelection(filter);
    }

    @Override
    protected void onEditSelection(EditOption editOption, SmartRegisterClient client) {
        super.onEditSelection(editOption, client);
    }

    @Override
    protected void onEditSelectionWithMetadata(EditOption editOption, SmartRegisterClient client, String metadata) {
        super.onEditSelectionWithMetadata(editOption, client, metadata);
    }

    @Override
    public void showFragmentDialog(DialogOptionModel dialogOptionModel) {
        super.showFragmentDialog(dialogOptionModel);
    }

    @Override
    public void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        super.showFragmentDialog(dialogOptionModel, tag);
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
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
    public void startRegistration() {

    }

    @Override
    public void saveFormSubmission(String formSubmision, String id, String formName, JSONObject fieldOverrides) {
        super.saveFormSubmission(formSubmision, id, formName, fieldOverrides);
    }

    @Override
    protected String getParams(FormSubmission submission) {
        return super.getParams(submission);
    }

    @Override
    public void savePartialFormData(String formData, String id, String formName, JSONObject fieldOverrides) {
        super.savePartialFormData(formData, id, formName, fieldOverrides);
    }

    @Override
    public String getPreviouslySavedDataForForm(String formName, String overridesStr, String id) {
        return super.getPreviouslySavedDataForForm(formName, overridesStr, id);
    }

    private LinearLayout linearLayout;
    private LocationSelectorDialogFragment fragment;

    @Override
    public void onCreate(Bundle bundle) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(bundle);
        linearLayout = new LinearLayout(this);
        setContentView(linearLayout);
        startFragment();
    }

    public void startFragment() {

        fragment = LocationSelectorDialogFragment.newInstance(this, DialogOptionModalMock.getDialogOptionModal(), "", "");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();
    }

    @Override
    public void OnLocationSelected(String locationSelected) {

    }
}
