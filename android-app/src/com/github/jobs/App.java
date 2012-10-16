package com.github.jobs;

import android.app.Application;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.persistence.DatabaseSpec;
import com.codeslap.persistence.PersistenceConfig;
import com.crittercism.app.Crittercism;
import com.github.jobs.bean.SearchesAndJobs;

public class App extends Application {

    private static final int DB_VERSION = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Crittercism.init(getApplicationContext(), "507d86b101ed855254000003");
        // configure database
        DatabaseSpec databaseSpec = PersistenceConfig.registerSpec(DB_VERSION);
        databaseSpec.match(SearchesAndJobs.class);
        databaseSpec.matchNotAutoIncrement(Job.class);
    }
}
