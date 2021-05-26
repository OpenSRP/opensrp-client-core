package org.smartregister.view.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.FadingCircle;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.R;
import org.smartregister.cursoradapter.RecyclerViewFragment;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.ResponseErrorStatus;
import org.smartregister.job.SyncSettingsServiceJob;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.NetworkUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.BaseRegisterFragmentContract;
import org.smartregister.view.dialog.DialogOption;

import java.util.HashMap;

import timber.log.Timber;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by keyman on 26/06/2018.
 */

public abstract class BaseRegisterFragment extends RecyclerViewFragment implements BaseRegisterFragmentContract.View,
        SyncStatusBroadcastReceiver.SyncStatusListener {

    private static final String TAG = BaseRegisterFragment.class.getCanonicalName();
    public static String TOOLBAR_TITLE = BaseRegisterActivity.class.getPackage() + ".toolbarTitle";

    protected RegisterActionHandler registerActionHandler = new RegisterActionHandler();
    protected BaseRegisterFragmentContract.Presenter presenter;
    protected View rootView;
    protected TextView headerTextDisplay;
    protected TextView filterStatus;
    protected RelativeLayout filterRelativeLayout;
    protected View.OnKeyListener hideKeyboard = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                Utils.hideKeyboard(getActivity(), v);
                return true;
            }
            return false;
        }
    };
    protected ImageView qrCodeScanImageView;
    protected ProgressBar syncProgressBar;
    protected ImageView syncButton;
    protected boolean globalQrSearch = false;
    protected final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            //Overriden Do something before Text Changed
        }

        @Override
        public void onTextChanged(final CharSequence cs, int start, int before, int count) {
            filter(cs.toString(), "", getMainCondition(), false);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //Overriden Do something after Text Changed
        }
    };

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {

        return null;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                };
            }

            @Override
            public String searchHint() {
                return context().getStringResource(R.string.search_hint);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        rootView = view;//handle to the root
        setUpActionBar();
        setupViews(view);
        return view;
    }

    protected void setUpActionBar() {
        if (getActivity() instanceof AppCompatActivity) {
            Toolbar toolbar = rootView.findViewById(R.id.register_toolbar);
            AppCompatActivity activity = ((AppCompatActivity) getActivity());

            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(TOOLBAR_TITLE));
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            activity.getSupportActionBar().setLogo(R.drawable.round_white_background);
            activity.getSupportActionBar().setDisplayUseLogoEnabled(false);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }


    @LayoutRes
    protected int getLayout() {
        return R.layout.fragment_base_register;
    }

    protected abstract void initializePresenter();

    protected void updateSearchView() {
        if (getSearchView() != null) {
            getSearchView().removeTextChangedListener(textWatcher);
            getSearchView().addTextChangedListener(textWatcher);
            getSearchView().setOnKeyListener(hideKeyboard);
        }
    }

    @Override
    public void updateSearchBarHint(String searchBarText) {
        if (getSearchView() != null) {
            getSearchView().setHint(searchBarText);
        }
    }

    public void setSearchTerm(String searchText) {
        if (getSearchView() != null) {
            getSearchView().setText(searchText);
        }
    }

    public void onQRCodeSucessfullyScanned(String qrCode) {
        Timber.i("QR code: %s", qrCode);
        if (StringUtils.isNotBlank(qrCode)) {
            filter(qrCode.replace("-", ""), "", getMainCondition(), true);
            setUniqueID(qrCode);
        }
    }

    public abstract void setUniqueID(String qrCode);

    public abstract void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData);

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);

        presenter.processViewConfigurations();
        presenter.initializeQueries(getMainCondition());
        updateSearchView();
        setServiceModeViewDrawableRight(null);
        attachQrCode(view);
        attachSyncButton(view);
        attachTopLeftLayout(view);
        attachProgressBar(view);

        // Sort and Filter
        headerTextDisplay = view.findViewById(R.id.header_text_display);
        filterStatus = view.findViewById(R.id.filter_status);
        filterRelativeLayout = view.findViewById(R.id.filter_display_view);
    }

    protected void attachTopLeftLayout(View view) {
        View topLeftLayout = view.findViewById(R.id.top_left_layout);
        if (topLeftLayout != null) {
            topLeftLayout.setOnClickListener(v -> qrCodeScanImageView.performLongClick());
        }
    }

    protected void attachProgressBar(View view) {
        // Progress bar
        syncProgressBar = view.findViewById(R.id.sync_progress_bar);
        if (syncProgressBar != null) {
            FadingCircle circle = new FadingCircle();
            syncProgressBar.setIndeterminateDrawable(circle);
        }
    }

    protected void attachSyncButton(View view) {
        //Sync
        syncButton = view.findViewById(R.id.sync_refresh);
        if (syncButton != null) {
            syncButton.setOnClickListener(view1 -> SyncSettingsServiceJob.scheduleJobImmediately(SyncSettingsServiceJob.TAG));
        }
    }

    protected void attachQrCode(View view) {
        // QR Code
        qrCodeScanImageView = view.findViewById(R.id.scanQrCode);
        if (qrCodeScanImageView != null) {
            qrCodeScanImageView.setOnClickListener(v -> {
                BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) getActivity();
                if (baseRegisterActivity != null) {
                    baseRegisterActivity.startQrCodeScanner();
                }
            });
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        renderView();
    }

    @VisibleForTesting
    protected void renderView() {
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            presenter.initializeQueries(getMainCondition());
        }
        updateSearchView();
        presenter.processViewConfigurations();
        // updateLocationText();
        refreshSyncProgressSpinner();
        setTotalPatients();
    }

    @Override
    public void setTotalPatients() {
        if (headerTextDisplay != null) {
            headerTextDisplay.setText(clientAdapter.getTotalcount() > 1 ?
                    String.format(getActivity().getString(R.string.clients), clientAdapter.getTotalcount()) :
                    String.format(getActivity().getString(R.string.client), clientAdapter.getTotalcount()));

            filterRelativeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void initializeQueryParams(String tableName, String countSelect, String mainSelect) {
        this.tablename = tableName;
        this.mainCondition = getMainCondition();
        this.countSelect = countSelect;
        this.mainSelect = mainSelect;
        this.Sortqueries = getDefaultSortQuery();
    }

    protected abstract String getMainCondition();

    protected abstract String getDefaultSortQuery();

    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        getSearchCancelView().setVisibility(isEmpty(filterString) ? View.INVISIBLE : View.VISIBLE);
        if (isEmpty(filterString)) {
            Utils.hideKeyboard(getActivity());
        }

        this.filters = filterString;
        this.joinTable = joinTableString;
        this.mainCondition = mainConditionString;

        countExecute();

        if (qrCode && StringUtils.isNotBlank(filterString) && clientAdapter.getTotalcount() == 0 && NetworkUtils.isNetworkAvailable()) {
            globalQrSearch = true;
            presenter.searchGlobally(filterString);
        } else {
            filterandSortExecute();
        }

        setTotalPatients();

    }

    @Override
    public void updateFilterAndFilterStatus(String filterText, String sortText) {
        if (headerTextDisplay != null) {
            headerTextDisplay.setText(Html.fromHtml(filterText));
            filterRelativeLayout.setVisibility(View.VISIBLE);
        }

        if (filterStatus != null) {
            filterStatus.setText(Html.fromHtml(clientAdapter.getTotalcount() + " patients " + sortText));
        }
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {//Implement Abstract Method
    }

    protected abstract void startRegistration();

    @Override
    protected void onCreation() {
        initializePresenter();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            boolean isRemote = extras.getBoolean(AllConstants.INTENT_KEY.IS_REMOTE_LOGIN); //TODO PROVIDE RIGHT CONSTANT
            if (isRemote) {
                presenter.startSync();
            }
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    /*protected void updateLocationText() {
        if (facilitySelection != null) {
            facilitySelection.setText(LocationHelper.getInstance().getOpenMrsReadableName(
                    facilitySelection.getSelectedItem()));
            String locationId = LocationHelper.getInstance().getOpenMrsLocationId(facilitySelection.getSelectedItem());
            context().allSharedPreferences().savePreference(Constants.CURRENT_LOCATION_ID, locationId);

        }
    }

    public LocationPickerView getFacilitySelection() {
        return facilitySelection;
    }*/

    protected abstract void onViewClicked(View view);

    private void registerSyncStatusBroadcastReceiver() {
        SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);
    }

    private void unregisterSyncStatusBroadcastReceiver() {
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(this);
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        refreshSyncStatusViews(fetchStatus);
    }

    @Override
    public void onSyncStart() {
        refreshSyncStatusViews(null);
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        refreshSyncStatusViews(fetchStatus);
    }

    @VisibleForTesting
    protected void showShortToast(Context context, String message) {
        Utils.showShortToast(context, message);
    }

    @VisibleForTesting
    protected void refreshSyncStatusViews(FetchStatus fetchStatus) {
        if (isSyncing()) {
            showShortToast(getActivity(), getActivity().getString(R.string.syncing));
            Timber.i(getActivity().getString(R.string.syncing));
            refreshSyncProgressSpinner();
        } else {
            if (fetchStatus != null) {
                if (fetchStatus.equals(FetchStatus.fetchedFailed)) {
                    if (fetchStatus.displayValue().equals(ResponseErrorStatus.malformed_url.name())) {
                        showShortToast(getActivity(), getActivity().getString(R.string.sync_failed_malformed_url));
                        Timber.i(getActivity().getString(R.string.sync_failed_malformed_url));
                    } else if (fetchStatus.displayValue().equals(ResponseErrorStatus.timeout.name())) {
                        showShortToast(getActivity(), getActivity().getString(R.string.sync_failed_timeout_error));
                        Timber.i(getActivity().getString(R.string.sync_failed_timeout_error));
                    } else {
                        showShortToast(getActivity(), getActivity().getString(R.string.sync_failed));
                        Timber.i(getActivity().getString(R.string.sync_failed));
                    }
                    refreshSyncProgressSpinner();
                } else if (fetchStatus.equals(FetchStatus.fetched) || fetchStatus.equals(FetchStatus.nothingFetched)) {
                    setRefreshList(true);
                    renderView();

                    showShortToast(getActivity(), getActivity().getString(R.string.sync_complete));
                    Timber.i(getActivity().getString(R.string.sync_complete));
                } else if (fetchStatus.equals(FetchStatus.noConnection)) {

                    showShortToast(getActivity(), getActivity().getString(R.string.sync_failed_no_internet));
                    Timber.i(getActivity().getString(R.string.sync_failed_no_internet));
                    refreshSyncProgressSpinner();
                } else {
                    refreshSyncProgressSpinner();
                }
            } else {
                Timber.i("Fetch Status NULL");
                refreshSyncProgressSpinner();
            }
        }
    }

    @VisibleForTesting
    protected boolean isSyncing() {
        return SyncStatusBroadcastReceiver.getInstance().isSyncing();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSyncStatusBroadcastReceiver();
    }

    @Override
    public void onPause() {
        unregisterSyncStatusBroadcastReceiver();
        super.onPause();
    }

    @VisibleForTesting
    protected void refreshSyncProgressSpinner() {
        if (isSyncing()) {
            if (syncProgressBar != null) {
                syncProgressBar.setVisibility(View.VISIBLE);
            }
            if (syncButton != null) {
                syncButton.setVisibility(View.GONE);
            }
        } else {
            if (syncProgressBar != null) {
                syncProgressBar.setVisibility(View.GONE);
            }
            if (syncButton != null) {
                syncButton.setVisibility(View.VISIBLE);
            }
        }
    }


    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class RegisterActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            onViewClicked(view);
        }
    }
}



