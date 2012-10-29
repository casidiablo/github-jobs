package com.github.jobs.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.ui.activity.TemplateDetailsActivity;
import com.github.jobs.utils.AppUtils;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplateDetailsFragment extends SherlockFragment {
    public static final String PREVIEW_TEMPLATE_URL = "file:///android_asset/preview_template.html";
    public static final String JS_INTERFACE = "githubJobs";

    private WebView mTemplateContent;
    private SqlAdapter mAdapter;
    private long mTemplateId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.template_details, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return;
        }


        mTemplateId = intent.getLongExtra(TemplateDetailsActivity.EXTRA_TEMPLATE_ID, -1);
        if (mTemplateId == -1) {
            getActivity().finish();
            Toast.makeText(getActivity(), R.string.invalid_template, Toast.LENGTH_LONG).show();
            return;
        }

        mAdapter = Persistence.getAdapter(getActivity());
        mTemplateContent = (WebView) getView().findViewById(R.id.lbl_template_content);
        AppUtils.setupWebView(mTemplateContent);

        onTemplateChanged();
    }

    public void onTemplateChanged() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        // retrieve the template from the database
        Template template = new Template();
        template.setId(mTemplateId);
        template = mAdapter.findFirst(template);

        // set the template name
        activity.setTitle(template.getName());

        // set the template content
        final String content = template.getContent();
        mTemplateContent.addJavascriptInterface(new GithubJobsJavascriptInterface(getActivity(), mTemplateContent, content), JS_INTERFACE);
        mTemplateContent.loadUrl(PREVIEW_TEMPLATE_URL);
    }

    public static class GithubJobsJavascriptInterface {
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
}
