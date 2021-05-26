package org.smartregister.view.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.R;
import org.smartregister.domain.FPMethod;
import org.smartregister.view.contract.BaseFPSmartRegisterClient;

public class ClientPNCPpFpMethodView extends LinearLayout {
    private TextView ppFpMethodView;
    private ClientFpMethodView clientFpMethodView;

    @SuppressWarnings("UnusedDeclaration")
    public ClientPNCPpFpMethodView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ClientPNCPpFpMethodView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClientPNCPpFpMethodView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        ppFpMethodView = (TextView) findViewById(R.id.txt_pp_fp_method_view);
        clientFpMethodView = (ClientFpMethodView) findViewById(R.id.fp_method_layout);
        clientFpMethodView.initialize();
    }

    public void setLayoutParams() {
        LayoutParams params = new LayoutParams(
                (int) getResources().getDimension(R.dimen.list_fp_view_width),
                ViewGroup.LayoutParams.FILL_PARENT,
                getResources().getInteger(R.integer.fp_list_method_view_weight));

        this.setLayoutParams(params);
    }

    public void bindData(BaseFPSmartRegisterClient client, int txtColorBlack) {
        FPMethod fpMethod = client.fpMethod();

        refreshAllFPMethodDetailViews();

        if (fpMethod == FPMethod.NONE) {
            ppFpMethodView.setVisibility(View.VISIBLE);
        } else {
            clientFpMethodView.setVisibility(View.VISIBLE);
            clientFpMethodView.bindData(client, txtColorBlack);
        }
    }

    public void refreshAllFPMethodDetailViews() {
        ppFpMethodView.setVisibility(View.GONE);
        clientFpMethodView.setVisibility(View.GONE);
    }
}
