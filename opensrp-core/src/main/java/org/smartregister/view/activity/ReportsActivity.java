package org.smartregister.view.activity;

import org.smartregister.view.controller.ReportsController;

public class ReportsActivity extends SecuredWebActivity {
    @Override
    protected void onInitialization() {
        webView.addJavascriptInterface(new ReportsController(this), "context");
        webView.loadUrl("file:///android_asset/www/reports.html");
    }
}
