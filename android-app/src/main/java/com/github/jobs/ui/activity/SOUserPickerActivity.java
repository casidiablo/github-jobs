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

package com.github.jobs.ui.activity;

import android.os.Bundle;
import com.github.jobs.R;
import com.github.jobs.ui.fragment.SOUserPickerFragment;

/**
 * @author cristian
 * @version 1.0
 */
public class SOUserPickerActivity extends TrackActivity {
  public static final String EXTRA_SEARCH = "com.github.jobs.extra.search";
  public static final int REQUEST_CODE = 123;
  public static final String EXTRA_USER = "com.github.jobs.extra.user";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    setupBaseFragment(R.id.base_container, SOUserPickerFragment.class);
  }
}
