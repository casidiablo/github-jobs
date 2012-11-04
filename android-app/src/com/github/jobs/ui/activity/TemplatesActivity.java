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

import android.content.Intent;
import android.os.Bundle;
import com.github.jobs.R;
import com.github.jobs.ui.fragment.TemplatesListFragment;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplatesActivity extends TrackActivity {
    public static final int REQUEST_CODE = 773;
    public static final String EXTRA_PICK = "com.github.jobs.extra.pick";
    public static final String EXTRA_TEMPLATE_ID = "com.github.jobs.extra.template_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupBaseFragment(R.id.base_container, TemplatesListFragment.class);
    }

    public void setTemplateResultAndFinish(long id) {
        Intent data = new Intent();
        data.putExtra(EXTRA_TEMPLATE_ID, id);
        setResult(RESULT_OK, data);
        finish();
    }
}
