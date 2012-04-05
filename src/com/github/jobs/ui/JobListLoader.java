package com.github.jobs.ui;

import android.content.Context;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.groundy.ListLoader;
import com.codeslap.persistence.Persistence;

import java.util.List;

/**
 * @author cristian
 */
public class JobListLoader extends ListLoader<Job> {

    public JobListLoader(Context context) {
        super(context);
    }

    @Override
    protected List<Job> getData() {
        return Persistence.quick(getContext()).findAll(Job.class);
    }
}
