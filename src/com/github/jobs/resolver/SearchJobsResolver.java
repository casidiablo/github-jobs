package com.github.jobs.resolver;

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

    @Override
    protected void updateData() {
        Search.Builder builder = new Search.Builder();
        builder.setSearch("android");
        builder.setFullTime(true);
        builder.createSearch();
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
