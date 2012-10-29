package com.github.jobs.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.SOUser;
import com.github.jobs.bean.Template;
import com.github.jobs.ui.activity.SOUserPickerActivity;
import com.github.jobs.ui.activity.TemplateDetailsActivity;
import com.github.jobs.ui.dialog.ServiceChooserDialog;
import com.github.jobs.utils.AppUtils;
import com.github.jobs.utils.TemplateServicesUtil;

import static com.github.jobs.ui.fragment.TemplateDetailsFragment.GithubJobsJavascriptInterface;

/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateFragment extends SherlockFragment {

    private EditText mTemplateContent;
    private EditText mTemplateName;

    private long mTemplateId;
    private GithubJobsJavascriptInterface mJavascriptInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.edit_template, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = getView();
        WebView templatePreview = (WebView) root.findViewById(R.id.lbl_template_preview);
        AppUtils.setupWebview(templatePreview);
        templatePreview.setOnTouchListener(mPreviewTouchListener);
        mJavascriptInterface = new GithubJobsJavascriptInterface(getActivity(), templatePreview, null);
        templatePreview.addJavascriptInterface(mJavascriptInterface, TemplateDetailsFragment.JS_INTERFACE);
        templatePreview.loadUrl(TemplateDetailsFragment.PREVIEW_TEMPLATE_URL);

        mTemplateName = (EditText) root.findViewById(R.id.edit_template_name);
        mTemplateContent = (EditText) root.findViewById(R.id.edit_template_content);
        mTemplateContent.addTextChangedListener(mTextWatcher);

        mTemplateId = getActivity().getIntent().getLongExtra(TemplateDetailsActivity.EXTRA_TEMPLATE_ID, -1);
        if (mTemplateId != -1) {
            // retrieve template from database
            SqlAdapter adapter = Persistence.getAdapter(getActivity());
            Template template = new Template();
            template.setId(mTemplateId);
            template = adapter.findFirst(template);

            // set name and raw content
            mTemplateName.setText(template.getName());
            String content = template.getContent();
            mTemplateContent.setText(content);
            mJavascriptInterface.setContent(content);
            mJavascriptInterface.onLoaded();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_template_service_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_add_service:
                Intent serviceChooser = new Intent(getActivity(), ServiceChooserDialog.class);
                startActivityForResult(serviceChooser, ServiceChooserDialog.REQUEST_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SOUserPickerActivity.REQUEST_CODE:
                if (data == null) {
                    // meh... there was no data
                    return;
                }
                Parcelable userParcel = data.getParcelableExtra(SOUserPickerActivity.EXTRA_USER);
                if (userParcel instanceof SOUser) {
                    SOUser soUser = (SOUser) userParcel;
                    // add this to the template
                    updatePreview(soUser.toString());
                }
                break;
            case ServiceChooserDialog.REQUEST_CODE:
                int serviceId = data.getIntExtra(ServiceChooserDialog.EXTRA_SERVICE_ID, -1);
                if (serviceId != 1) {
                    TemplateServicesUtil.resolve(getActivity(), this, serviceId);
                }
                break;
        }
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            updatePreview(text);
        }
    };

    private void updatePreview(final String text) {
        if (mJavascriptInterface == null) {
            return;
        }
        mJavascriptInterface.setContent(text);
        mJavascriptInterface.onLoaded();
    }

    public Template buildTemplate() {
        // build the template
        Template template = new Template();
        if (mTemplateId > 0) {
            template.setId(mTemplateId);
        }
        template.setName(mTemplateName.getText().toString().trim());
        template.setContent(mTemplateContent.getText().toString().trim());
        template.setLastUpdate(System.currentTimeMillis());

        return template;
    }

    private View.OnTouchListener mPreviewTouchListener = new View.OnTouchListener() {
        private int previousEvent;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && previousEvent == MotionEvent.ACTION_DOWN) {
                mTemplateContent.requestFocus();
                mTemplateContent.setSelection(mTemplateContent.getText().length());
                // show the keyboard so that user can start editing this
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mTemplateContent, InputMethodManager.SHOW_IMPLICIT);
                previousEvent = MotionEvent.ACTION_UP;
                return true;
            }
            previousEvent = event.getAction();
            return false;
        }
    };

    public boolean isTemplateValid() {
        if (TextUtils.isEmpty(mTemplateName.getText().toString().trim())) {
            mTemplateName.setError(getString(R.string.template_name_is_empty));
            mTemplateName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(mTemplateContent.getText().toString().trim())) {
            mTemplateContent.setError(getString(R.string.template_content_is_empty));
            mTemplateContent.requestFocus();
            return false;
        }
        return true;
    }
}
