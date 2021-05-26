package org.smartregister.cursoradapter;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.smartregister.R;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.ReportMonth;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
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
import static org.smartregister.AllConstants.SHORT_DATE_FORMAT;

/**
 * Created by keyman on 09/07/18.
 */
public abstract class RecyclerViewFragment extends
        SecuredNativeSmartRegisterFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    protected static final int LOADER_ID = 0;

    public final SearchCancelHandler searchCancelHandler = new SearchCancelHandler();
    public final PaginationViewHandler paginationViewHandler = new PaginationViewHandler();
    private final NavBarActionsHandler navBarActionsHandler = new NavBarActionsHandler();

    public String mainSelect;
    public String filters = "";
    public String mainCondition = "";
    public String Sortqueries;
    public String tablename;
    public String countSelect;
    public String joinTable = "";
    public String joinTables[];
    public RecyclerView clientsView;
    public RecyclerViewPaginatedAdapter clientAdapter;

    public View mView;

    private FilterOption currentVillageFilter;
    private SortOption currentSortOption;
    private FilterOption currentSearchFilter;
    private ServiceModeOption currentServiceModeOption;

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

    public RecyclerViewPaginatedAdapter getClientsCursorAdapter() {
        return clientAdapter;
    }

    public void setClientsAdapter(RecyclerViewPaginatedAdapter clientsAdapter) {
        this.clientAdapter = clientsAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getActivity().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View view = inflater.inflate(R.layout.smart_register_rv_activity, container, false);
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
        clientsView = view.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        clientsView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        clientsView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        clientsView.addItemDecoration(itemDecor);

        setupStatusBarViews(view);

        updateDefaultOptions();
    }

    public void refreshListView() {
        super.setRefreshList(true);
        this.onResumption();
        super.setRefreshList(false);
    }

    @Override
    protected void onResumption() {
        // TODO add implementation
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
        if (serviceModeView != null) {
            serviceModeView.setCompoundDrawables(null, null, drawable, null);
        }
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
                    // TODO implement this
                }

                @Override
                public void onTextChanged(CharSequence cs, int start, int before, int count) {
                    // TODO implement this
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // TODO implement this
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

    protected void showFragmentDialog(DialogOptionModel dialogOptionModel) {
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
        if (clientAdapter.hasNextPage()) {
            clientAdapter.nextPageOffset();
            filterandSortExecute();
        }
    }

    public void goBackToPreviousPage() {
        if (clientAdapter.hasPreviousPage()) {
            clientAdapter.previousPageOffset();
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
        getLoaderManager().restartLoader(LOADER_ID, args, this);
    }

    public void filterandSortExecute() {
        filterandSortExecute(null);
    }

    public void showProgressView() {
        if (clientsProgressView.getVisibility() == INVISIBLE) {
            clientsProgressView.setVisibility(VISIBLE);
        }

        if (clientsView.getVisibility() == VISIBLE) {
            clientsView.setVisibility(INVISIBLE);
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
                                clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());
                List<String> ids = commonRepository().findSearchIds(sql);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN,
                        Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));

            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    private String filterandSortJoinArrayQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb
                        .searchQueryFts(tablename, joinTables, mainCondition, filters, Sortqueries,
                                clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());
                List<String> ids = commonRepository().findSearchIds(sql);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN,
                        Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));

            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    public void countExecute() {
        Cursor c = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query = "";
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb.countQueryFts(tablename, joinTable, mainCondition, filters);
                Timber.i(query);

                clientAdapter.setTotalcount(commonRepository().countSearchIds(sql));
                Timber.v("total count here %d", clientAdapter.getTotalcount());


            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(query);

                Timber.i(query);
                c = commonRepository().rawCustomQueryForAdapter(query);
                c.moveToFirst();
                clientAdapter.setTotalcount(c.getInt(0));
                Timber.v("total count here %d", clientAdapter.getTotalcount());
            }

            if (clientAdapter.getCurrentlimit() == 0) {
                clientAdapter.setCurrentlimit(20);
            }
            clientAdapter.setCurrentoffset(0);


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
                            countExecute();
                        }
                        String query = "";
                        // Select register query

                        if (ArrayUtils.isNotEmpty(joinTables)) {
                            query = filterandSortJoinArrayQuery();
                        } else {
                            query = filterandSortQuery();
                        }
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

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////
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