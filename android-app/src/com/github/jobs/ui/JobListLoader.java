package com.github.jobs.ui;

import android.content.Context;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.groundy.ListLoader;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.bean.SearchesAndJobs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cristian
 */
class JobListLoader extends ListLoader<Job> {

    private final SearchPack mCurrentSearch;

    public JobListLoader(Context context, SearchPack currentSearch) {
        super(context);
        mCurrentSearch = currentSearch;
    }

    @Override
    protected List<Job> getData() {
        SqlAdapter sqlAdapter = Persistence.getSqliteAdapter(getContext());
        if (mCurrentSearch.isDefault()) {
            return sqlAdapter.findAll(Job.class);
        }
        SearchesAndJobs sample = new SearchesAndJobs();
        sample.setSearchHashCode(mCurrentSearch.hashCode());
        List<SearchesAndJobs> searchesAndJobs = sqlAdapter.findAll(sample);
        if (searchesAndJobs.size() == 0) {
            return new ArrayList<Job>();
        }
        // create IN statement with all the jobs that should be retrieved
        StringBuilder in = new StringBuilder();
        String glue = "";
        for (SearchesAndJobs searchesAndJob : searchesAndJobs) {
            in.append(glue).append('\'').append(searchesAndJob.getJobId()).append('\'');
            glue = ", ";
        }
        String inStatement = in.toString();
        return sqlAdapter.findAll(Job.class, "_id IN (" + inStatement + ")", null);
    }
}
