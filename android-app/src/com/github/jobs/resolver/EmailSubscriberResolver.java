package com.github.jobs.resolver;

import android.os.Bundle;
import com.github.jobs.api.GithubJobsApi;
import com.codeslap.groundy.CallResolver;
import com.codeslap.groundy.Groundy;
import com.github.jobs.bean.SearchPack;

/**
 * @author cristian
 */
public class EmailSubscriberResolver extends CallResolver {

    public static final String EXTRA_EMAIL = "com.github.jobs.extra_email";
    public static final String EXTRA_SEARCH = "com.github.jobs.extra_search";

    private boolean mResult;

    @Override
    protected void updateData() {
        Bundle parameters = getParameters();
        SearchPack searchPack = (SearchPack) parameters.getSerializable(EXTRA_SEARCH);
        String email = parameters.getString(EXTRA_EMAIL);
        String description = searchPack.getSearch();
        String location = searchPack.getLocation();
        boolean fullTime = searchPack.isFullTime();
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
