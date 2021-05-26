package org.smartregister.view.customcontrols;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PNCWebViewClient extends WebViewClient {
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String
            failingUrl) {
        view.loadUrl("file:///android_asset/www/pnc_graph/pnc_visit_graph_error.html");
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
    }
}
