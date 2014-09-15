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

import com.github.jobs.bean.AboutMeService;
import com.github.jobs.bean.AboutMeUser;
import com.github.jobs.templates.apis.AboutMeApi;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.util.ArrayList;
import java.util.List;

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

    AboutMeUser aboutMeUser = AboutMeApi.INSTANCE.getAboutMeUser(username);
    if (aboutMeUser != null && aboutMeUser.getServices() != null) {
      // check if there is a service that should be removed
      List<AboutMeService> toKeep = new ArrayList<AboutMeService>();
      for (AboutMeService service : aboutMeUser.getServices()) {
        if (service.getServiceUrl() != null) {
          toKeep.add(service);
        }
      }
      // recreate array with only complete services
      aboutMeUser.setServices(toKeep.toArray(new AboutMeService[toKeep.size()]));
    }

    if (aboutMeUser == null
        || aboutMeUser.getServices() == null
        || aboutMeUser.getServices().length == 0) {
      return failed();
    }

    return succeeded().add(RESULT_USER, aboutMeUser);
  }
}
