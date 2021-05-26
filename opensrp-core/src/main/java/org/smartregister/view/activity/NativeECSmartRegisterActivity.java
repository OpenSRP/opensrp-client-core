package org.smartregister.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.View;

import org.smartregister.R;
import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.provider.ECSmartRegisterClientsProvider;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.ECClient;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.controller.ECSmartRegisterController;
import org.smartregister.view.controller.VillageController;
import org.smartregister.view.dialog.AllClientsFilter;
import org.smartregister.view.dialog.AllEligibleCoupleServiceMode;
import org.smartregister.view.dialog.BPLSort;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.DialogOptionMapper;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.dialog.ECNumberSort;
import org.smartregister.view.dialog.EditOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.HighPrioritySort;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.NameSort;
import org.smartregister.view.dialog.OpenFormOption;
import org.smartregister.view.dialog.SCSort;
import org.smartregister.view.dialog.STSort;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static org.smartregister.AllConstants.FormNames.ANC_REGISTRATION;
import static org.smartregister.AllConstants.FormNames.CHILD_REGISTRATION_EC;
import static org.smartregister.AllConstants.FormNames.EC_CLOSE;
import static org.smartregister.AllConstants.FormNames.EC_EDIT;
import static org.smartregister.AllConstants.FormNames.EC_REGISTRATION;
import static org.smartregister.AllConstants.FormNames.FP_CHANGE;

public class NativeECSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    public static final String locationDialogTAG = "locationDialogTAG";
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private SmartRegisterClientsProvider clientProvider = null;
    private ECSmartRegisterController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

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
                return getResources().getString(R.string.ec_register_title_in_short);
            }
        };
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                Iterable<? extends DialogOption> villageFilterOptions = dialogOptionMapper
                        .mapToVillageFilterOptions(villageController.getVillages());
                return toArray(concat(DEFAULT_FILTER_OPTIONS, villageFilterOptions),
                        DialogOption.class);
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new ECNumberSort(), new
                        HighPrioritySort(), new BPLSort(), new SCSort(), new STSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_ec_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new ECSmartRegisterClientsProvider(this, clientActionHandler,
                    controller);
        }
        return clientProvider;
    }

    private DialogOption[] getEditOptions() {
        return new DialogOption[]{new OpenFormOption(getString(R.string.str_register_anc_form),
                ANC_REGISTRATION,
                formController),
                new OpenFormOption(getString(R.string.str_register_fp_form),
                        FP_CHANGE,
                        formController),
                new OpenFormOption(getString(R.string.str_register_child_form),
                        CHILD_REGISTRATION_EC,
                        formController),
                new OpenFormOption(getString(R.string.str_edit_ec_form),
                        EC_EDIT,
                        formController),
                new OpenFormOption(getString(R.string.str_close_ec_form),
                        EC_CLOSE,
                        formController),
        };
    }

    @Override
    protected void onInitialization() {
        controller = new ECSmartRegisterController(context().allEligibleCouples(),
                context().allBeneficiaries(), context().listCache(), context().ecClientsCache());
        villageController = new VillageController(context().allEligibleCouples(),
                context().listCache(), context().villagesCache());
        dialogOptionMapper = new DialogOptionMapper();
    }

    @Override
    public void setupViews() {
        super.setupViews();

        setServiceModeViewDrawableRight(null);
    }

    @Override
    public void startRegistration() {
        System.out.println("was in reg");

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        LocationSelectorDialogFragment.newInstance(this, new EditDialogOptionModel(),
                context().anmLocationController().get(), EC_REGISTRATION)
                .show(ft, locationDialogTAG);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.profile_info_layout) {
                showProfileView((ECClient) view.getTag());

            } else if (i == R.id.btn_edit) {
                showFragmentDialog(new EditDialogOptionModel(), view.getTag());

            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
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
}
