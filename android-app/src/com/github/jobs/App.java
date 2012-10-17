package com.github.jobs;

import android.app.Application;
import com.codeslap.persistence.DatabaseSpec;
import com.codeslap.persistence.HasMany;
import com.codeslap.persistence.PersistenceConfig;
import com.codeslap.persistence.PersistenceLogManager;
import com.crittercism.app.Crittercism;
import com.github.bean.Job;
import com.github.jobs.bean.GeneralSettings;
import com.github.jobs.bean.SearchesAndJobs;
import com.github.jobs.bean.Service;
import com.github.jobs.bean.Template;
import com.github.jobs.utils.AppUtils;

public class App extends Application {

    private static final int DB_VERSION = 1;
    private static final String TAG = "github:jobs";
    private static final String CRITTERCISM_ID = "507d86b101ed855254000003";

    @Override
    public void onCreate() {
        super.onCreate();
        if (AppUtils.IN_DEVELOPMENT) {
            PersistenceLogManager.register(TAG);
        } else {
            Crittercism.init(getApplicationContext(), CRITTERCISM_ID);
        }
        // configure database
        DatabaseSpec databaseSpec = PersistenceConfig.registerSpec(DB_VERSION);
        databaseSpec.match(SearchesAndJobs.class);
        databaseSpec.matchNotAutoIncrement(Job.class);
        databaseSpec.match(new HasMany(Template.class, Service.class));

        PersistenceConfig.getPreference().match(GeneralSettings.class);
    }
}
