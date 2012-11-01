package com.github.jobs.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import com.github.jobs.R;
import com.github.jobs.ui.fragment.TemplatesListFragment;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplatesActivity extends TrackActivity {
    public static final int REQUEST_CODE = 773;
    public static final String EXTRA_PICK = "com.github.jobs.extra.pick";
    public static final String EXTRA_TEMPLATE_ID = "com.github.jobs.extra.template_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupBaseFragment(R.id.base_container, TemplatesListFragment.class);
    }

    public void setTemplateResultAndFinish(long id) {
        Intent data = new Intent();
        data.putExtra(EXTRA_TEMPLATE_ID, id);
        setResult(RESULT_OK, data);
        finish();
    }
}
