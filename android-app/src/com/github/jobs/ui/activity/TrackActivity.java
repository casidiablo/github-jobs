package com.github.jobs.ui.activity;

import com.codeslap.topy.BaseActivity;
import com.github.jobs.utils.AnalyticsHelper;

/**
 * @author cristian
 * @version 1.0
 */
public class TrackActivity extends BaseActivity {

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsHelper.getTracker(this).onActivityStarted(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        // This is deprecated on Honeycomb+ but Analytics implementation requires it
        Object obj = super.onRetainCustomNonConfigurationInstance();
        AnalyticsHelper.getTracker(this).onActivityRetainNonConfigurationInstance();
        return obj;
    }

    @Override
    protected void onStop() {
        super.onStop();
        AnalyticsHelper.getTracker(this).onActivityStopped(this);
    }
}
