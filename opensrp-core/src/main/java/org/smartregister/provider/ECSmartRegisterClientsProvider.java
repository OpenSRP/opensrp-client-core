package org.smartregister.provider;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import org.smartregister.R;
import org.smartregister.view.contract.ECSmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.controller.ECSmartRegisterController;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.ECProfilePhotoLoader;
import org.smartregister.view.viewholder.NativeECSmartRegisterViewHolder;
import org.smartregister.view.viewholder.OnClickFormLauncher;
import org.smartregister.view.viewholder.ProfilePhotoLoader;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ECSmartRegisterClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final ProfilePhotoLoader photoLoader;

    private final String maleChildAgeFormatString;
    private final String femaleChildAgeFormatString;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected ECSmartRegisterController controller;

    private Drawable iconPencilDrawable;

    public ECSmartRegisterClientsProvider(Context context, View.OnClickListener onClickListener,
                                          ECSmartRegisterController controller) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        photoLoader = new ECProfilePhotoLoader(context.getResources(),
                context.getResources().getDrawable(R.drawable.woman_placeholder));

        maleChildAgeFormatString = context.getResources()
                .getString(R.string.ec_register_male_child);
        femaleChildAgeFormatString = context.getResources()
                .getString(R.string.ec_register_female_child);
        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(R.color.text_black);
    }

    @Override
    public View getView(SmartRegisterClient smartRegisterClient, View convertView, ViewGroup
            viewGroup) {
        ViewGroup itemView;
        NativeECSmartRegisterViewHolder viewHolder;
        if (convertView == null) {
            itemView = (ViewGroup) inflater().inflate(R.layout.smart_register_ec_client, null);
            viewHolder = new NativeECSmartRegisterViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            itemView = (ViewGroup) convertView;
            viewHolder = (NativeECSmartRegisterViewHolder) itemView.getTag();
        }

        ECSmartRegisterClient client = (ECSmartRegisterClient) smartRegisterClient;
        setupClientProfileView(client, viewHolder);
        setupEcNumberView(client, viewHolder);
        setupGPLSAView(client, viewHolder);
        setupFPMethodView(client, viewHolder);
        setupChildrenView(client, viewHolder);
        setupStatusView(client, viewHolder);
        setupEditView(client, viewHolder);

        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    private void setupClientProfileView(ECSmartRegisterClient client,
                                        NativeECSmartRegisterViewHolder viewHolder) {
        viewHolder.profileInfoLayout().bindData(client, photoLoader);
        viewHolder.profileInfoLayout().setOnClickListener(onClickListener);
        viewHolder.profileInfoLayout().setTag(client);
    }

    private void setupEcNumberView(ECSmartRegisterClient client, NativeECSmartRegisterViewHolder
            viewHolder) {
        viewHolder.txtECNumberView().setText(String.valueOf(client.ecNumber()));
    }

    private void setupGPLSAView(ECSmartRegisterClient client, NativeECSmartRegisterViewHolder
            viewHolder) {
        viewHolder.gplsaLayout().bindData(client);
    }

    private void setupFPMethodView(ECSmartRegisterClient client, NativeECSmartRegisterViewHolder
            viewHolder) {
        viewHolder.fpMethodView().bindData(client, txtColorBlack);
    }

    private void setupChildrenView(ECSmartRegisterClient client, NativeECSmartRegisterViewHolder
            viewHolder) {
        viewHolder.childrenView()
                .bindData(client, maleChildAgeFormatString, femaleChildAgeFormatString);
    }

    private void setupStatusView(ECSmartRegisterClient client, NativeECSmartRegisterViewHolder
            viewHolder) {
        viewHolder.statusView().bindData(client);
    }

    private void setupEditView(ECSmartRegisterClient client, NativeECSmartRegisterViewHolder
            viewHolder) {
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        viewHolder.editButton().setImageDrawable(iconPencilDrawable);
        viewHolder.editButton().setOnClickListener(onClickListener);
        viewHolder.editButton().setTag(client);
    }

    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
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
}
