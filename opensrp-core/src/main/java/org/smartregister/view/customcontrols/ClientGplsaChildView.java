package org.smartregister.view.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartregister.R;
import org.smartregister.view.contract.FPSmartRegisterClient;

public class ClientGplsaChildView extends RelativeLayout {
    private TextView txtGravida;
    private TextView txtParity;
    private TextView txtNumberOfLivingChildren;
    private TextView txtNumberOfStillBirths;
    private TextView txtNumberOfAbortions;
    private TextView txtChildAge;
    private TextView txtChild;

    @SuppressWarnings("UnusedDeclaration")
    public ClientGplsaChildView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ClientGplsaChildView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClientGplsaChildView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        txtGravida = (TextView) findViewById(R.id.txt_gravida);
        txtParity = (TextView) findViewById(R.id.txt_parity);
        txtNumberOfLivingChildren = (TextView) findViewById(R.id.txt_number_of_living_children);
        txtNumberOfStillBirths = (TextView) findViewById(R.id.txt_number_of_still_births);
        txtNumberOfAbortions = (TextView) findViewById(R.id.txt_number_of_abortions);
        txtChildAge = (TextView) findViewById(R.id.txt_child_age);
        txtChild = (TextView) findViewById(R.id.label_child);
    }

    public void bindData(FPSmartRegisterClient client) {
        txtGravida.setText(client.numberOfPregnancies());
        txtParity.setText(client.parity());
        txtNumberOfLivingChildren.setText(client.numberOfLivingChildren());
        txtNumberOfStillBirths.setText(client.numberOfStillbirths());
        txtNumberOfAbortions.setText(client.numberOfAbortions());
        if (client.youngestChildAge() == null) {
            txtChild.setVisibility(GONE);
            txtChildAge.setVisibility(GONE);
        } else {
            txtChildAge.setText(client.youngestChildAge());
            txtChild.setVisibility(VISIBLE);
            txtChildAge.setVisibility(VISIBLE);
        }
    }
}
