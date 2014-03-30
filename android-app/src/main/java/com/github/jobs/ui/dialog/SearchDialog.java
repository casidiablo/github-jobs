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

package com.github.jobs.ui.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.github.jobs.R;

import static com.github.jobs.utils.AnalyticsHelper.*;

public class SearchDialog extends TrackDialog implements View.OnClickListener {

  public static final String EXTRA_DESCRIPTION = "description";
  public static final String EXTRA_LOCATION = "location";
  public static final String EXTRA_FULL_TIME = "full_time";

  private EditText mDescription, mLocation;
  private CheckBox mFullTime;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getTracker(this).trackPageView(NAME_SEARCH_DIALOG);
    setContentView(R.layout.search_settings);

    mDescription = (EditText) findViewById(R.id.edit_description);
    mLocation = (EditText) findViewById(R.id.edit_location);
    mFullTime = (CheckBox) findViewById(R.id.checkbox_full_time);

    findViewById(R.id.btn_search).setOnClickListener(this);
    findViewById(R.id.btn_cancel).setOnClickListener(this);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_search:
        // validate data
        if (TextUtils.isEmpty(mDescription.getText())) {
          mDescription.setError(getString(R.string.must_specify_description));
          return;
        }
        // everything looks ok, let's search
        String description = mDescription.getText().toString();
        String location = mLocation.getText().toString();
        Intent data = new Intent().putExtra(EXTRA_DESCRIPTION, description)
            .putExtra(EXTRA_LOCATION, location)
            .putExtra(EXTRA_FULL_TIME, mFullTime.isChecked());
        setResult(RESULT_OK, data);
        getTracker(this).trackEvent(CATEGORY_SEARCH, ACTION_SEARCH, description + "," + location);
        break;
      case R.id.btn_cancel:
        getTracker(this).trackEvent(CATEGORY_SEARCH, ACTION_CANCEL, LABEL_DIALOG);
        setResult(RESULT_CANCELED);
        break;
    }
    finish();
  }
}
