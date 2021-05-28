package org.smartregister.cursoradapter;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.smartregister.R;
import org.smartregister.commonregistry.CommonRepository;
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
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;

import java.util.List;

import timber.log.Timber;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.smartregister.AllConstants.SHORT_DATE_FORMAT;

/**
 * Created by koros on 10/12/15.
 */
public abstract class SecuredNativeSmartRegisterCursorAdapterFragment extends
        SecuredNativeSmartRegisterFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DIALOG_TAG = "dialog";
    public static final List<? extends DialogOption> DEFAULT_FILTER_OPTIONS = asList(
            new AllClientsFilter());
    protected static final int LOADER_ID = 0;
    private static final String INIT_LOADER = "init";
    public static int totalcount = 0;
    public static int currentlimit = 20;
    public static int currentoffset = 0;
    public final SearchCancelHandler searchCancelHandler = new SearchCancelHandler();
    private final PaginationViewHandler paginationViewHandler = new PaginationViewHandler();
    private final NavBarActionsHandler navBarActionsHandler = new NavBarActionsHandler();
    public String mainSelect;
    public String filters = "";
    public String mainCondition = "";
    public String Sortqueries;
    public String tablename;
    public String countSelect;
    public String joinTable = "";
    public SmartRegisterPaginatedCursorAdapter clientAdapter;
    public View mView;
    private String currentquery;
    private FilterOption currentVillageFilter;
    private SortOption currentSortOption;
    private FilterOption currentSearchFilter;
    private ServiceModeOption currentServiceModeOption;
    private TextView pageInfoView;
    private Button nextPageView;
    private Button previousPageView;

    private static final String COUNT = "count_execute";

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

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

    public SmartRegisterPaginatedCursorAdapter getClientsCursorAdapter() {
        return clientAdapter;
    }

    public void setClientsAdapter(SmartRegisterPaginatedCursorAdapter clientsAdapter) {
        this.clientAdapter = clientsAdapter;
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
        if (getDefaultOptionsProvider() != null) {
            populateClientListHeaderView(
                    getDefaultOptionsProvider().serviceMode().getHeaderProvider(), view);
        }

        clientsProgressView = view.findViewById(R.id.client_list_progress);
        clientsView = view.findViewById(R.id.list);

        setupStatusBarViews(view);
        paginationViewHandler.addPagination(clientsView);

        updateDefaultOptions();
    }

    public void refreshListView() {
        super.setRefreshList(true);
        this.onResumption();
        super.setRefreshList(false);
    }

    @Override
    protected void onResumption() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                publishProgress();
