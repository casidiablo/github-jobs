package com.github.jobs.ui.activity;

import com.actionbarsherlock.view.MenuItem;
import com.codeslap.topy.BaseActivity;
import com.github.jobs.utils.AnalyticsHelper;
import com.github.jobs.utils.AppUtils;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                AppUtils.goHome(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
