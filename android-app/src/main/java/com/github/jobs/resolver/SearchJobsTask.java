/*
 * Copyright 2014 Some Dev
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

package com.github.jobs.resolver;

import android.os.Bundle;

import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.GithubJobsApplication;
import com.github.jobs.api.GithubJobsApi;
import com.github.jobs.bean.Job;
import com.github.jobs.bean.SearchPack;
import com.github.jobs.bean.SearchesAndJobs;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SearchJobsTask extends GroundyTask {

  public static final String EXTRA_SEARCH_PACK = "com.github.jobs.extra_search_pack";
  public static final String DATA_JOBS = "com.github.jobs.data.jobs";
  public static final String DATA_SEARCH_PACK = "com.github.jobs.data.search_pack";
  @Inject
  SqlAdapter sqlAdapter;

  @Override
  protected TaskResult doInBackground() {
    ((GithubJobsApplication) getContext().getApplicationContext()).inject(this);
    // get parameters
    Bundle params = getArgs();
    SearchPack searchPack = params.getParcelable(EXTRA_SEARCH_PACK);

    // execute search
    List<Job> jobsList = GithubJobsApi.INSTANCE.search(searchPack.getSearch(),
        searchPack.getLocation(),
        searchPack.getPage(),
        searchPack.isFullTime());
    if (jobsList == null) {
      return failed();
    }

    ArrayList<Job> jobs = new ArrayList<Job>(jobsList);
    // delete old content
    if (searchPack.getPage() == 0 && jobs.size() > 0) {
      if (searchPack.isDefault()) {
        sqlAdapter.delete(Job.class, null, null);
      } else {
        SearchesAndJobs sample = new SearchesAndJobs();
        sample.setSearchHashCode(searchPack.hashCode());
        sqlAdapter.delete(sample);
      }
    }
    // if the search is not the default one, references to cache table too
    if (!searchPack.isDefault()) {
      List<SearchesAndJobs> searchCaches = new ArrayList<SearchesAndJobs>();
      for (Job job : jobs) {
        SearchesAndJobs searchesAndJobs = new SearchesAndJobs();
        searchesAndJobs.setSearchHashCode(searchPack.hashCode());
        searchesAndJobs.setJobId(job.getId());
        searchCaches.add(searchesAndJobs);
      }
      sqlAdapter.storeCollection(searchCaches, null);
    }
    // persist all data to be able to use it later
    sqlAdapter.storeCollection(jobs, null);

    // prepare result
    Bundle resultData = new Bundle();
    resultData.putParcelable(DATA_SEARCH_PACK, searchPack);
    resultData.putParcelableArrayList(DATA_JOBS, jobs);
    return succeeded().addAll(resultData);
  }

  @Override
  protected boolean keepWifiOn() {
    return true;
  }
}
