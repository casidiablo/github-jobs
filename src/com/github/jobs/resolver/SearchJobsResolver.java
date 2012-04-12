package com.github.jobs.resolver;

import android.os.Bundle;
import com.codeslap.github.jobs.api.GithubJobsApi;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.github.jobs.api.Search;
import com.codeslap.groundy.CallResolver;
import com.codeslap.groundy.Groundy;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.ui.SearchPack;

import java.util.ArrayList;
import java.util.List;

public class SearchJobsResolver extends CallResolver {

    private ArrayList<Job> mJobs;
    public static final String EXTRA_SEARCH_PACK = "com.github.jobs.extra_search_pack";
    public static final String DATA_JOBS = "com.github.jobs.data_jobs";
    public static final String DATA_SEARCH_PACK = "com.github.jobs.data_search_pack";

    @Override
    protected void updateData() {
        // get parameters
        Bundle params = getParameters();
        SearchPack searchPack = (SearchPack) params.getSerializable(EXTRA_SEARCH_PACK);

        // configure search
        Search.Builder builder = new Search.Builder();
        builder.setSearch(searchPack.search);
        builder.setLocation(searchPack.location);
        builder.setFullTime(searchPack.fullTime);
        if (searchPack.page > 0) {
            builder.setPage(searchPack.page);
        }
        builder.createSearch();

        // execute search
        List<Job> jobsList = GithubJobsApi.search(builder.createSearch());
        if (jobsList == null) {
            return;
        }
        mJobs = new ArrayList<Job>(jobsList);
        if (searchPack.isDefault()) {
            SqlAdapter sqlAdapter = Persistence.getSqliteAdapter(getContext());
            // delete old content
            if (searchPack.page == 0 && mJobs.size() > 0) {
                sqlAdapter.delete(Job.class, null, null);
            }
            sqlAdapter.storeCollection(mJobs, null);
        }
    }

    @Override
    protected void prepareResult() {
        if (mJobs == null) {
            return;
        }
        Bundle resultData = new Bundle();
        Bundle params = getParameters();
        SearchPack searchPack = (SearchPack) params.getSerializable(EXTRA_SEARCH_PACK);
        resultData.putSerializable(DATA_SEARCH_PACK, searchPack);
        resultData.putSerializable(DATA_JOBS, mJobs);
        setResultData(resultData);
        setResultCode(Groundy.STATUS_FINISHED);
    }

    @Override
    protected boolean keepWifiOn() {
        return true;
    }
}
