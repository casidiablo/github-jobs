package com.github.jobs.resolver;

import android.os.Bundle;
import com.codeslap.github.jobs.api.GithubJobsApi;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.github.jobs.api.Search;
import com.codeslap.groundy.CallResolver;
import com.codeslap.groundy.Groundy;
import com.codeslap.persistence.Persistence;

import java.util.List;

public class SearchJobsResolver extends CallResolver {

    private List<Job> mJobs;
    public static final String EXTRA_QUERY = "com.github.jobs.EXTRA_QUERY";
    public static final String EXTRA_LOCATION = "com.github.jobs.EXTRA_LOCATION";
    public static final String EXTRA_FULL_TIME = "com.github.jobs.EXTRA_FULL_TIME";

    @Override
    protected void updateData() {
        // get parameters
        Bundle params = getParameters();
        String query = params.getString(EXTRA_QUERY);
        String location = params.getString(EXTRA_LOCATION);
        boolean fullTime = params.getBoolean(EXTRA_FULL_TIME);

        // configure search
        Search.Builder builder = new Search.Builder();
        builder.setSearch(query);
        builder.setLocation(location);
        builder.setFullTime(fullTime);
        builder.createSearch();

        // execute search
        mJobs = GithubJobsApi.search(builder.createSearch());
        if (mJobs != null) {
            Persistence.quick(getContext()).storeUniqueCollection(mJobs, null);
        }
    }

    @Override
    protected void prepareResult() {
        if (mJobs != null) {
            setResultCode(Groundy.STATUS_FINISHED);
        }
    }

    @Override
    protected boolean keepWifiOn() {
        return true;
    }
}
