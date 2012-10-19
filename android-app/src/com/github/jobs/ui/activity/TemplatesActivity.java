package com.github.jobs.ui.activity;

import android.os.Bundle;
import com.github.jobs.R;
import com.github.jobs.ui.fragment.TemplatesListFragment;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplatesActivity extends TrackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupBaseFragment(R.id.base_container, TemplatesListFragment.class);
    }
}
