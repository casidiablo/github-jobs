package com.github.jobs;

import android.app.Application;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.persistence.PersistenceConfig;
import com.codeslap.persistence.SqlPersistence;
import com.github.jobs.bean.SearchesAndJobs;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // configure database
        SqlPersistence database = PersistenceConfig.getDatabase(getPackageName(), 1);
        database.match(SearchesAndJobs.class);
        database.matchNotAutoIncrement(Job.class);
    }
}
