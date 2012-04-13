package com.github.jobs;

import android.app.Application;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.persistence.PersistenceConfig;
import com.codeslap.persistence.SqlPersistence;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // configure database
        PersistenceConfig.getDatabase(getPackageName(), 1).matchNotAutoIncrement(Job.class);
    }
}
