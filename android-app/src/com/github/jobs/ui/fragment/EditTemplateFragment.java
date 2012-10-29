package com.github.jobs.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ViewSwitcher;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.SOUser;
import com.github.jobs.bean.Template;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.templates.TemplateServicesUtil;
import com.github.jobs.ui.activity.SOUserPickerActivity;
import com.github.jobs.ui.activity.TemplateDetailsActivity;
import com.github.jobs.ui.dialog.ServiceChooserDialog;
import com.github.jobs.utils.AppUtils;

import java.util.ArrayList;

import static com.github.jobs.ui.fragment.TemplateDetailsFragment.GithubJobsJavascriptInterface;

/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateFragment extends SherlockFragment {
    private static final String KEY_TEMPLATE_SERVICES = "com.github.jobs.key.template_services";
    private static final int EDITOR_MODE = 0;
    private static final int PREVIEW_MODE = 1;

    private EditText mTemplateContent;
    private EditText mTemplateName;

    private long mTemplateId;
    private GithubJobsJavascriptInterface mJavascriptInterface;
    private ArrayList<TemplateService> mTemplateServices;
    private ViewSwitcher mViewSwitcher;
    private boolean mShowEditor = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mTemplateServices = new ArrayList<TemplateService>();
        if (savedInstanceState != null) {
            ArrayList<Parcelable> list = savedInstanceState.getParcelableArrayList(KEY_TEMPLATE_SERVICES);
            if (list != null) {
                for (Parcelable parcelable : list) {
                    mTemplateServices.add((TemplateService) parcelable);
                }
            }
        }
        return inflater.inflate(R.layout.edit_template, null, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_TEMPLATE_SERVICES, mTemplateServices);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = getView();

        mViewSwitcher = (ViewSwitcher) root.findViewById(R.id.switcher_edit_mode);
        if (!mShowEditor) {
            mViewSwitcher.setDisplayedChild(PREVIEW_MODE);
        }

        WebView templatePreview = (WebView) root.findViewById(R.id.lbl_template_preview);
        AppUtils.setupWebView(templatePreview);
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
                    TemplateService soService = new TemplateService();
                    soService.setType(TemplateServicesUtil.STACK_OVERFLOW);
                    soService.setData(soUser.getLink());
                    mTemplateServices.add(soService);
                    if (soUser.getWebsiteUrl() != null && !TemplateServicesUtil.containsWebsite(mTemplateServices)) {
                        TemplateService webService = new TemplateService();
                        webService.setType(TemplateServicesUtil.WEBSITE);
                        webService.setData(soUser.getWebsiteUrl());
                        mTemplateServices.add(webService);
                    }
                    updatePreview();
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
        public void afterTextChanged(Editable editable) {
            updatePreview();
        }
    };

    private void updatePreview() {
        if (mJavascriptInterface == null) {
            return;
        }
        String markdownContent = mTemplateContent.getText().toString().trim();
        if (mTemplateServices != null && !mTemplateServices.isEmpty()) {
            markdownContent += "\n\n---\n";
            for (TemplateService service : mTemplateServices) {
                markdownContent += TemplateServicesUtil.getContent(getActivity(), service) + "\n\n";
            }

        }
        mJavascriptInterface.setContent(markdownContent);
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

    public void showEditor(boolean showEditor) {
        mShowEditor = showEditor;
        if (mViewSwitcher == null) {
            return;
        }
        mViewSwitcher.setDisplayedChild(showEditor ? EDITOR_MODE : PREVIEW_MODE);
    }
}
