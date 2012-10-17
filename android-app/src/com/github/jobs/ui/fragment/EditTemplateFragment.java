package com.github.jobs.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragment;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.ui.activity.TemplateDetailsActivity;
import com.github.jobs.utils.AppUtils;
import com.petebevin.markdown.MarkdownProcessor;

/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateFragment extends SherlockFragment {
    public static final String TAG = EditTemplateFragment.class.getSimpleName();
    private static final MarkdownProcessor MARKDOWN_PROCESSOR = new MarkdownProcessor();

    private WebView mTemplatePreview;
    private EditText mTemplateContent;
    private EditText mTemplateName;
    private long mTemplateId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_template, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTemplatePreview = (WebView) getView().findViewById(R.id.lbl_template_preview);
        AppUtils.setupWebview(mTemplatePreview);
        mTemplatePreview.setOnTouchListener(mPreviewTouchListener);

        mTemplateContent = (EditText) getView().findViewById(R.id.edit_template_content);
        mTemplateContent.addTextChangedListener(mTextWatcher);

        mTemplateName = (EditText) getView().findViewById(R.id.edit_template_name);

        mTemplateId = getActivity().getIntent().getLongExtra(TemplateDetailsActivity.EXTRA_TEMPLATE_ID, -1);
        if (mTemplateId != -1) {
            // retrieve template from database
            SqlAdapter adapter = Persistence.getAdapter(getActivity());
            Template template = new Template();
            template.setId(mTemplateId);
            template = adapter.findFirst(template);

            mTemplateName.setText(template.getName());
            mTemplateContent.setText(template.getContent());
            String html = MARKDOWN_PROCESSOR.markdown(template.getContent());
            mTemplatePreview.loadData(html, "text/html", "utf-8");
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
            String html = MARKDOWN_PROCESSOR.markdown(text).trim();
            mTemplatePreview.loadData(html, "text/html", "utf-8");
        }
    };

    public Template buildTemplate() {
        // build the template
        Template template = new Template();
        if (mTemplateId > 0) {
            template.setId(mTemplateId);
        }
        template.setName(mTemplateName.getText().toString().trim());
        template.setContent(mTemplateContent.getText().toString().trim());
        template.setLastUpdate(System.currentTimeMillis());

//        // create the list of services
//        ArrayList<Service> services = new ArrayList<Service>();
//        Service service = new Service();
//        service.setType("github");
//        service.setData("casidiablo");
//        services.add(service);
//        template.setServices(services);

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
}
