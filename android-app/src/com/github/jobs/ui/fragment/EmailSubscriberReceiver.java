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
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.github.jobs.R;
import com.github.jobs.ui.dialog.SubscribeDialog;
import com.telly.groundy.ReceiverFragment;

/**
 * @author cristian
 * @version 1.0
 */
public class EmailSubscriberReceiver extends ReceiverFragment {
  @Override
  protected void onFinished(Bundle resultData) {
    super.onFinished(resultData);
    FragmentActivity activity = getActivity();
    if (activity == null || !isAdded()) {
      return;
    }
    Toast.makeText(activity, R.string.subscribed, Toast.LENGTH_LONG).show();
    activity.finish();
  }

  @Override
  protected void onError(Bundle resultData) {
    super.onError(resultData);
    FragmentActivity activity = getActivity();
    if (activity == null || !isAdded()) {
      return;
    }
    Toast.makeText(activity, R.string.error_subscribing, Toast.LENGTH_LONG).show();
    activity.finish();
  }

  @Override
  protected void onProgressChanged(boolean running) {
    FragmentActivity activity = getActivity();
    if (activity instanceof SubscribeDialog) {
      ((SubscribeDialog) activity).progress(running);
    }
  }
}
