package com.github.jobs.ui.fragment;

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
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.templates.TemplateServicesUtil;
import com.github.jobs.ui.activity.EditTemplateActivity;
import com.github.jobs.utils.AppUtils;
import com.github.jobs.utils.GithubJobsJavascriptInterface;

import java.util.ArrayList;

import static com.github.jobs.utils.GithubJobsJavascriptInterface.JS_INTERFACE;
import static com.github.jobs.utils.GithubJobsJavascriptInterface.PREVIEW_TEMPLATE_URL;


/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateFragment extends SherlockFragment {
    private static final String KEY_TEMPLATE_SERVICES = "com.github.jobs.key.template_services";

    public static final String ARG_TEMPLATE_ID = "com.github.jobs.arg.template_id";
    public static final String ARG_EDIT_MODE = "com.github.jobs.arg.edit_mode";

    private static final int EDITOR_MODE = 0;
    private static final int PREVIEW_MODE = 1;

    private EditText mTemplateContent;
    private EditText mTemplateName;

    private long mTemplateId;
    private GithubJobsJavascriptInterface mJavascriptInterface;
    private ArrayList<TemplateService> mTemplateServices;
    private ViewSwitcher mViewSwitcher;
    private boolean mShowEditor = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        // init argument fields
        Bundle arguments = getArguments();
        mShowEditor = arguments.getBoolean(ARG_EDIT_MODE, false);
        mTemplateId = arguments.getLong(ARG_TEMPLATE_ID, -1);

        // prepare ui
        View root = getView();
        mViewSwitcher = (ViewSwitcher) root.findViewById(R.id.switcher_edit_mode);
        if (!mShowEditor) {
            showEditor(mShowEditor);
        }

        WebView templatePreview = (WebView) root.findViewById(R.id.lbl_cover_letter_preview);
        AppUtils.setupWebView(templatePreview);
        mJavascriptInterface = new GithubJobsJavascriptInterface(getActivity(), templatePreview, null);
        templatePreview.addJavascriptInterface(mJavascriptInterface, JS_INTERFACE);
        templatePreview.loadUrl(PREVIEW_TEMPLATE_URL);

        mTemplateName = (EditText) root.findViewById(R.id.edit_cover_letter_name);
        mTemplateContent = (EditText) root.findViewById(R.id.edit_cover_letter_content);
        mTemplateContent.addTextChangedListener(mTextWatcher);

        if (mTemplateId != -1) {
            // retrieve template from database
            SqlAdapter adapter = Persistence.getAdapter(getActivity());
            Template template = new Template();
            template.setId(mTemplateId);
            template = adapter.findFirst(template);
            mTemplateServices = new ArrayList<TemplateService>(template.getTemplateServices());

            // set name and raw content
            mTemplateName.setText(template.getName());
            getActivity().setTitle(template.getName());
            String content = template.getContent();
            mTemplateContent.setText(content);
            updatePreview();
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
        template.setTemplateServices(mTemplateServices);
        return template;
    }

    public boolean isTemplateValid() {
        if (TextUtils.isEmpty(mTemplateName.getText().toString().trim())) {
            selectEditorTab();
            mTemplateName.setError(getString(R.string.cover_letter_name_is_empty));
            mTemplateName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(mTemplateContent.getText().toString().trim())) {
            selectEditorTab();
            mTemplateContent.setError(getString(R.string.cover_letter_content_is_empty));
            mTemplateContent.requestFocus();
            return false;
        }
        return true;
    }

    private void selectEditorTab() {
        if (getActivity() instanceof EditTemplateActivity) {
            EditTemplateActivity activity = (EditTemplateActivity) getActivity();
            activity.selectEditorTab();
        }
    }

    public void showEditor(boolean showEditor) {
        mShowEditor = showEditor;
        if (mViewSwitcher == null) {
            return;
        }
        mViewSwitcher.setDisplayedChild(showEditor ? EDITOR_MODE : PREVIEW_MODE);
    }

    public void addTemplateService(TemplateService templateService) {
        mTemplateServices.add(templateService);
        updatePreview();
    }
}
