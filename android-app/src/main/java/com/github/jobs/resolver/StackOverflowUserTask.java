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

import com.github.jobs.bean.SOSearchResponse;
import com.github.jobs.bean.SOUser;
import com.github.jobs.templates.apis.StackOverflowApi;
import com.telly.groundy.GroundyTask;
import com.telly.groundy.TaskResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cristian
 * @version 1.0
 */
public class StackOverflowUserTask extends GroundyTask {
  public static final String EXTRA_SEARCH = "com.github.jobs.extra.search";
  public static final String RESULT_USERS = "com.github.jobs.result.users";

  @Override
  protected TaskResult doInBackground() {
    String search = getStringArg(EXTRA_SEARCH);

    SOSearchResponse soSearchResponse = StackOverflowApi.INSTANCE.findUser(search);
    List<SOUser> users = soSearchResponse.getItems();

    if (users == null) {
      return failed(); // something went wrong :-/
    }

    // pack the result in an parcelable array list
    ArrayList<SOUser> SOUsers = new ArrayList<SOUser>(users);
    return succeeded().add(RESULT_USERS, SOUsers); // everything went fine :)
  }
}
