package org.smartregister.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.acra.ACRA;
import org.smartregister.R;
import org.smartregister.sync.SyncAfterFetchListener;
import org.smartregister.sync.SyncProgressIndicator;
import org.smartregister.sync.UpdateActionsTask;
import org.smartregister.view.InternationalizationContext;
import org.smartregister.view.controller.UpdateController;

import static android.webkit.ConsoleMessage.MessageLevel.ERROR;
import static java.text.MessageFormat.format;
import static org.smartregister.util.Log.logDebug;
import static org.smartregister.util.Log.logError;

public abstract class SecuredWebActivity extends SecuredActivity {
    protected WebView webView;
    protected UpdateController updateController;
    protected boolean shouldDismissProgressBarOnProgressComplete = true;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreation() {
        setActivityLayout();

        progressDialogInitialization();
        webViewInitialization(this);

        updateController = new UpdateController(webView);

        onInitialization();
    }

    protected void setActivityLayout() {
        setContentView(R.layout.html);
    }

    protected abstract void onInitialization();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.updateMenuItem) {
            updateFromServer();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void updateFromServer() {
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(this, context().actionService(),
                context().formSubmissionSyncService(), new SyncProgressIndicator(),
                context().allFormVersionSyncService());
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }

    protected void closeDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onResumption() {
    }

    //    Added to fix the memory leak caused due to bug in android which stops activities with
    // webview to be GCed.
    //    Refer to this link for more details - http://code.google
    // .com/p/android/issues/detail?id=9375
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (webView != null) {
            webView.destroy();
            webView = null;
        }

        if (updateController != null) {
            updateController.destroy();
        }
    }

    private void progressDialogInitialization() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("Please wait");
        progressDialog.show();
    }

    private void webViewInitialization(final Activity activity) {
        webView = (WebView) findViewById(R.id.webview);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                dismissProgressBarOnProgressComplete(progress, activity);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String message = format(
                        "Javascript Log. Message: {0}, lineNumber: {1}, sourceId," + "" + ""
                                + " {2}", consoleMessage.message(), consoleMessage.lineNumber(),
                        consoleMessage.sourceId());

                if (consoleMessage.messageLevel() == ERROR) {
                    logError(message);
                    reportException(message);
                } else {
                    logDebug(message);
                }
                return true;
            }
        });

        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.addJavascriptInterface(formController, "formContext");
        webView.addJavascriptInterface(navigationController, "navigationContext");
        webView.addJavascriptInterface(new InternationalizationContext(getResources()),
                "internationalizationContext");
    }

    private void dismissProgressBarOnProgressComplete(int progress, Activity activity) {
        if (shouldDismissProgressBarOnProgressComplete) {
            activity.setProgress(progress * 1000);

            if (progress == 100 && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    protected void reportException(String message) {
        ACRA.getErrorReporter().handleSilentException(new RuntimeException(message));
    }
}
