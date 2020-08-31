package org.smartregister.view.fragment;

import android.content.pm.ActivityInfo;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.VisibleForTesting;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.smartregister.R;
import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.domain.ReportMonth;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.util.PaginationHolder;
import org.smartregister.util.ViewHelper;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.dialog.AllClientsFilter;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.dialog.ECSearchOption;
import org.smartregister.view.dialog.EditOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.util.List;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.smartregister.AllConstants.SHORT_DATE_FORMAT;

/**
 * Created by koros on 10/12/15.
 */
public abstract class SecuredNativeSmartRegisterFragment extends SecuredFragment {

    public static final String DIALOG_TAG = "dialog";
    public static final List<? extends DialogOption> DEFAULT_FILTER_OPTIONS = asList(
            new AllClientsFilter());
    private final PaginationViewHandler paginationViewHandler = new PaginationViewHandler();
    private final NavBarActionsHandler navBarActionsHandler = new NavBarActionsHandler();
    private final SearchCancelHandler searchCancelHandler = new SearchCancelHandler();
    public ListView clientsView;
    public ProgressBar clientsProgressView;
    public TextView serviceModeView;
    public TextView appliedVillageFilterView;
    public TextView appliedSortView;
    public EditText searchView;
    public View searchCancelView;
    public TextView titleLabelView;
    public View mView;
    private SmartRegisterPaginatedAdapter clientsAdapter;
    private FilterOption currentVillageFilter;
    private SortOption currentSortOption;
    private FilterOption currentSearchFilter;
    private ServiceModeOption currentServiceModeOption;
    private boolean refreshList;

    public EditText getSearchView() {
        return searchView;
    }

    public View getSearchCancelView() {
        return searchCancelView;
    }

    public FilterOption getCurrentVillageFilter() {
        return currentVillageFilter;
    }

    public FilterOption getCurrentSearchFilter() {
        return currentSearchFilter;
    }

    public void setCurrentSearchFilter(FilterOption currentSearchFilter) {
        this.currentSearchFilter = currentSearchFilter;
    }

    public SortOption getCurrentSortOption() {
        return currentSortOption;
    }

    public ServiceModeOption getCurrentServiceModeOption() {
        return currentServiceModeOption;
    }

    public SmartRegisterPaginatedAdapter getClientsAdapter() {
        return clientsAdapter;
    }

    public void setClientsAdapter(SmartRegisterPaginatedAdapter clientsAdapter) {
        this.clientsAdapter = clientsAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getActivity().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View view = inflater.inflate(R.layout.smart_register_activity, container, false);
        mView = view;
        onInitialization();
        setupViews(view);
        onResumption();
        return view;
    }

    protected void setupViews(View view) {
        setupNavBarViews(view);
        populateClientListHeaderView(getDefaultOptionsProvider().serviceMode().getHeaderProvider(),
                view);

        clientsProgressView = view.findViewById(R.id.client_list_progress);
        clientsView = view.findViewById(R.id.list);

        setupStatusBarViews(view);
        paginationViewHandler.addPagination(clientsView);

        updateDefaultOptions();
    }

    public void refreshListView() {
        this.setRefreshList(true);
        this.onResumption();
        this.setRefreshList(false);
    }

