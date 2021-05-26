package org.smartregister.view.activity;

import android.os.Bundle;

import org.smartregister.domain.Report;
import org.smartregister.view.controller.ReportIndicatorDetailViewController;

import static org.smartregister.AllConstants.CATEGORY_DESCRIPTION;
import static org.smartregister.AllConstants.INDICATOR_DETAIL;

public class ReportIndicatorDetailActivity extends SecuredWebActivity {

    @Override
    protected void onInitialization() {
        Bundle extras = getIntent().getExtras();
        Report indicatorDetails = (Report) extras.get(INDICATOR_DETAIL);
        String categoryDescription = extras.getString(CATEGORY_DESCRIPTION);

        webView.addJavascriptInterface(
                new ReportIndicatorDetailViewController(this, indicatorDetails,
                        categoryDescription), "context");
        webView.loadUrl("file:///android_asset/www/report_indicator_detail.html");
    }
}
