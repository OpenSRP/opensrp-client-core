package org.smartregister.view.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.util.IntegerUtil;
import org.smartregister.view.contract.ANCSmartRegisterClient;

import static org.smartregister.R.color.alert_urgent_red;

public class ANCStatusView extends RelativeLayout {
    private TextView txtEdd;
    private TextView txtDaysPastDue;
    private TextView lblEdd;
    private TextView lblDaysPastDue;
    private TextView lblANCStatus;
    private TextView txtANCStatus;
    private TextView lblLmp;
    private TextView txtLmp;

    @SuppressWarnings("UnusedDeclaration")
    public ANCStatusView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ANCStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ANCStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        txtEdd = (TextView) findViewById(R.id.txt_edd);
        txtDaysPastDue = (TextView) findViewById(R.id.txt_days_past_due);
        lblEdd = (TextView) findViewById(R.id.lbl_edd);
        lblDaysPastDue = (TextView) findViewById(R.id.lbl_days_past_due);
        lblANCStatus = (TextView) findViewById(R.id.lbl_anc_status);
        txtANCStatus = (TextView) findViewById(R.id.txt_anc_status);
        lblLmp = (TextView) findViewById(R.id.lbl_lmp);
        txtLmp = ((TextView) findViewById(R.id.txt_lmp));
    }

    public void bindData(ANCSmartRegisterClient client) {
        hideAllViews();
        if (IntegerUtil.tryParse(client.pastDueInDays(), 0) > 0) {
            setVisibilityForPastDue();
            setTextForPastDue(client);
            setTextColorForPastDue();
        } else {
            setVisibilityForLMP();
            setTextForLMP(client);
            setTextColorForLMP(client);
        }
    }

    private void setTextForPastDue(ANCSmartRegisterClient client) {
        txtEdd.setText(client.eddForDisplay());
        txtDaysPastDue.setText(client.pastDueInDays());
    }

    private void setVisibilityForPastDue() {
        txtEdd.setVisibility(VISIBLE);
        txtDaysPastDue.setVisibility(VISIBLE);
        lblEdd.setVisibility(VISIBLE);
        lblDaysPastDue.setVisibility(VISIBLE);
    }

    private void setVisibilityForLMP() {
        txtLmp.setVisibility(VISIBLE);
        txtEdd.setVisibility(VISIBLE);
        txtANCStatus.setVisibility(VISIBLE);
        lblLmp.setVisibility(VISIBLE);
        lblEdd.setVisibility(VISIBLE);
        lblANCStatus.setVisibility(VISIBLE);
    }

    private void setTextForLMP(ANCSmartRegisterClient client) {
        txtLmp.setText(client.lmp());
        txtEdd.setText(client.eddForDisplay());
        txtANCStatus.setText(client.weeksAfterLMP());
    }

    private void setTextColorForLMP(ANCSmartRegisterClient client) {
        if (IntegerUtil.tryParse(client.pastDueInDays(), 0) > -30
                && IntegerUtil.tryParse(client.pastDueInDays(), 0) < 0) {
            txtLmp.setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.alert_in_progress_blue));
            txtEdd.setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.alert_in_progress_blue));
            txtANCStatus
                    .setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.alert_in_progress_blue));
            lblLmp.setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.alert_in_progress_blue));
            lblEdd.setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.alert_in_progress_blue));
            lblANCStatus
                    .setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.alert_in_progress_blue));
        } else {
            txtLmp.setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.text_black));
            txtEdd.setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.text_black));
            txtANCStatus.setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.text_black));
            lblLmp.setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.text_black));
            lblEdd.setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.text_black));
            lblANCStatus.setTextColor(CoreLibrary.getInstance().context().getColorResource(R.color.text_black));
        }
    }

    private void setTextColorForPastDue() {
        lblEdd.setTextColor(CoreLibrary.getInstance().context().getColorResource(alert_urgent_red));
        txtEdd.setTextColor(CoreLibrary.getInstance().context().getColorResource(alert_urgent_red));
        lblDaysPastDue.setTextColor(CoreLibrary.getInstance().context().getColorResource(alert_urgent_red));
        txtDaysPastDue.setTextColor(CoreLibrary.getInstance().context().getColorResource(alert_urgent_red));
    }

    private void hideAllViews() {
        lblLmp.setVisibility(View.GONE);
        txtLmp.setVisibility(View.GONE);
        lblANCStatus.setVisibility(View.GONE);
        txtANCStatus.setVisibility(View.GONE);
        txtDaysPastDue.setVisibility(View.GONE);
        lblDaysPastDue.setVisibility(View.GONE);
        txtEdd.setVisibility(View.GONE);
        lblEdd.setVisibility(View.GONE);
    }

}
