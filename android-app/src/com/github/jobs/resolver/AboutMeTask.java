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

import com.github.jobs.bean.AboutMeUser;
import com.github.jobs.templates.fetcher.AboutMeFetcher;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

/**
 * @author cristian
 * @version 1.0
 */
public class AboutMeTask extends GroundyTask {
  public static final String PARAM_USERNAME = "com.github.jobs.param.username";
  public static final String RESULT_USER = "com.github.jobs.result.user";

  @Override
  protected TaskResult doInBackground() {
    String username = getStringArg(PARAM_USERNAME);

    AboutMeFetcher aboutMeFetcher = new AboutMeFetcher();
    AboutMeUser aboutMeUser = aboutMeFetcher.getAboutMeUser(username);

    if (aboutMeUser == null || aboutMeUser.getServices() == null || aboutMeUser.getServices().length == 0) {
      return failed();
    }

    return succeeded().add(RESULT_USER, aboutMeUser);
  }
}
