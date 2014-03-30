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

package com.github.jobs.ui.activity;

import android.view.MenuItem;
import com.github.jobs.utils.AnalyticsHelper;
import com.github.jobs.utils.AppUtils;

/**
 * @author cristian
 * @version 1.0
 */
public class TrackActivity extends BaseActivity {

  @Override protected void onStart() {
    super.onStart();
    AnalyticsHelper.getTracker(this).onActivityStarted(this);
  }

  @Override protected void onStop() {
    super.onStop();
    AnalyticsHelper.getTracker(this).onActivityStopped(this);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    switch (itemId) {
      case android.R.id.home:
        AppUtils.goHome(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
