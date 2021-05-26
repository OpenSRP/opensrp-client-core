package org.smartregister.view.customcontrols;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.FPMethod;
import org.smartregister.view.contract.BaseFPSmartRegisterClient;

public class ClientFpMethodView extends LinearLayout {
    private TextView fpMethodView;
    private TextView fpMethodDateView;
    private TextView fpMethodQuantityLabelView;
    private TextView fpMethodQuantityView;
    private TextView iudPlaceView;
    private TextView iudPersonView;

    @SuppressWarnings("UnusedDeclaration")
    public ClientFpMethodView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ClientFpMethodView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClientFpMethodView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        fpMethodView = (TextView) findViewById(R.id.txt_fp_method);
        fpMethodDateView = (TextView) findViewById(R.id.txt_fp_method_date);
        fpMethodQuantityLabelView = (TextView) findViewById(R.id.txt_fp_method_quantity_label);
        fpMethodQuantityView = (TextView) findViewById(R.id.txt_fp_method_quantity);
        iudPlaceView = (TextView) findViewById(R.id.txt_iud_place);
        iudPersonView = (TextView) findViewById(R.id.txt_iud_person);
    }

    public void setLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen.list_fp_view_width),
                ViewGroup.LayoutParams.FILL_PARENT,
                getResources().getInteger(R.integer.fp_list_method_view_weight));

        this.setLayoutParams(params);
    }

    public void bindData(BaseFPSmartRegisterClient client, int txtColorBlack) {
        FPMethod fpMethod = client.fpMethod();

        refreshAllFPMethodDetailViews(txtColorBlack);

        fpMethodView.setText(fpMethod.displayName());
        fpMethodDateView.setVisibility(View.VISIBLE);
        fpMethodDateView.setText(client.familyPlanningMethodChangeDate());

        if (fpMethod == FPMethod.NONE) {
            fpMethodView.setTextColor(Color.RED);
            fpMethodDateView.setVisibility(View.GONE);
        } else if (fpMethod == FPMethod.OCP) {
            fpMethodQuantityLabelView.setVisibility(View.VISIBLE);
            fpMethodQuantityView.setVisibility(View.VISIBLE);
            fpMethodQuantityView.setText(client.numberOfOCPDelivered());
        } else if (fpMethod == FPMethod.CONDOM) {
            fpMethodQuantityLabelView.setVisibility(View.VISIBLE);
            fpMethodQuantityLabelView
                    .setText(CoreLibrary.getInstance().context().getStringResource(R.string.str_number_given));
            fpMethodQuantityView.setVisibility(View.VISIBLE);
            fpMethodQuantityView.setText(client.numberOfCondomsSupplied());
        } else if (fpMethod == FPMethod.CENTCHROMAN) {
            fpMethodQuantityLabelView.setVisibility(View.VISIBLE);
            fpMethodQuantityView.setVisibility(View.VISIBLE);
            fpMethodQuantityView.setText(client.numberOfCentchromanPillsDelivered());
        } else if (fpMethod == FPMethod.IUD) {
            if (StringUtils.isNotBlank(client.iudPerson())) {
                iudPersonView.setVisibility(View.VISIBLE);
                iudPersonView.setText(client.iudPerson());
            }
            if (StringUtils.isNotBlank(client.iudPlace())) {
                iudPlaceView.setVisibility(View.VISIBLE);
                iudPlaceView.setText(client.iudPlace());
            }
        }
    }

    public void refreshAllFPMethodDetailViews(int fpMethodTextColor) {
        fpMethodView.setTextColor(fpMethodTextColor);
        fpMethodDateView.setVisibility(View.GONE);
        fpMethodQuantityLabelView.setVisibility(View.GONE);
        fpMethodQuantityView.setVisibility(View.GONE);
        iudPersonView.setVisibility(View.GONE);
        iudPlaceView.setVisibility(View.GONE);
    }
}
