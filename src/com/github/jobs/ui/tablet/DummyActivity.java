package com.github.jobs.ui.tablet;

import android.os.Bundle;
import com.codeslap.topy.BaseMultiPaneActivity;
import com.github.jobs.ui.DummyFragment;
import com.github.jobs.ui.FooBarFragment;

public class DummyActivity extends BaseMultiPaneActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getTwoColumns(new DummyFragment(), 2, new FooBarFragment(), 1));
    }

    @Override
    protected FragmentReplaceInfo onSubstituteFragmentForActivityLaunch(String activityClassName) {
        return null;
    }
}
