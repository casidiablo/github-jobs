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

import com.github.jobs.api.GithubJobsApi;
import com.github.jobs.bean.SearchPack;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

public class EmailSubscriberTask extends GroundyTask {

  public static final String EXTRA_EMAIL = "com.github.jobs.extra.email";
  public static final String EXTRA_SEARCH = "com.github.jobs.extra.search";

  @Override
  protected TaskResult doInBackground() {
    Bundle parameters = getArgs();
    SearchPack searchPack = parameters.getParcelable(EXTRA_SEARCH);
    String subscribe = GithubJobsApi.INSTANCE.subscribe(
        parameters.getString(EXTRA_EMAIL),
        searchPack.getSearch(),
        searchPack.getLocation(),
        searchPack.isFullTime());
    return boolToResult("ok".equals(subscribe));
  }

  @Override
  protected boolean keepWifiOn() {
    return true;
  }
}
