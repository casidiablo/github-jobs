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

package com.github.jobs.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.github.jobs.GithubJobsApplication;
import com.github.jobs.bean.SearchPack;
import com.github.jobs.events.ProgressWheel;
import com.github.jobs.events.SearchError;
import com.github.jobs.events.SearchFinished;
import com.github.jobs.events.SearchProgressChanged;
import com.github.jobs.resolver.SearchJobsTask;
import com.github.jobs.utils.ViewUtils;
import com.squareup.otto.Bus;
import com.telly.groundy.DetachableResultReceiver;
import com.telly.groundy.Groundy;

import javax.inject.Inject;

/**
 * @author cristian
 */
public class SearchReceiver implements DetachableResultReceiver.Receiver {
  private boolean mSyncing;
  private final DetachableResultReceiver mReceiver;
  @Inject ViewUtils viewUtils;
  @Inject Bus bus;

  public SearchReceiver(SherlockFragmentActivity activity) {
    ((GithubJobsApplication) activity.getApplication()).inject(this);
    mReceiver = new DetachableResultReceiver(new Handler());
    mReceiver.setReceiver(this);
  }

  @Override
  public void onReceiveResult(int resultCode, Bundle resultData) {
    SearchPack searchPack = null;
    if (resultData.containsKey(SearchJobsTask.DATA_SEARCH_PACK)) {
      searchPack = (SearchPack) resultData.get(SearchJobsTask.DATA_SEARCH_PACK);
    }

    switch (resultCode) {
      case Groundy.STATUS_RUNNING: {
        mSyncing = true;
        break;
      }
      case Groundy.STATUS_FINISHED: {
        mSyncing = false;
        if (searchPack != null) {
          bus.post(new SearchFinished(searchPack, resultData));
        }
        break;
      }
      case Groundy.STATUS_ERROR: {
        mSyncing = false;
        viewUtils.toast(resultData.getString(Groundy.KEY_ERROR));
        if (searchPack != null) {
          bus.post(new SearchError(searchPack));
        }
        break;
      }
    }
    bus.post(new ProgressWheel(mSyncing));
    if (searchPack != null) {
      bus.post(new SearchProgressChanged(searchPack, mSyncing));
    }
  }

  public ResultReceiver getReceiver() {
    return mReceiver;
  }
}
