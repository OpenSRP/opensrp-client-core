package org.smartregister.commonregistry.mockactivities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.CursorCommonObjectFilterOption;
import org.smartregister.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.util.AssetHandler;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.ECClient;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.controller.VillageController;
import org.smartregister.view.dialog.AllClientsFilter;
import org.smartregister.view.dialog.AllEligibleCoupleServiceMode;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.DialogOptionMapper;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.dialog.EditOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.NameSort;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by koros on 10/12/15.
 */
public class HouseHoldSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

    private SmartRegisterClientsProvider clientProvider = null;
    private CommonPersonObjectController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private String locationDialogTAG = "locationDialogTAG";

    @Override
    protected void onCreation() {
        //
    }

//    @Override
//    protected SmartRegisterPaginatedAdapter adapter() {
//        return new SmartRegisterPaginatedAdapter(clientsProvider());
//    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new AllEligibleCoupleServiceMode(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new NameSort();

            }

            @Override
            public String nameInShortFormForTitle() {
                return "household";
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {

                ArrayList<DialogOption> dialogOptionslist = new ArrayList<DialogOption>();

                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_all_label), filterStringForAll()));

                String locationjson = context().anmLocationController().get();
                LocationTree locationTree = AssetHandler.jsonStringToJava(locationjson, LocationTree.class);

                Map<String, TreeNode<String, Location>> locationMap =
                        locationTree.getLocationsHierarchy();
                addChildToList(dialogOptionslist, locationMap);
                DialogOption[] dialogOptions = new DialogOption[dialogOptionslist.size()];
                for (int i = 0; i < dialogOptionslist.size(); i++) {
                    dialogOptions[i] = dialogOptionslist.get(i);
                }

                return dialogOptions;
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
//                        new HouseholdCensusDueDateSort(),


