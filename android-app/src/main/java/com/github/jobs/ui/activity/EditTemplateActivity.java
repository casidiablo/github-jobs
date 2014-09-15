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

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import com.github.jobs.R;
import com.github.jobs.events.ConfigureActionBar;
import com.github.jobs.events.DisableEditMode;
import com.github.jobs.events.FinishCurrentActivity;
import com.github.jobs.events.SelectEditorTab;
import com.github.jobs.events.ShowTemplateEditor;
import com.github.jobs.ui.fragment.EditTemplateFragment;
import com.github.jobs.utils.AppUtils;
import com.github.jobs.utils.TabListenerAdapter;
import com.squareup.otto.Subscribe;

/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateActivity extends TrackActivity {

  // used to start this activity with startActivityForResult
  public static final int REQUEST_CODE = 7874;

  // used to save the state of this activity
  private static final String KEY_CURRENT_TAB = "com.github.jobs.key.current_tab";

  // used to pass a template id to edit
  public static final String EXTRA_TEMPLATE_ID = "com.github.jobs.extra.template_id";

  private int mCurrentTab;
  private boolean mAlreadyConfigured;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    if (savedInstanceState != null) {
      mCurrentTab = savedInstanceState.getInt(KEY_CURRENT_TAB);
    }

    long templateId = getIntent().getLongExtra(EditTemplateActivity.EXTRA_TEMPLATE_ID, -1);

    Bundle args = new Bundle();
    args.putLong(EditTemplateFragment.ARG_TEMPLATE_ID, templateId);
    setupBaseFragment(R.id.base_container, EditTemplateFragment.class, args);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    ActionBar.Tab selectedTab = getActionBar().getSelectedTab();
    if (selectedTab != null) {
      mCurrentTab = selectedTab.getPosition();
      outState.putInt(KEY_CURRENT_TAB, mCurrentTab);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    switch (itemId) {
      case android.R.id.home:
        AppUtils.goTemplatesList(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Subscribe public void selectEditorTab(SelectEditorTab selectEditorTab) {
    ActionBar supportActionBar = getActionBar();
    ActionBar.Tab selectedTab = supportActionBar.getSelectedTab();
    if (selectedTab.getPosition() != 0) {
      supportActionBar.getTabAt(0).select();
    }
  }

  @Subscribe public void finishActivity(FinishCurrentActivity finishCurrentActivity) {
    setResult(RESULT_OK);
    finish();
  }

  @Subscribe public void configureActionBar(ConfigureActionBar configureActionBar) {
    if (mAlreadyConfigured) {
      return;
    }
    mAlreadyConfigured = true;
    // let's create edit tabs!
    ActionBar actionBar = getActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    ActionBar.Tab editorTab = actionBar.newTab();
    editorTab.setText(R.string.lbl_editor);
    editorTab.setTabListener(new TabListenerAdapter() {
      @Override public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        bus.post(new ShowTemplateEditor(true));
      }
    });

    ActionBar.Tab previewTab = actionBar.newTab();
    previewTab.setText(R.string.preview);
    previewTab.setTabListener(new TabListenerAdapter() {
      @Override public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        bus.post(new ShowTemplateEditor(false));
      }
    });

    // add tabs to the activity and select the current tab
    actionBar.addTab(editorTab);
    actionBar.addTab(previewTab);
    actionBar.getTabAt(mCurrentTab).select();

    long templateId = getIntent().getLongExtra(EditTemplateActivity.EXTRA_TEMPLATE_ID, -1);
    setTitle(templateId != -1 ? R.string.edit_cover_letter : R.string.new_cover_letter);
  }

  @Subscribe public void disableEditMode(DisableEditMode disableEditMode) {
    // let's create edit tabs!
    ActionBar actionBar = getActionBar();
    actionBar.removeAllTabs();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
  }
}