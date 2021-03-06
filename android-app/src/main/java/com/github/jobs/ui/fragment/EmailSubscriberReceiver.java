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

package com.github.jobs.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.github.jobs.GithubJobsApplication;
import com.github.jobs.R;
import com.github.jobs.events.EmailSubscriberProgress;
import com.github.jobs.resolver.EmailSubscriberTask;
import com.squareup.otto.Bus;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnStart;
import com.telly.groundy.annotations.OnSuccess;
import javax.inject.Inject;

/**
 * @author cristian
 * @version 1.0
 */
public class EmailSubscriberReceiver extends Fragment {
  public static final String TAG = EmailSubscriberReceiver.class.getSimpleName();
  @Inject Bus bus;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((GithubJobsApplication) getActivity().getApplication()).inject(this);
  }

  @OnStart(EmailSubscriberTask.class) public void onSubscriberStart() {
    showProgress(true);
  }

  @OnSuccess(EmailSubscriberTask.class) public void onFinished() {
    FragmentActivity activity = getActivity();
    if (activity == null || !isAdded()) {
      return;
    }
    Toast.makeText(activity, R.string.subscribed, Toast.LENGTH_LONG).show();
    activity.finish();
    showProgress(false);
  }

  @OnFailure(EmailSubscriberTask.class) public void onError() {
    FragmentActivity activity = getActivity();
    if (activity == null || !isAdded()) {
      return;
    }
    Toast.makeText(activity, R.string.error_subscribing, Toast.LENGTH_LONG).show();
    activity.finish();
    showProgress(false);
  }

  private void showProgress(boolean running) {
    bus.post(new EmailSubscriberProgress(running));
  }
}
