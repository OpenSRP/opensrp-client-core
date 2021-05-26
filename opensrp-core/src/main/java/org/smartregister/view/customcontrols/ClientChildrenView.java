package org.smartregister.view.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.R;
import org.smartregister.view.contract.ECChildClient;
import org.smartregister.view.contract.ECSmartRegisterClient;

import java.util.List;

import static java.text.MessageFormat.format;

public class ClientChildrenView extends LinearLayout {

    private TextView ageView1;
    private TextView ageView2;

    @SuppressWarnings("UnusedDeclaration")
    public ClientChildrenView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ClientChildrenView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClientChildrenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        ageView1 = (TextView) findViewById(R.id.txt_children_age_left);
        ageView2 = (TextView) findViewById(R.id.txt_children_age_right);
    }

    public void bindData(ECSmartRegisterClient client, String maleChildAgeFormatString, String
            femaleChildAgeFormatString) {
        List<ECChildClient> children = client.children();
        if (children.size() == 0) {
            ageView1.setVisibility(GONE);
            ageView2.setVisibility(GONE);
        } else if (children.size() == 1) {
            setupChildView(children.get(0), ageView1, maleChildAgeFormatString,
                    femaleChildAgeFormatString);
            ((LinearLayout.LayoutParams) ageView1.getLayoutParams()).weight = 100;
            ageView2.setVisibility(GONE);
        } else {
            setupChildView(children.get(0), ageView1, maleChildAgeFormatString,
                    femaleChildAgeFormatString);
            setupChildView(children.get(1), ageView2, maleChildAgeFormatString,
                    femaleChildAgeFormatString);
            ((LinearLayout.LayoutParams) ageView1.getLayoutParams()).weight = 50;
            ((LinearLayout.LayoutParams) ageView2.getLayoutParams()).weight = 50;
        }
    }

    private void setupChildView(ECChildClient child, TextView ageView, String
            maleChildAgeFormatString, String femaleChildAgeFormatString) {
        ageView.setVisibility(VISIBLE);
        ageView.setText(
                format(child.isMale() ? maleChildAgeFormatString : femaleChildAgeFormatString,
                        child.getAgeInString()));
    }
}
