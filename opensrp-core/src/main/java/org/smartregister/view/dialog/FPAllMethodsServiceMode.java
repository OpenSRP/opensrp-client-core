package org.smartregister.view.dialog;

import android.view.View;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.AlertStatus;
import org.smartregister.view.contract.FPAlertType;
import org.smartregister.view.contract.FPSmartRegisterClient;
import org.smartregister.view.contract.RefillFollowUps;
import org.smartregister.view.viewholder.NativeFPSmartRegisterViewHolder;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import static org.smartregister.AllConstants.FormNames.FP_COMPLICATIONS;
import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class FPAllMethodsServiceMode extends ServiceModeOption {

    public FPAllMethodsServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.fp_register_service_mode_all_methods);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
                return 6;
            }

            @Override
            public int weightSum() {
                return 100;
            }

            @Override
            public int[] weights() {
                return new int[]{24, 6, 11, 20, 20, 19};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{R.string.header_name, R.string.header_ec_no, R.string
                        .header_gplsa, R.string.header_method, R.string.header_side_effects, R
                        .string.header_followup_refill};
            }
        };
    }


    private void setupSideEffectsButtonView(FPSmartRegisterClient client,
                                            NativeFPSmartRegisterViewHolder viewHolder) {
        viewHolder.btnSideEffectsView()
                .setOnClickListener(launchForm(FP_COMPLICATIONS, client.entityId(), null));
        viewHolder.btnSideEffectsView().setTag(client);
    }

    private void setupSideEffectsView(FPSmartRegisterClient client,
                                      NativeFPSmartRegisterViewHolder viewHolder) {
        viewHolder.clientSideEffectsView().bindData(client);
    }

    private void setupUpdateButtonView(FPSmartRegisterClient client,
                                       NativeFPSmartRegisterViewHolder viewHolder, View
                                               .OnClickListener onClickListener) {
        viewHolder.btnUpdateView().setOnClickListener(onClickListener);
        viewHolder.btnUpdateView().setTag(client);
    }

    private void setupFPMethodView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder
            viewHolder, int txtColorBlack) {
        viewHolder.fpMethodView().bindData(client, txtColorBlack);
        viewHolder.fpMethodView().setTag(client);
    }

    private void setupAlertView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder
            viewHolder) {
        viewHolder.txtAlertTypeView().setTag(client);
        refreshAlertView(viewHolder);
        bindAlertData(client, viewHolder);
    }

    private void refreshAlertView(NativeFPSmartRegisterViewHolder viewHolder) {
        viewHolder.txtAlertTypeView().setText("");
        viewHolder.txtAlertTypeView().setTextColor(0);
        viewHolder.fpAlertLayout().setBackgroundResource(0);
        viewHolder.txtAlertDateView().setTextColor(0);
        viewHolder.txtAlertDateView().setText("");
        viewHolder.fpAlertLayout().setOnClickListener(null);
    }

    //#TODO: REMOVE THE HARDCODED METADATA
    private void bindAlertData(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder
            viewHolder) {
        RefillFollowUps refillFollowUps = client.refillFollowUps();
        if (refillFollowUps == null) {
            return;
        }
        AlertStatus alertStatus = refillFollowUps.alert().alertStatus();

        viewHolder.txtAlertTypeView()
                .setText(FPAlertType.from(refillFollowUps.type()).getAlertType());
        viewHolder.txtAlertTypeView().setTextColor(alertStatus.fontColor());
        viewHolder.fpAlertLayout().setBackgroundResource(alertStatus.backgroundColorResourceId());
        viewHolder.txtAlertDateView().setTextColor(alertStatus.fontColor());
        viewHolder.txtAlertDateView().setText(
                CoreLibrary.getInstance().context().getStringResource(R.string.str_due) + refillFollowUps.alert()
                        .shortDate());
        viewHolder.fpAlertLayout().setOnClickListener(
                launchForm(FPAlertType.from(refillFollowUps.type()).getFormName(),
                        client.entityId(),
                        "{\"entityId\": \"" + client.entityId() + "\", \"alertName\":\"" + client
                                .refillFollowUps().name() + "\"}"));
    }

    private OnClickFormLauncher launchForm(String formName, String entityId, String metaData) {
        return provider().newFormLauncher(formName, entityId, metaData);
    }


}
