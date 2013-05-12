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

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.jobs.GithubJobsApplication;
import com.github.jobs.R;
import com.github.jobs.adapter.SearchJobFragmentAdapter;
import com.github.jobs.bean.SearchPack;
import com.github.jobs.events.ProgressWheel;
import com.github.jobs.events.RemoveSearch;
import com.github.jobs.events.SearchError;
import com.github.jobs.events.SearchFinished;
import com.github.jobs.events.SearchProgressChanged;
import com.github.jobs.resolver.SearchJobsTask;
import com.github.jobs.ui.dialog.AboutDialog;
import com.github.jobs.ui.dialog.SearchDialog;
import com.github.jobs.utils.ViewUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnStart;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;
import com.viewpagerindicator.TabPageIndicator;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import static com.github.jobs.utils.AnalyticsHelper.ACTION_OPEN;
import static com.github.jobs.utils.AnalyticsHelper.CATEGORY_ABOUT;
import static com.github.jobs.utils.AnalyticsHelper.CATEGORY_SEARCH;
import static com.github.jobs.utils.AnalyticsHelper.LABEL_DIALOG;
import static com.github.jobs.utils.AnalyticsHelper.NAME_HOME;
import static com.github.jobs.utils.AnalyticsHelper.NAME_TEMPLATES;
import static com.github.jobs.utils.AnalyticsHelper.getTracker;

public class HomeActivity extends TrackActivity {
  private static final int SEARCH_REQUEST = 534;

  private TabPageIndicator mIndicator;
  private SearchJobFragmentAdapter mSearchJobFragmentAdapter;
  private ViewPager mViewPager;
  private State mState;
  private CallbacksManager callbacksManager;
  private boolean mSyncing;
  @Inject ViewUtils viewUtils;
  @Inject Bus bus;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((GithubJobsApplication) getApplication()).inject(this);
    getTracker(this).trackPageView(NAME_HOME);
    setContentView(R.layout.main);

    mState = (State) getLastCustomNonConfigurationInstance();
    callbacksManager = CallbacksManager.init(savedInstanceState, this);
    if (mState == null) {
      mState = new State();
    } else if (mState.loading) {
      setSupportProgressBarIndeterminateVisibility(mState.loading);
    }

    mSearchJobFragmentAdapter = new SearchJobFragmentAdapter(this, getSupportFragmentManager());
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSearchJobFragmentAdapter);
    Drawable drawable = getResources().getDrawable(R.drawable.view_pager_separator);
    mViewPager.setPageMargin(drawable.getIntrinsicWidth());
    mViewPager.setPageMarginDrawable(drawable);

    mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
    mIndicator.setViewPager(mViewPager);

    if (mState.searchPacks != null) {
      showTabs();
      for (SearchPack searchPack : mState.searchPacks) {
        mSearchJobFragmentAdapter.addSearch(searchPack);
      }
      mIndicator.notifyDataSetChanged();
      selectTab(mState.currentTab);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    callbacksManager.onSaveInstanceState(outState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    callbacksManager.onDestroy();
  }

  @Override public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
    super.setSupportProgressBarIndeterminateVisibility(visible);
    if (mState != null) {
      mState.loading = visible;
    }
  }

  @Override public Object onRetainCustomNonConfigurationInstance() {
    mState.currentTab = mViewPager.getCurrentItem();
    return mState;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode != SEARCH_REQUEST || resultCode != Activity.RESULT_OK) {
      return;
    }
    SearchPack searchPack = new SearchPack();
    searchPack.setSearch(data.getStringExtra(SearchDialog.EXTRA_DESCRIPTION));
    searchPack.setLocation(data.getStringExtra(SearchDialog.EXTRA_LOCATION));
    searchPack.setFullTime(data.getBooleanExtra(SearchDialog.EXTRA_FULL_TIME, true));
    showTabs();
    if (!mSearchJobFragmentAdapter.containsSearch(searchPack)) {
      mSearchJobFragmentAdapter.addSearch(searchPack);
      if (mState.searchPacks == null) {
        mState.searchPacks = new ArrayList<SearchPack>();
      }
      mState.searchPacks.add(searchPack);
      mIndicator.notifyDataSetChanged();
    }
    int position = mSearchJobFragmentAdapter.positionFor(searchPack);
    selectTab(position);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_search:
        getTracker(this).trackEvent(CATEGORY_SEARCH, ACTION_OPEN, LABEL_DIALOG);
        startActivityForResult(new Intent(this, SearchDialog.class), SEARCH_REQUEST);
        break;
      case R.id.menu_about:
        getTracker(this).trackEvent(CATEGORY_ABOUT, ACTION_OPEN, LABEL_DIALOG);
        startActivity(new Intent(this, AboutDialog.class));
        break;
      case R.id.menu_templates:
        getTracker(this).trackPageView(NAME_TEMPLATES);
        startActivity(new Intent(this, TemplatesActivity.class));
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getSupportMenuInflater().inflate(R.menu.home_menu, menu);
    return true;
  }

  @Subscribe public void showProgressWheel(ProgressWheel progressWheel) {
    setSupportProgressBarIndeterminateVisibility(progressWheel.show);
  }

  private void showTabs() {
    if (mIndicator == null) {
      return;
    }
    mIndicator.setVisibility(View.VISIBLE);
  }

  private void hideTabs() {
    if (mIndicator == null) {
      return;
    }
    mIndicator.setVisibility(View.GONE);
  }

  private void selectTab(int i) {
    mIndicator.setCurrentItem(i);
    mViewPager.setCurrentItem(i);
  }

  @Subscribe public void removeSearch(RemoveSearch removeSearch) {
    int position = mSearchJobFragmentAdapter.positionFor(removeSearch.searchPack);
    mSearchJobFragmentAdapter.removeItem(removeSearch.searchPack);
    mIndicator.notifyDataSetChanged();
    // choose the next open tab or the latest one
    if (position < mSearchJobFragmentAdapter.getCount()) {
      selectTab(position);
    } else {
      selectTab(mSearchJobFragmentAdapter.getCount() - 1);
    }
    if (mSearchJobFragmentAdapter.getCount() <= 1) {
      hideTabs();
    }
  }

  @OnStart(SearchJobsTask.class) public void onSearchStart() {
    mSyncing = true;
    bus.post(new ProgressWheel(mSyncing));
  }

  @OnSuccess(SearchJobsTask.class)
  public void onSearchFinished(@Param(SearchJobsTask.DATA_SEARCH_PACK) SearchPack searchPack,
      @Param(SearchJobsTask.DATA_JOBS) ArrayList<Parcelable> parcelableArrayList) {
    mSyncing = false;
    if (searchPack != null) {
      bus.post(new SearchFinished(searchPack, parcelableArrayList));
      bus.post(new SearchProgressChanged(searchPack, mSyncing));
    }
    bus.post(new ProgressWheel(mSyncing));
  }

  @OnFailure(SearchJobsTask.class)
  public void onSearchFailed(@Param(Groundy.CRASH_MESSAGE) String error,
      @Param(SearchJobsTask.DATA_SEARCH_PACK) SearchPack searchPack) {
    mSyncing = false;
    viewUtils.toast(error);
    if (searchPack != null) {
      bus.post(new SearchError(searchPack));
      bus.post(new SearchProgressChanged(searchPack, mSyncing));
    }
  }

  private class State {
    boolean loading;
    List<SearchPack> searchPacks;
    int currentTab;
  }
}
