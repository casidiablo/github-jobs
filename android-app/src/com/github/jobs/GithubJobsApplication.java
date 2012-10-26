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
import com.github.jobs.bean.TemplateService;
import com.github.jobs.bean.Template;
import com.github.jobs.utils.AppUtils;
import com.parse.Parse;

public class GithubJobsApplication extends Application {

    private static final int DB_VERSION = 1;
    private static final String TAG = "github:jobs";
    private static final String CRITTERCISM_ID = "507d86b101ed855254000003";
    private static final String PARSE_APP_ID = "qdJF7c9mZR6B1GM1dmKCfeqprNixnlenDty6zV7i";
    private static final String PARSE_CLIENT_ID = "Cp596RrLZaF78CxG5keLHJlmbOBpLGZ5A1iWXvDk";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_ID);
        if (AppUtils.IN_DEVELOPMENT) {
            PersistenceLogManager.register(TAG);
        } else {
            Crittercism.init(getApplicationContext(), CRITTERCISM_ID);
        }
        // configure database
        DatabaseSpec databaseSpec = PersistenceConfig.registerSpec(DB_VERSION);
        databaseSpec.match(SearchesAndJobs.class);
        databaseSpec.matchNotAutoIncrement(Job.class);
        databaseSpec.match(new HasMany(Template.class, TemplateService.class));

        PersistenceConfig.getPreference().match(GeneralSettings.class);
    }
}
