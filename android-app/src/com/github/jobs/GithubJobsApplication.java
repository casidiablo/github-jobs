/*
 * Copyright 2012 CodeSlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.github.jobs.bean.Template;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.utils.AppUtils;

public class GithubJobsApplication extends Application {

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
        databaseSpec.match(new HasMany(Template.class, TemplateService.class));

        PersistenceConfig.getPreference().match(GeneralSettings.class);
    }
}
