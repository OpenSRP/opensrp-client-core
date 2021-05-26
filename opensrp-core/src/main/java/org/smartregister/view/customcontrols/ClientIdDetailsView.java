package org.smartregister.view.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.R;
import org.smartregister.view.contract.ChildSmartRegisterClient;

public class ClientIdDetailsView extends RelativeLayout {
    private TextView txtThayiNumber;
    private TextView txtMotherEcNumber;
    private TextView lblThayiNumber;
    private TextView lblMotherEcNumber;

    @SuppressWarnings("UnusedDeclaration")
    public ClientIdDetailsView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ClientIdDetailsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClientIdDetailsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        txtThayiNumber = (TextView) findViewById(R.id.txt_thayi_no);
        txtMotherEcNumber = (TextView) findViewById(R.id.txt_mother_ec_no);
        lblThayiNumber = (TextView) findViewById(R.id.lbl_thayi);
        lblMotherEcNumber = (TextView) findViewById(R.id.lbl_mother_ec);
    }

    public void bindData(ChildSmartRegisterClient client) {
        String thayiNumber = client.thayiCardNumber();
        String motherEcNumber = client.motherEcNumber();

        setupTextView(lblThayiNumber, txtThayiNumber, thayiNumber);
        setupTextView(lblMotherEcNumber, txtMotherEcNumber, motherEcNumber);
    }

    private void setupTextView(TextView lblView, TextView txtView, String thayiNumber) {
        if (StringUtils.isBlank(thayiNumber)) {
            lblView.setVisibility(GONE);
            txtView.setVisibility(GONE);
        } else {
            lblView.setVisibility(VISIBLE);
            txtView.setVisibility(VISIBLE);
            txtView.setText(thayiNumber);
        }
    }
}
