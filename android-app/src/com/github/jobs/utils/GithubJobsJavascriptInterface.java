package com.github.jobs.utils;

import android.app.Activity;
import android.webkit.WebView;

/**
 * @author cristian
 */
public class GithubJobsJavascriptInterface {
    public static final String PREVIEW_TEMPLATE_URL = "file:///android_asset/preview_template.html";
    public static final String JS_INTERFACE = "githubJobs";

    private final Activity mActivity;
    private final WebView mWebView;
    private String mContent;

    public GithubJobsJavascriptInterface(Activity activity, WebView webView, String content) {
        mActivity = activity;
        mWebView = webView;
        setContent(content);
    }

    public void setContent(String content) {
        if (content == null) {
            mContent = null;
            return;
        }
        mContent = content.replaceAll("'", "\\\\'")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\r", "\\\\n");
    }

    public void onLoaded() {
        if (mContent != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:updatePreview('" + mContent + "')");
                }
            });
        }
    }
}