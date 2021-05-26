package org.smartregister.view.activity;

import android.os.Bundle;

import org.smartregister.view.controller.ReportIndicatorCaseListViewController;

import java.util.List;

import static org.smartregister.AllConstants.CASE_IDS;
import static org.smartregister.AllConstants.INDICATOR;
import static org.smartregister.AllConstants.MONTH;

public class ReportIndicatorCaseListActivity extends SecuredWebActivity {
    @Override
    protected void onInitialization() {
        Bundle extras = getIntent().getExtras();
        List<String> caseIds = extras.getStringArrayList(CASE_IDS);
        String month = extras.getString(MONTH);
        String indicator = extras.getString(INDICATOR);

        webView.addJavascriptInterface(
                new ReportIndicatorCaseListViewController(this, indicator, caseIds, month),
                "context");
        webView.loadUrl("file:///android_asset/www/report_indicator_case_list.html");
    }
}
