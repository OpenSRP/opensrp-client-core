package org.smartregister.view.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.view.contract.ANCSmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.viewholder.ProfilePhotoLoader;

public class ClientProfileView extends RelativeLayout {
    private ImageView imgProfileView;
    private TextView txtNameView;
    private TextView txtHusbandNameView;
    private TextView txtVillageNameView;
    private TextView txtAgeView;
    private TextView txtOutOfArea;
    private ImageView badgeHPView;
    private ImageView badgeHRView;
    private ImageView badgeBPLView;
    private ImageView badgeSCView;
    private ImageView badgeSTView;

    @SuppressWarnings("UnusedDeclaration")
    public ClientProfileView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ClientProfileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClientProfileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        imgProfileView = (ImageView) findViewById(R.id.img_profile);
        txtNameView = (TextView) findViewById(R.id.txt_wife_name);
        txtHusbandNameView = (TextView) findViewById(R.id.txt_husband_name);
        txtVillageNameView = (TextView) findViewById(R.id.txt_village_name);
        txtAgeView = (TextView) findViewById(R.id.txt_age);
        txtOutOfArea = (TextView) findViewById(R.id.txt_out_of_area);
        badgeHPView = (ImageView) findViewById(R.id.img_hp_badge);
        badgeHRView = (ImageView) findViewById(R.id.img_hr_badge);
        badgeBPLView = (ImageView) findViewById(R.id.img_bpl_badge);
        badgeSCView = (ImageView) findViewById(R.id.img_sc_badge);
        badgeSTView = (ImageView) findViewById(R.id.img_st_badge);
    }

    // #TODO: make these names generic, so this layout can be reused in all the registers
    public void bindData(SmartRegisterClient client, ProfilePhotoLoader photoLoader) {
        imgProfileView.setBackground(photoLoader.get(client));
        txtNameView.setText(client.displayName());
        txtHusbandNameView.setText(client.husbandName());
        txtVillageNameView.setText(client.village());
        txtAgeView.setText(client.ageInString());
        txtOutOfArea.setText(getOutOfAreaText(client.locationStatus()));
        badgeHPView.setVisibility(
                !isAnANCClient(client) && client.isHighPriority() ? View.VISIBLE : View.GONE);
        setHROrHRPBadge(client);
        badgeHRView.setVisibility(client.isHighRisk() ? View.VISIBLE : View.GONE);
        badgeBPLView.setVisibility(client.isBPL() ? View.VISIBLE : View.GONE);
        badgeSCView.setVisibility(client.isSC() ? View.VISIBLE : View.GONE);
        badgeSTView.setVisibility(client.isST() ? View.VISIBLE : View.GONE);
    }

    private void setHROrHRPBadge(SmartRegisterClient client) {
        badgeHRView
                .setImageResource(isAnANCClient(client) ? R.drawable.flag_hrp : R.drawable.flag_hr);
    }

    private boolean isAnANCClient(SmartRegisterClient client) {
        return client instanceof ANCSmartRegisterClient;
    }

    private String getOutOfAreaText(String locationStatus) {
        return isOutOfArea(locationStatus) ? CoreLibrary.getInstance().context()
                .getStringResource(R.string.str_out_of_area) : "";
    }

    private boolean isOutOfArea(String locationStatus) {
        return AllConstants.OUT_OF_AREA.equalsIgnoreCase(locationStatus);
    }
}