//                setupAdapter();
//                return null;
//            }
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                clientsProgressView.setVisibility(VISIBLE);
//                clientsView.setVisibility(INVISIBLE);
//            }
//
//            @Override
//            protected void onPostExecute(Void result) {
//                clientsView.setAdapter(clientsAdapter);
//                if(isAdded()) {
//                    paginationViewHandler.refresh();
//                    clientsProgressView.setVisibility(View.GONE);
//                    clientsView.setVisibility(VISIBLE);
//                }
//
//            }
//        }.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    private void setupStatusBarViews(View view) {
        appliedSortView = view.findViewById(R.id.sorted_by);
        appliedVillageFilterView = view.findViewById(R.id.village);
    }

    private void setupNavBarViews(View view) {
        View backButton = view.findViewById(R.id.btn_back_to_home);
        if (backButton != null) {
            backButton.setOnClickListener(navBarActionsHandler);
        }

        setupTitleView(view);

        View villageFilterView = view.findViewById(R.id.filter_selection);
        if (villageFilterView != null) {
            villageFilterView.setOnClickListener(navBarActionsHandler);
        }

        View sortView = view.findViewById(R.id.sort_selection);
        if (sortView != null) {
            sortView.setOnClickListener(navBarActionsHandler);
        }

        serviceModeView = view.findViewById(R.id.service_mode_selection);
        if (serviceModeView != null) {
            serviceModeView.setOnClickListener(navBarActionsHandler);
        }

        View registerClient = view.findViewById(R.id.register_client);
        if (registerClient != null) {
            registerClient.setOnClickListener(navBarActionsHandler);
        }

        setupSearchView(view);
    }

    protected void setServiceModeViewDrawableRight(Drawable drawable) {
        serviceModeView.setCompoundDrawables(null, null, drawable, null);
    }

    private void setupTitleView(View view) {
        ViewGroup titleLayout = view.findViewById(R.id.title_layout);
        if (titleLayout != null) {
            titleLayout.setOnClickListener(navBarActionsHandler);
        }

        titleLabelView = view.findViewById(R.id.txt_title_label);

        TextView reportMonthStartView = view.findViewById(R.id.btn_report_month);
        if (reportMonthStartView != null) {
            setReportDates(reportMonthStartView);
        }
    }

    public void setupSearchView(View view) {
        searchView = view.findViewById(R.id.edt_search);
        if (searchView != null) {
            if (getNavBarOptionsProvider() != null) {
                searchView.setHint(getNavBarOptionsProvider().searchHint());
            }
            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence cs, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }

        searchCancelView = view.findViewById(R.id.btn_search_cancel);
        if (searchCancelView != null) {
            searchCancelView.setOnClickListener(searchCancelHandler);
        }
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
        if (getDefaultOptionsProvider() != null) {
            currentVillageFilter = getDefaultOptionsProvider().villageFilter();
            currentServiceModeOption = getDefaultOptionsProvider().serviceMode();
            currentSortOption = getDefaultOptionsProvider().sortOption();

            appliedSortView.setText(currentSortOption.name());
            appliedVillageFilterView.setText(currentVillageFilter.name());
            serviceModeView.setText(currentServiceModeOption.name());
            titleLabelView.setText(getDefaultOptionsProvider().nameInShortFormForTitle());
        }
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
        CustomFontTextView header = new CustomFontTextView(getActivity(), null,
                R.style.CustomFontTextViewStyle_Header_Black);
        header.setFontVariant(FontVariant.BLACK);
        header.setTextSize(16);
        header.setTextColor(getResources().getColor(R.color.client_list_header_text_color));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, weights[i]);

        header.setLayoutParams(lp);
        header.setText(headerTxtResIds[i]);
        return header;
    }

    @Override
    public void onSortSelection(SortOption sortBy) {
        appliedSortView.setText(sortBy.name());
        Sortqueries = ((CursorSortOption) sortBy).sort();
        filterandSortExecute();
    }

    @Override
    public void onFilterSelection(FilterOption filter) {
        appliedVillageFilterView.setText(filter.name());
        filters = ((CursorFilterOption) filter).filter();

        filterandSortExecute(countBundle());
    }

    protected void onEditSelection(EditOption editOption, SmartRegisterClient client) {
        editOption.doEdit(client);
    }

    protected void goBack() {
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

    private int getCurrentPageCount() {
        if (currentoffset != 0) {
            if ((currentoffset / currentlimit) != 0) {
                return ((currentoffset / currentlimit) + 1);
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    private int getTotalcount() {
        if (totalcount % currentlimit == 0) {
            return (totalcount / currentlimit);
        } else {
            return ((totalcount / currentlimit) + 1);
        }
    }

    public void refresh() {
        pageInfoView.setText(getFormattedPaginationInfoText(getCurrentPageCount(), getTotalcount()));
        nextPageView.setVisibility(hasNextPage() ? VISIBLE : INVISIBLE);
        previousPageView.setVisibility(hasPreviousPage() ? VISIBLE : INVISIBLE);
    }

    private boolean hasNextPage() {

        return ((totalcount > (currentoffset + currentlimit)));
    }

    private boolean hasPreviousPage() {
        return currentoffset != 0;
    }

    public void gotoNextPage() {
        if (!(currentoffset + currentlimit > totalcount)) {
            currentoffset = currentoffset + currentlimit;
            filterandSortExecute();
        }
    }

    public void goBackToPreviousPage() {
        if (currentoffset > 0) {
            currentoffset = currentoffset - currentlimit;
            filterandSortExecute();
        }
    }

    public void filterandSortInInitializeQueries() {
        if (isPausedOrRefreshList()) {
            this.showProgressView();
            this.filterandSortExecute(countBundle());
        } else {
            this.initialFilterandSortExecute();
        }
    }

    public void initialFilterandSortExecute() {
        Loader<Cursor> loader = getLoaderManager().getLoader(LOADER_ID);
        showProgressView();
        if (loader != null) {
            filterandSortExecute(countBundle());
        } else {
            getLoaderManager().initLoader(LOADER_ID, countBundle(), this);
        }
    }

    public void filterandSortExecute(Bundle args) {
        refresh();

        getLoaderManager().restartLoader(LOADER_ID, args, this);
    }


    public void filterandSortExecute() {
        filterandSortExecute(null);
    }

    public void showProgressView() {
        if (clientsProgressView.getVisibility() == INVISIBLE) {
            clientsProgressView.setVisibility(View.VISIBLE);
        }

        if (clientsView.getVisibility() == VISIBLE) {
            clientsView.setVisibility(View.INVISIBLE);
        }
    }

    public void hideProgressView() {
        if (clientsProgressView.getVisibility() == VISIBLE) {
            clientsProgressView.setVisibility(INVISIBLE);
        }
        if (clientsView.getVisibility() == INVISIBLE) {
            clientsView.setVisibility(VISIBLE);
        }
    }

    private String filterandSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb
                        .searchQueryFts(tablename, joinTable, mainCondition, filters, Sortqueries,
                                currentlimit, currentoffset);
                List<String> ids = commonRepository().findSearchIds(sql);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN,
                        Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query, currentlimit, currentoffset));

            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    public void CountExecute() {
        Cursor c = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query = "";
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb.countQueryFts(tablename, joinTable, mainCondition, filters);
                Timber.i(query);

                totalcount = commonRepository().countSearchIds(sql);
                Timber.v("total count here %d", totalcount);


            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(query);

                Timber.i(query);
                c = commonRepository().rawCustomQueryForAdapter(query);
                c.moveToFirst();
                totalcount = c.getInt(0);
                Timber.v("total count here %d", totalcount);
            }

            currentlimit = 20;
            currentoffset = 0;

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    protected boolean isValidFilterForFts(CommonRepository commonRepository) {
        return commonRepository.isFts() && filters != null && !StringUtils
                .containsIgnoreCase(filters, "like") && !StringUtils
                .startsWithIgnoreCase(filters.trim(), "and ");
    }

    public Bundle countBundle() {
        Bundle args = new Bundle();
        args.putBoolean(COUNT, true);
        return args;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case LOADER_ID:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        // Count query
                        if (args != null && args.getBoolean(COUNT)) {
                            CountExecute();
                        }

                        // Select register query
                        String query = filterandSortQuery();
                        return commonRepository().rawCustomQueryForAdapter(query);
                    }
                };
            default:
                // An invalid id was passed in
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        clientAdapter.swapCursor(cursor);

        hideProgressView();
        refresh();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clientAdapter.swapCursor(null);
    }

    public CommonRepository commonRepository() {
        return context().commonrepository(tablename);
    }

    public boolean isPausedOrRefreshList() {
        return isPaused() || isRefreshList();
    }

    private class FilterDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            if (getNavBarOptionsProvider() != null) {
                return getNavBarOptionsProvider().filterOptions();
            } else {
                return new DialogOption[]{};
            }
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onFilterSelection((FilterOption) option);
        }
    }

    private class SortDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            if (getNavBarOptionsProvider() != null) {
                return getNavBarOptionsProvider().sortingOptions();
            } else {
                return new DialogOption[]{};
            }
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onSortSelection((SortOption) option);
        }
    }

    protected class ServiceModeDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            if (getNavBarOptionsProvider() != null) {
                return getNavBarOptionsProvider().serviceModeOptions();
            } else {
                return new DialogOption[]{};
            }
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onServiceModeSelection((ServiceModeOption) option, mView);
        }
    }

    private class PaginationViewHandler implements View.OnClickListener {

        private void addPagination(ListView clientsView) {

            PaginationHolder paginationHolder = ViewHelper.addPaginationCore(this, clientsView);
            nextPageView = paginationHolder.getNextPageView();
            previousPageView = paginationHolder.getPreviousPageView();
            pageInfoView = paginationHolder.getPageInfoView();
            refresh();
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

    protected String getFormattedPaginationInfoText(int currentPage, int pageCount) {
        return format(getResources().getString(R.string.str_page_info), currentPage, pageCount);
    }
}