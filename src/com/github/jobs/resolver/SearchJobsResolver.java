package com.github.jobs.resolver;

import android.os.Bundle;
import com.codeslap.github.jobs.api.GithubJobsApi;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.github.jobs.api.Search;
import com.codeslap.groundy.CallResolver;
import com.codeslap.groundy.Groundy;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;

import java.util.List;

public class SearchJobsResolver extends CallResolver {

    private List<Job> mJobs;
    public static final String EXTRA_QUERY = "com.github.jobs.extra_query";
    public static final String EXTRA_LOCATION = "com.github.jobs.extra_location";
    public static final String EXTRA_FULL_TIME = "com.github.jobs.extra_full_time";
    public static final String EXTRA_PAGE = "com.github.jobs.extra_page";
    public static final String DATA_ITEMS = "com.github.jobs.data_times";

    @Override
    protected void updateData() {
        // get parameters
        Bundle params = getParameters();
        String query = params.getString(EXTRA_QUERY);
        String location = params.getString(EXTRA_LOCATION);
        boolean fullTime = params.getBoolean(EXTRA_FULL_TIME, true);
        int page = params.getInt(EXTRA_PAGE, 0);

        // configure search
        Search.Builder builder = new Search.Builder();
        builder.setSearch(query);
        builder.setLocation(location);
        builder.setFullTime(fullTime);
        if (page > 0) {
            builder.setPage(page);
        }
        builder.createSearch();

        // execute search
        mJobs = GithubJobsApi.search(builder.createSearch());
        if (mJobs != null) {
            SqlAdapter sqlAdapter = Persistence.getSqliteAdapter(getContext());
            // delete old content
            if (page == 0 && mJobs.size() > 0) {
                sqlAdapter.delete(Job.class, null, null);
            }
            sqlAdapter.storeCollection(mJobs, null);
        }
    }

    @Override
    protected void prepareResult() {
        if (mJobs != null) {
            Bundle resultData = new Bundle();
            resultData.putInt(DATA_ITEMS, mJobs.size());
            setResultData(resultData);
            setResultCode(Groundy.STATUS_FINISHED);
        }
    }

    @Override
    protected boolean keepWifiOn() {
        return true;
    }
}
