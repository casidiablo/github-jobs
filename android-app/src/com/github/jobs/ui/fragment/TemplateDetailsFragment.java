package com.github.jobs.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.ui.activity.TemplateDetailsActivity;
import com.petebevin.markdown.MarkdownProcessor;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplateDetailsFragment extends SherlockFragment {
    public static final String TAG = TemplateDetailsFragment.class.getSimpleName();
    private static final MarkdownProcessor MARKDOWN_PROCESSOR = new MarkdownProcessor();

    private TextView mTemplateName;
    private TextView mTemplateContent;
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
        mTemplateName = (TextView) getView().findViewById(R.id.lbl_template_name);
        mTemplateContent = (TextView) getView().findViewById(R.id.lbl_template_preview);

        onTemplateChanged();
    }

    public void onTemplateChanged() {
        // retrieve the template from the database
        Template template = new Template();
        template.setId(mTemplateId);
        template = mAdapter.findFirst(template);

        // set the template name
        mTemplateName.setText(template.getName());

        // set the template content
        String html = MARKDOWN_PROCESSOR.markdown(template.getContent());
        mTemplateContent.setText(Html.fromHtml(html));
    }
}
