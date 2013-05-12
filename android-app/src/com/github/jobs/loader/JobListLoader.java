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

package com.github.jobs.loader;

import android.content.Context;
import android.util.Log;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.GithubJobsApplication;
import com.github.jobs.adapter.JobsAdapter;
import com.github.jobs.bean.Job;
import com.github.jobs.bean.SearchPack;
import com.github.jobs.bean.SearchesAndJobs;
import org.joda.time.DateTime;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author cristian
 */
public class JobListLoader extends SupportListLoader<Job> {

  private final SearchPack mCurrentSearch;
  @Inject SqlAdapter sqlAdapter;

  public JobListLoader(Context context, SearchPack currentSearch) {
    super(context);
    ((GithubJobsApplication) context.getApplicationContext()).inject(this);
    mCurrentSearch = currentSearch;
  }

  @Override protected List<Job> getData() {
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
      DateTime dateA;
      try {
        dateA = JobsAdapter.DATE_PARSER.withZoneUTC().parseDateTime(jobA.getCreatedAt());
      } catch (Exception e) {
        return 1;
      }
      DateTime dateB;
      try {
        dateB = JobsAdapter.DATE_PARSER.withZoneUTC().parseDateTime(jobB.getCreatedAt());
      } catch (Exception e) {
        return -1;
      }
      return dateB.compareTo(dateA);
    }
  };
}
