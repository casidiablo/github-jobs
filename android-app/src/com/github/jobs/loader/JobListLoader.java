package com.github.jobs.loader;

import android.content.Context;
import android.util.Log;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.groundy.ListLoader;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.adapter.JobsAdapter;
import com.github.jobs.bean.SearchPack;
import com.github.jobs.bean.SearchesAndJobs;

import java.text.ParseException;
import java.util.*;

/**
 * @author cristian
 */
public class JobListLoader extends ListLoader<Job> {

    private final SearchPack mCurrentSearch;

    public JobListLoader(Context context, SearchPack currentSearch) {
        super(context);
        mCurrentSearch = currentSearch;
    }

    @Override
    protected List<Job> getData() {
        SqlAdapter sqlAdapter = Persistence.getAdapter(getContext());
        if (mCurrentSearch.isDefault()) {
            return sort(sqlAdapter.findAll(Job.class));
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
        return sort(sqlAdapter.findAll(Job.class, "_id IN (" + inStatement + ")", null));
    }

    private List<Job> sort(List<Job> jobs) {
        if (jobs == null) {
            return null;
        }
        try {
            Collections.sort(jobs, JOB_COMPARATOR);
        } catch (Exception e) {
            Log.wtf("jobs:listLoader", "General contract should not be wrong :-/", e);
        }
        return jobs;
    }

    private static final Comparator<Job> JOB_COMPARATOR = new Comparator<Job>() {
        @Override
        public int compare(Job jobA, Job jobB) {
            if (jobA == null) {
                return -1;
            }
            if (jobB == null) {
                return 1;
            }
            Date dateA;
            try {
                dateA = JobsAdapter.DATE_PARSER.parse(jobA.getCreatedAt());
            } catch (ParseException e) {
                return 1;
            }
            Date dateB;
            try {
                dateB = JobsAdapter.DATE_PARSER.parse(jobB.getCreatedAt());
            } catch (ParseException e) {
                return -1;
            }
            return dateB.compareTo(dateA);
        }
    };
}
