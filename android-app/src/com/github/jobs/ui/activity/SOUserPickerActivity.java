package com.github.jobs.ui.activity;

import android.os.Bundle;
import com.github.jobs.R;
import com.github.jobs.ui.fragment.SOUserPickerFragment;

/**
 * @author cristian
 * @version 1.0
 */
public class SOUserPickerActivity extends TrackActivity {
    public static final String EXTRA_SEARCH = "com.github.jobs.extra.search";
    public static final int REQUEST_CODE = 7623;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupBaseFragment(R.id.base_container, SOUserPickerFragment.class);
    }
}
