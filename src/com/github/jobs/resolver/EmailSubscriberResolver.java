package com.github.jobs.resolver;

import android.os.Bundle;
import com.codeslap.github.jobs.api.GithubJobsApi;
import com.codeslap.groundy.CallResolver;
import com.codeslap.groundy.Groundy;

/**
 * @author cristian
 */
public class EmailSubscriberResolver extends CallResolver {

    public static final String EXTRA_EMAIL = "com.github.jobs.EXTRA_EMAIL";
    public static final String EXTRA_DESCRIPTION = "com.github.jobs.EXTRA_DESCRIPTION";
    public static final String EXTRA_LOCATION = "com.github.jobs.EXTRA_LOCATION";
    public static final String EXTRA_FULL_TIME = "com.github.jobs.EXTRA_FULL_TIME";

    private boolean mResult;

    @Override
    protected void updateData() {
        Bundle parameters = getParameters();
        String email = parameters.getString(EXTRA_EMAIL);
        String description = parameters.getString(EXTRA_DESCRIPTION);
        String location = parameters.getString(EXTRA_LOCATION);
        boolean fullTime = parameters.getBoolean(EXTRA_FULL_TIME, true);
        mResult = GithubJobsApi.subscribe(email, description, location, fullTime);
    }

    @Override
    protected void prepareResult() {
        if (mResult) {
            setResultCode(Groundy.STATUS_FINISHED);
        }
    }

    @Override
    protected boolean keepWifiOn() {
        return true;
    }
}