//                        new CommonObjectSort(true,false,true,"age")
                };
            }

            @Override
            public String searchHint() {
                return "search";
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
//        if (clientProvider == null) {
//            clientProvider = new HouseHoldSmartClientsProvider(
//                    getActivity(),clientActionHandler , context.alertService());
//        }
        return null;
    }

    private DialogOption[] getEditOptions() {
        return ((HouseHoldSmartRegisterActivity) getActivity()).getEditOptions();
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    public void setupViews(View view) {
        getDefaultOptionsProvider();

        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(View.GONE);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
//        list.setBackgroundColor(Color.RED);
        initializeQueries();
    }

    private String sortByAlertmethod() {
        return " CASE WHEN FW_CENSUS = 'urgent' THEN '1'\n" +
                "WHEN FW_CENSUS = 'upcoming' THEN '2'\n" +
                "WHEN FW_CENSUS = 'normal' THEN '3'\n" +
                "WHEN FW_CENSUS = 'expired' THEN '4'\n" +
                "WHEN FW_CENSUS is Null THEN '5'\n" +
                "Else FW_CENSUS END ASC";
    }

    public String houseHoldMainCount() {
        return "Select Count(*) from (Select *, " +
                "(Select count(*)  from ec_elco where ec_elco.relational_id = ec_household.base_entity_id) as ELCO " +
                "from ec_household) ec_household ";
    }

    public void initializeQueries() {
        try {
            HouseHoldSmartClientsProvider hhscp = new HouseHoldSmartClientsProvider(getActivity(), clientActionHandler, context().alertService());
            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, new CommonRepository("ec_household", new String[]{"FWHOHFNAME", "FWGOBHHID", "FWJIVHHID", "existing_Mauzapara", "ELCO"}));
            clientsView.setAdapter(clientAdapter);

            setTablename("ec_household");
            SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder(houseHoldMainCount());
            mainCondition = " FWHOHFNAME is not null ";
            joinTable = "";
            countSelect = countqueryBUilder.mainCondition(mainCondition);
            super.CountExecute();

            String elcoCountSubQuery = "(Select count(*)  from ec_elco where ec_elco.relational_id = ec_household.base_entity_id) as ELCO";

            SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
            queryBUilder.selectInitiateMainTable("ec_household", new String[]{"relationalid", elcoCountSubQuery, "FWHOHFNAME", "FWGOBHHID", "FWJIVHHID", "existing_Mauzapara"});
            mainSelect = queryBUilder.mainCondition(mainCondition);
            Sortqueries = sortByAlertmethod();

            currentlimit = 20;
            currentoffset = 0;

            super.filterandSortInInitializeQueries();

//        setServiceModeViewDrawableRight(null);
            updateSearchView();
            refresh();
//        checkforNidMissing(view);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }


    }


    @Override
    public void startRegistration() {
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        Fragment prev = getActivity().getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        LocationSelectorDialogFragment
                .newInstance((HouseHoldSmartRegisterActivity) getActivity(), new
                                EditDialogOptionModel(), context().anmLocationController().get(),
                        "new_household_registration")
                .show(ft, locationDialogTAG);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
    }


    private String filterStringForOneOrMoreElco() {
        return " and ELCO > 0";
    }

    private String filterStringForNoElco() {
        return " and ELCO = 0";
    }

    private String filterStringForAll() {
        return "";
    }

    private String householdSortByName() {
        return " FWHOHFNAME COLLATE NOCASE ASC";
    }

    private String householdSortByFWGOBHHID() {
        return " FWGOBHHID ASC";
    }

    private String householdSortByFWJIVHHID() {
        return " FWJIVHHID ASC";
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }

    @Override
    protected void onResumption() {
//        super.onResumption();
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries();
        }
//        updateSearchView();
//
        try {
//            LoginActivity.setLanguage();
        } catch (Exception e) {

        }

    }

    @Override
    public void setupSearchView(View view) {
        searchView = (EditText) view.findViewById(org.smartregister.R.id.edt_search);
        searchView.setHint(getNavBarOptionsProvider().searchHint());
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(final CharSequence cs, int start, int before, int count) {

                //filters = "and FWHOHFNAME Like '%" + cs.toString() + "%' or FWGOBHHID Like '%" + cs.toString() + "%'  or FWJIVHHID Like '%" + cs.toString() + "%' ";
                filters = cs.toString();
                joinTable = "";
                mainCondition = " FWHOHFNAME is not null ";

                getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                CountExecute();
                filterandSortExecute();

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        searchCancelView = view.findViewById(org.smartregister.R.id.btn_search_cancel);
        searchCancelView.setOnClickListener(searchCancelHandler);
    }

    public void updateSearchView() {
        getSearchView().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(final CharSequence cs, int start, int before, int count) {

                //filters = "and FWHOHFNAME Like '%"+cs.toString()+"%' or FWGOBHHID Like '%"+cs.toString()+"%'  or FWJIVHHID Like '%"+cs.toString()+"%' or household.id in (Select elco.relationalid from elco where FWWOMFNAME Like '%"+cs.toString()+"%' )";
                filters = cs.toString();
                joinTable = "ec_elco";
                mainCondition = " FWHOHFNAME is not null ";

                getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                filterandSortExecute();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void addChildToList(ArrayList<DialogOption> dialogOptionslist, Map<String, TreeNode<String, Location>> locationMap) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if (entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist, entry.getValue().getChildren());

            } else {
//                StringUtil.humanize(entry.getValue().getLabel());
//                String name = StringUtil.humanize(entry.getValue().getLabel());
//                dialogOptionslist.add(new HHMauzaCommonObjectFilterOption(name, "location_name", name, "ec_household"));

            }
        }
    }


    private boolean anyNIdmissing(CommonPersonObjectController controller) {
        boolean toreturn = false;
//        List<CommonPersonObject> allchildelco = null;
//        CommonPersonObjectClients clients = controller.getClients();
//        ArrayList<String> list = new ArrayList<String>();
//        AllCommonsRepository allElcoRepository = Context.getInstance().allCommonsRepositoryobjects("elco");
//
//        for(int i = 0;i <clients.size();i++) {
//
//            list.add((clients.get(i).entityId()));
//
//        }
//        allchildelco = allElcoRepository.findByRelationalIDs(list);
//
//        if(allchildelco != null) {
//            for (int i = 0; i < allchildelco.size(); i++) {
//                if (allchildelco.get(i).getDetails().get("FWELIGIBLE").equalsIgnoreCase("1")) {
//                    if (allchildelco.get(i).getDetails().get("nidImage") == null) {
//                        toreturn = true;
//                    }
//                }
//            }
//        }
        CommonRepository commonRepository = context().commonrepository("ec_household");
        setTablename("ec_household");
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.selectInitiateMainTableCounts("ec_household");
        countqueryBUilder.joinwithALerts("ec_household", "FW CENSUS");
        countqueryBUilder.mainCondition(" FWHOHFNAME is not null ");
        String nidfilters = "and ec_household.id in ( Select Distinct ec_elco.relational_id from ec_elco where base_entity_id not in (select base_entity_id from ec_details where key MATCH 'nidImage' ) and base_entity_id  in (select base_entity_id from ec_details where key MATCH 'FWELIGIBLE2' INTERSECT select base_entity_id from ec_details where value MATCH '1') group by ec_elco.base_entity_id )";

        countqueryBUilder.addCondition(nidfilters);
        Cursor c = commonRepository.rawCustomQueryForAdapter(countqueryBUilder.Endquery(countqueryBUilder.toString()));
        c.moveToFirst();
        int missingnidCount = c.getInt(0);
        c.close();
        if (missingnidCount > 0) {
            toreturn = true;
        }
        return toreturn;
//        return false;
    }

    @Override
    protected Context context() {
        return HouseHoldSmartRegisterActivity.mockactivitycontext;
    }

}
