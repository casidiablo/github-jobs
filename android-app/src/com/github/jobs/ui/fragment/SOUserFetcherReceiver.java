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

package com.github.jobs.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.github.jobs.GithubJobsApplication;
import com.github.jobs.R;
import com.github.jobs.bean.SOUser;
import com.github.jobs.events.HideKeyboardEvent;
import com.github.jobs.events.SOUsersUpdateEvent;
import com.github.jobs.resolver.StackOverflowUserTask;
import com.github.jobs.utils.AppUtils;
import com.squareup.otto.Bus;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnStart;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;
import java.util.ArrayList;
import javax.inject.Inject;

public class SOUserFetcherReceiver extends Fragment {

  public static final String TAG = SOUserFetcherReceiver.class.getSimpleName();

  @Inject Bus bus;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((GithubJobsApplication) getActivity().getApplication()).inject(this);
  }

  @OnFailure(StackOverflowUserTask.class) public void onError() {
    FragmentActivity activity = getActivity();
    if (activity == null || !isAdded()) {
      return;
    }
    if (!AppUtils.isOnline(activity)) {
      Toast.makeText(activity, R.string.error_fetching_search_result_network, Toast.LENGTH_LONG)
          .show();
    } else {
      Toast.makeText(activity, R.string.error_fetching_search_result, Toast.LENGTH_LONG).show();
    }
    changeProgress(false);
  }

  @OnSuccess(StackOverflowUserTask.class)
  public void onFinished(@Param(StackOverflowUserTask.RESULT_USERS) ArrayList<SOUser> users) {
    bus.post(new SOUsersUpdateEvent(users));
    changeProgress(false);
  }

  @OnStart(StackOverflowUserTask.class) public void onFetchStart() {
    bus.post(new HideKeyboardEvent());
    changeProgress(true);
  }

  private void changeProgress(boolean running) {
    FragmentActivity activity = getActivity();
    if (activity == null) {
      return;
    }
    activity.setProgressBarIndeterminateVisibility(running);
  }
}
