package com.github.jobs.ui.activity;

import android.os.Bundle;
import com.codeslap.topy.BaseActivity;
import com.github.jobs.utils.AnalyticsHelper;

/**
 * @author cristian
 * @version 1.0
 */
public class TrackActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsHelper.getTracker().onActivityCreated(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsHelper.getTracker().onActivityStarted(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        // This is deprecated on Honeycomb+ but Analytics implementation requires it
        Object obj = super.onRetainCustomNonConfigurationInstance();
        AnalyticsHelper.getTracker().onActivityRetainNonConfigurationInstance();
        return obj;
    }

    @Override
    protected void onStop() {
        super.onStop();
        AnalyticsHelper.getTracker().onActivityStopped(this);
    }
}
