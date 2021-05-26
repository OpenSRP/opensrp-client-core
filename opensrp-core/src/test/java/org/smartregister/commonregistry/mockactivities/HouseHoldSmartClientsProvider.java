package org.smartregister.commonregistry.mockactivities;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by user on 2/12/15.
 */
public class HouseHoldSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;

    public HouseHoldSmartClientsProvider(Context context,
                                         View.OnClickListener onClickListener,
                                         AlertService alertService) {
        this.onClickListener = onClickListener;
//        this.controller = controller;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.smartregister.R.color.text_black);

    }


    //    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View convertView) {
        ViewHolder viewHolder;
//        if (convertView == null){
//           convertView = (ViewGroup) inflater().inflate(R.layout.smart_register_household_client, null);
        viewHolder = new ViewHolder();

        convertView.setTag(viewHolder);
//        }else{
//            viewHolder = (ViewHolder) convertView.getTag();
//            viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.household_profile_thumb));
//        }

//        viewHolder.follow_up.setOnClickListener(onClickListener);
//        viewHolder.follow_up.setTag(smartRegisterClient);
//        viewHolder.profilelayout.setOnClickListener(onClickListener);
//        viewHolder.profilelayout.setTag(smartRegisterClient);
        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;

//        List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "FW CENSUS");

//        AllCommonsRepository householdrep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_household");
//        CommonPersonObject householdobject = householdrep.findByCaseID(pc.entityId());
//
//        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
//        detailsRepository.updateDetails(pc);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        // do nothing.
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        View View = inflater().inflate(R.layout.smart_register_ec_client, null);
        return View;
    }


    class ViewHolder {

        TextView gobhhid;
        TextView jvitahhid;
        TextView village;
        TextView headofhouseholdname;
        TextView no_of_mwra;
        TextView last_visit_date;
        TextView due_visit_date;
        ImageButton follow_up;
        LinearLayout profilelayout;
        ImageView profilepic;
        FrameLayout due_date_holder;
        Button warnbutton;
    }


}

