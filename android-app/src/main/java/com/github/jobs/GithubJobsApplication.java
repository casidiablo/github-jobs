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
import android.content.Context;

import com.codeslap.persistence.DatabaseSpec;
import com.codeslap.persistence.HasMany;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.PersistenceConfig;
import com.codeslap.persistence.PersistenceLogManager;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.bean.GeneralSettings;
import com.github.jobs.bean.Job;
import com.github.jobs.bean.SearchesAndJobs;
import com.github.jobs.bean.Template;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.loader.JobListLoader;
import com.github.jobs.loader.TemplatesLoader;
import com.github.jobs.resolver.SearchJobsTask;
import com.github.jobs.ui.activity.EditTemplateActivity;
import com.github.jobs.ui.activity.HomeActivity;
import com.github.jobs.ui.activity.JobDetailsActivity;
import com.github.jobs.ui.activity.SOUserPickerActivity;
import com.github.jobs.ui.activity.TemplatesActivity;
import com.github.jobs.ui.dialog.DeleteTemplateDialog;
import com.github.jobs.ui.dialog.HowToApplyDialog;
import com.github.jobs.ui.dialog.RemoveServicesDialog;
import com.github.jobs.ui.dialog.SubscribeDialog;
import com.github.jobs.ui.fragment.EditTemplateFragment;
import com.github.jobs.ui.fragment.EmailSubscriberReceiver;
import com.github.jobs.ui.fragment.JobDetailsFragment;
import com.github.jobs.ui.fragment.JobListFragment;
import com.github.jobs.ui.fragment.SOUserFetcherReceiver;
import com.github.jobs.ui.fragment.SOUserPickerFragment;
import com.github.jobs.utils.AppUtils;
import com.github.jobs.utils.ViewUtils;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

public class GithubJobsApplication extends Application {

  private static final int DB_VERSION = 1;
  private static final String TAG = "github:jobs";

  private ObjectGraph objectGraph;

  @Override
  public void onCreate() {
    super.onCreate();
    objectGraph = ObjectGraph.create(new GithubJobsModule(this));
    if (AppUtils.IN_DEVELOPMENT) {
      PersistenceLogManager.register(TAG);
    }
    // configure database
    DatabaseSpec databaseSpec = PersistenceConfig.registerSpec(DB_VERSION);
    databaseSpec.match(SearchesAndJobs.class);
    databaseSpec.matchNotAutoIncrement(Job.class);
    databaseSpec.match(new HasMany(Template.class, TemplateService.class));

    PersistenceConfig.getPreference().match(GeneralSettings.class);
  }

  public void inject(Object object) {
    objectGraph.inject(object);
  }

  @Module(injects = {
      HomeActivity.class, //
      EditTemplateActivity.class, //
      TemplatesActivity.class, //
      SOUserFetcherReceiver.class, //
      SOUserPickerFragment.class, //
      JobListFragment.class, //
      EditTemplateFragment.class, //
      JobDetailsActivity.class, //
      RemoveServicesDialog.class, //
      DeleteTemplateDialog.class, //
      SOUserPickerActivity.class, //
      JobListLoader.class, //
      TemplatesLoader.class, //
      JobDetailsFragment.class, //
      HowToApplyDialog.class, //
      EmailSubscriberReceiver.class, //
      SubscribeDialog.class, //
      SearchJobsTask.class
  })
  static class GithubJobsModule {
    private final Context context;

    private GithubJobsModule(Context context) {
      this.context = context;
    }

    @Provides @Singleton Bus provideBus() {
      return new Bus();
    }

    @Provides @Singleton ViewUtils provideViewUtils() {
      return new ViewUtils(context);
    }

    @Provides @Singleton SqlAdapter provideSqlAdapter() {
      return Persistence.getAdapter(context);
    }
  }
}