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

package com.github.jobs.resolver;

import android.os.Bundle;
import com.github.jobs.api.GithubJobsApi;
import com.github.jobs.bean.SearchPack;
import com.telly.groundy.GroundyTask;

/**
 * @author cristian
 */
public class EmailSubscriberTask extends GroundyTask {

  public static final String EXTRA_EMAIL = "com.github.jobs.extra.email";
  public static final String EXTRA_SEARCH = "com.github.jobs.extra.search";

  @Override
  protected boolean doInBackground() {
    Bundle parameters = getParameters();
    SearchPack searchPack = (SearchPack) parameters.getParcelable(EXTRA_SEARCH);
    String email = parameters.getString(EXTRA_EMAIL);
    String description = searchPack.getSearch();
    String location = searchPack.getLocation();
    boolean fullTime = searchPack.isFullTime();
    return GithubJobsApi.subscribe(email, description, location, fullTime);
  }

  @Override
  protected boolean keepWifiOn() {
    return true;
  }
}
