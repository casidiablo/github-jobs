package com.github.jobs.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Service;
import com.github.jobs.bean.Template;
import com.github.jobs.ui.activity.TemplateDetailsActivity;
import com.petebevin.markdown.MarkdownProcessor;

import java.util.ArrayList;

/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateFragment extends SherlockFragment implements View.OnClickListener {
    public static final String TAG = EditTemplateFragment.class.getSimpleName();
    private static final String P_STARTS = "<p>";
    private static final String P_ENDS = "</p>";
    private static final MarkdownProcessor MARKDOWN_PROCESSOR = new MarkdownProcessor();

    private TextView mTemplatePreview;
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
        mTemplatePreview = (TextView) getView().findViewById(R.id.lbl_template_preview);
        mTemplatePreview.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTemplatePreview.setOnClickListener(this);

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
            mTemplatePreview.setText(Html.fromHtml(html));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lbl_template_preview:
                mTemplateContent.requestFocus();
                mTemplateContent.setSelection(mTemplateContent.getText().length());
                // show the keyboard so that user can start editing this
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mTemplateContent, InputMethodManager.SHOW_IMPLICIT);
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
            String html = MARKDOWN_PROCESSOR.markdown(text).trim();
            // remove last paragraph
            if (html.endsWith(P_ENDS)) {
                int i = html.lastIndexOf(P_STARTS);
                html = html.substring(0, i) + html.substring(i + P_STARTS.length(), html.length() - P_ENDS.length());
            }
            mTemplatePreview.setText(Html.fromHtml(html));
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

        // create the list of services
        ArrayList<Service> services = new ArrayList<Service>();
        Service service = new Service();
        service.setType("github");
        service.setData("casidiablo");
        services.add(service);
        template.setServices(services);

        return template;
    }
}
