package com.github.jobs.ui.dialog;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.jobs.utils.AnalyticsHelper;

/**
 * @author cristian
 * @version 1.0
 */
public class TrackDialog extends SherlockActivity {

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsHelper.getTracker(this).onActivityStarted(this);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        // This is deprecated on Honeycomb+ but Analytics implementation requires it
        Object obj = super.onRetainNonConfigurationInstance();
        AnalyticsHelper.getTracker(this).onActivityRetainNonConfigurationInstance();
        return obj;
    }

    @Override
    protected void onStop() {
        super.onStop();
        AnalyticsHelper.getTracker(this).onActivityStopped(this);
    }
}