    @Override
    protected void onResumption() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                publishProgress();
                setupAdapter();
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                clientsProgressView.setVisibility(VISIBLE);
                clientsView.setVisibility(INVISIBLE);
            }

            @Override
            protected void onPostExecute(Void result) {
                clientsView.setAdapter(clientsAdapter);
                if (isAdded()) {
                    paginationViewHandler.refresh();
                    clientsProgressView.setVisibility(View.GONE);
                    clientsView.setVisibility(VISIBLE);
                }

            }
        }.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    private void setupStatusBarViews(View view) {
        appliedSortView = view.findViewById(R.id.sorted_by);
        appliedVillageFilterView = view.findViewById(R.id.village);
    }

    private void setupNavBarViews(View view) {
        view.findViewById(R.id.btn_back_to_home).setOnClickListener(navBarActionsHandler);

        setupTitleView(view);

        View villageFilterView = view.findViewById(R.id.filter_selection);
        villageFilterView.setOnClickListener(navBarActionsHandler);

        View sortView = view.findViewById(R.id.sort_selection);
        sortView.setOnClickListener(navBarActionsHandler);

        serviceModeView = view.findViewById(R.id.service_mode_selection);
        serviceModeView.setOnClickListener(navBarActionsHandler);

        view.findViewById(R.id.register_client).setOnClickListener(navBarActionsHandler);

        setupSearchView(view);
    }

    protected void setServiceModeViewDrawableRight(Drawable drawable) {
        serviceModeView.setCompoundDrawables(null, null, drawable, null);
    }

    private void setupTitleView(View view) {
        ViewGroup titleLayout = view.findViewById(R.id.title_layout);
        titleLayout.setOnClickListener(navBarActionsHandler);

        titleLabelView = view.findViewById(R.id.txt_title_label);

        TextView reportMonthStartView = view.findViewById(R.id.btn_report_month);
        setReportDates(reportMonthStartView);
    }

    public void setupSearchView(View view) {
        searchView = view.findViewById(R.id.edt_search);
        searchView.setHint(getNavBarOptionsProvider().searchHint());
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                currentSearchFilter = new ECSearchOption(cs.toString());
                clientsAdapter.refreshList(currentVillageFilter, currentServiceModeOption,
                        currentSearchFilter, currentSortOption);

                searchCancelView.setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        searchCancelView = view.findViewById(R.id.btn_search_cancel);
        searchCancelView.setOnClickListener(searchCancelHandler);
    }

    private void setReportDates(TextView titleView) {
        ReportMonth report = new ReportMonth();
        titleView.setText(
                report.startOfCurrentReportMonth(LocalDate.now()).toString(SHORT_DATE_FORMAT)
                        + " - " + report.endOfCurrentReportMonth(LocalDate.now())
                        .toString(SHORT_DATE_FORMAT));
    }

    private void updateDefaultOptions() {
        currentSearchFilter = new ECSearchOption(null);
        currentVillageFilter = getDefaultOptionsProvider().villageFilter();
        currentServiceModeOption = getDefaultOptionsProvider().serviceMode();
        currentSortOption = getDefaultOptionsProvider().sortOption();

        appliedSortView.setText(currentSortOption.name());
        appliedVillageFilterView.setText(currentVillageFilter.name());
        serviceModeView.setText(currentServiceModeOption.name());
        titleLabelView.setText(getDefaultOptionsProvider().nameInShortFormForTitle());
    }

    private void populateClientListHeaderView(SecuredNativeSmartRegisterActivity
                                                      .ClientsHeaderProvider headerProvider, View
                                                      view) {
        LinearLayout clientsHeaderLayout = view
                .findViewById(R.id.clients_header_layout);
        clientsHeaderLayout.removeAllViewsInLayout();
        int columnCount = headerProvider.count();
        int[] weights = headerProvider.weights();
        int[] headerTxtResIds = headerProvider.headerTextResourceIds();
        clientsHeaderLayout.setWeightSum(headerProvider.weightSum());

        for (int i = 0; i < columnCount; i++) {
            clientsHeaderLayout.addView(getColumnHeaderView(i, weights, headerTxtResIds));
        }
    }

    private View getColumnHeaderView(int i, int[] weights, int[] headerTxtResIds) {
        CustomFontTextView header = getCustomFontTextViewHeader();
        header.setFontVariant(FontVariant.BLACK);
        header.setTextSize(16);
        header.setTextColor(getResources().getColor(R.color.client_list_header_text_color));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, weights[i]);

        header.setLayoutParams(lp);
        header.setText(headerTxtResIds[i]);
        return header;
    }

    protected CustomFontTextView getCustomFontTextViewHeader() {
        return new CustomFontTextView(getActivity(), null, R.style.CustomFontTextViewStyle_Header_Black);
    }

    private void setupAdapter() {
        clientsAdapter = adapter();
        clientsAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                paginationViewHandler.refresh();
            }
        });
    }

    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    protected void onServiceModeSelection(ServiceModeOption serviceModeOption, View view) {
        currentServiceModeOption = serviceModeOption;
        serviceModeView.setText(serviceModeOption.name());
        clientsAdapter
                .refreshList(currentVillageFilter, currentServiceModeOption, currentSearchFilter,
                        currentSortOption);

        populateClientListHeaderView(serviceModeOption.getHeaderProvider(), view);
    }

    public void onSortSelection(SortOption sortBy) {
        Log.v("he pressed this", sortBy.name());
        currentSortOption = sortBy;
        appliedSortView.setText(sortBy.name());
        clientsAdapter
                .refreshList(currentVillageFilter, currentServiceModeOption, currentSearchFilter,
                        currentSortOption);
    }

    public void onFilterSelection(FilterOption filter) {
        currentVillageFilter = filter;
        appliedVillageFilterView.setText(filter.name());
        clientsAdapter
                .refreshList(currentVillageFilter, currentServiceModeOption, currentSearchFilter,
                        currentSortOption);
    }

    protected void onEditSelection(EditOption editOption, SmartRegisterClient client) {
        editOption.doEdit(client);
    }

    private void goBack() {
        getActivity().finish();
    }

    void showFragmentDialog(DialogOptionModel dialogOptionModel) {
        showFragmentDialog(dialogOptionModel, null);
    }

    protected void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        ((SecuredNativeSmartRegisterActivity) getActivity())
                .showFragmentDialog(dialogOptionModel, tag);
    }

    protected abstract SecuredNativeSmartRegisterActivity.DefaultOptionsProvider
    getDefaultOptionsProvider();

    protected abstract SecuredNativeSmartRegisterActivity.NavBarOptionsProvider
    getNavBarOptionsProvider();

    protected abstract SmartRegisterClientsProvider clientsProvider();

    protected abstract void onInitialization();

    protected abstract void startRegistration();

    public void gotoNextPage() {
        clientsAdapter.nextPage();
        clientsAdapter.notifyDataSetChanged();
    }

    public void goBackToPreviousPage() {
        clientsAdapter.previousPage();
        clientsAdapter.notifyDataSetChanged();
    }

    public boolean isRefreshList() {
        return refreshList;
    }

    public void setRefreshList(boolean refreshList) {
        this.refreshList = refreshList;
    }

    private class FilterDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getNavBarOptionsProvider().filterOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onFilterSelection((FilterOption) option);
        }
    }

    private class SortDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getNavBarOptionsProvider().sortingOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onSortSelection((SortOption) option);
        }
    }

    protected class ServiceModeDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getNavBarOptionsProvider().serviceModeOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onServiceModeSelection((ServiceModeOption) option, mView);
        }
    }

    @VisibleForTesting
    protected class PaginationViewHandler implements View.OnClickListener {
        private Button nextPageView;
        private Button previousPageView;
        private TextView pageInfoView;

        private void addPagination(ListView clientsView) {

            PaginationHolder paginationHolder = ViewHelper.addPaginationCore(this, clientsView);
            nextPageView = paginationHolder.getNextPageView();
            previousPageView = paginationHolder.getPreviousPageView();
            pageInfoView = paginationHolder.getPageInfoView();
        }

        private int getCurrentPageCount() {
            return clientsAdapter.currentPage() + 1 > clientsAdapter.pageCount() ? clientsAdapter
                    .pageCount() : clientsAdapter.currentPage() + 1;
        }

        public void refresh() {
            pageInfoView.setText(format(getResources().getString(R.string.str_page_info),
                    (getCurrentPageCount()), (clientsAdapter.pageCount())));
            nextPageView.setVisibility(clientsAdapter.hasNextPage() ? VISIBLE : INVISIBLE);
            previousPageView.setVisibility(clientsAdapter.hasPreviousPage() ? VISIBLE : INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.btn_next_page) {
                gotoNextPage();

            } else if (i == R.id.btn_previous_page) {
                goBackToPreviousPage();

            }
        }

    }

    public class NavBarActionsHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.title_layout || i == R.id.btn_back_to_home) {
                goBack();

            } else if (i == R.id.register_client) {
                startRegistration();

            } else if (i == R.id.filter_selection) {
                showFragmentDialog(new FilterDialogOptionModel());

            } else if (i == R.id.sort_selection) {
                showFragmentDialog(new SortDialogOptionModel());

            } else if (i == R.id.service_mode_selection) {
                showFragmentDialog(new ServiceModeDialogOptionModel());
            }
        }
    }

    public class SearchCancelHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            clearSearchText();
        }

        private void clearSearchText() {
            searchView.setText("");
        }

    }
}