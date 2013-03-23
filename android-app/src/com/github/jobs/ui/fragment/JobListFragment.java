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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.jobs.R;
import com.github.jobs.adapter.JobsAdapter;
import com.github.jobs.bean.Job;
import com.github.jobs.bean.SearchPack;
import com.github.jobs.events.*;
import com.github.jobs.loader.JobListLoader;
import com.github.jobs.receivers.SearchReceiver;
import com.github.jobs.resolver.EmailSubscriberTask;
import com.github.jobs.resolver.SearchJobsTask;
import com.github.jobs.ui.activity.HomeActivity;
import com.github.jobs.ui.activity.JobDetailsActivity;
import com.github.jobs.ui.dialog.HowToApplyDialog;
import com.github.jobs.ui.dialog.SubscribeDialog;
import com.github.jobs.utils.ShareHelper;
import com.squareup.otto.Subscribe;
import com.telly.groundy.Groundy;

import java.util.ArrayList;
import java.util.List;

import static com.github.jobs.utils.AnalyticsHelper.*;

/**
 * @author cristian
 */
public class JobListFragment extends BusFragment implements LoaderManager.LoaderCallbacks<List<Job>>,
  AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

  private static final String KEY_SEARCH = "search_key";
  private static final String KEY_LOADING = "loading_key";
  private static final String KEY_LAST_TOTAL_ITEM_COUNT = "last_total_item_count_key";

  public static JobListFragment newInstance(SearchPack searchPack) {
    JobListFragment jobListFragment = new JobListFragment();
    Bundle args = new Bundle();
    args.putParcelable(KEY_SEARCH, searchPack);
    jobListFragment.setArguments(args);
    return jobListFragment;
  }

  private static final int JOB_DETAILS = 8474;
  private static final int HOW_TO_APPLY = 5763;
  private static final int SHARE = 4722;

  private SearchPack mCurrentSearch = new SearchPack();
  private JobsAdapter mAdapter;

  private View mMoreRootView;
  private ListView mList;
  private boolean mLoading = false;
  private int mLastTotalItemCount;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      mCurrentSearch = (SearchPack) savedInstanceState.getParcelable(KEY_SEARCH);
      mLoading = savedInstanceState.getBoolean(KEY_LOADING);
      mLastTotalItemCount = savedInstanceState.getInt(KEY_LAST_TOTAL_ITEM_COUNT);
    } else {
      mCurrentSearch = (SearchPack) getArguments().getParcelable(KEY_SEARCH);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.jobs_list, null, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mAdapter = new JobsAdapter(getActivity());
    mList = (ListView) getView().findViewById(R.id.job_list);
    mList.setOnItemClickListener(this);
    mMoreRootView = getLayoutInflater(savedInstanceState).inflate(R.layout.list_footer, null);
    mList.addFooterView(mMoreRootView);
    mList.setAdapter(mAdapter);
    mList.setOnScrollListener(this);
    setHasOptionsMenu(true);
    registerForContextMenu(mList);

    queryList();
    removeFooterFromList();
    if (savedInstanceState == null) {
      triggerJobSearch();
    }
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    FragmentActivity activity = getActivity();
    if (activity == null || !isAdded()) {
      return;
    }
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
    Job job = mAdapter.getItem(info.position);
    menu.setHeaderTitle(job.getTitle());
    menu.add(0, JOB_DETAILS, 0, R.string.job_details);
    menu.add(0, HOW_TO_APPLY, 0, R.string.how_to_apply);
    menu.add(0, SHARE, 0, R.string.share);
    getTracker(activity).trackEvent(CATEGORY_JOBS, ACTION_OPEN_CONTEXT, mCurrentSearch.getSearch());
  }

  @Override
  public boolean onContextItemSelected(android.view.MenuItem item) {
    FragmentActivity activity = getActivity();
    if (activity == null || !isAdded()) {
      return true;
    }
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    Job job = mAdapter.getItem(info.position);
    switch (item.getItemId()) {
      case JOB_DETAILS:
        ArrayList<String> ids = mAdapter.getItemsIds();
        Intent jobDetailsIntent = new Intent(activity, JobDetailsActivity.class);
        jobDetailsIntent.putExtra(JobDetailsActivity.EXTRA_CURRENT_JOB_ID, job.getId());
        jobDetailsIntent.putExtra(JobDetailsActivity.EXTRA_JOBS_IDS, ids);
        startActivity(jobDetailsIntent);
        getTracker(activity).trackEvent(CATEGORY_JOBS, ACTION_FOLLOW_CONTEXT, LABEL_DETAILS);
        return true;
      case HOW_TO_APPLY:
        Intent howToApplyIntent = new Intent(activity, HowToApplyDialog.class);
        howToApplyIntent.putExtra(HowToApplyDialog.EXTRA_TITLE, job.getTitle());
        howToApplyIntent.putExtra(HowToApplyDialog.EXTRA_HOW_TO_APPLY, job.getHowToApply());
        startActivity(howToApplyIntent);
        getTracker(activity).trackEvent(CATEGORY_JOBS, ACTION_FOLLOW_CONTEXT, LABEL_APPLY);
        return true;
      case SHARE:
        startActivity(ShareHelper.getShareIntent(job));
        getTracker(activity).trackEvent(CATEGORY_JOBS, ACTION_FOLLOW_CONTEXT, LABEL_SHARE);
        return true;
    }
    return super.onContextItemSelected(item);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    FragmentActivity activity = getActivity();
    if (activity == null || !isAdded()) {
      return true;
    }
    switch (item.getItemId()) {
      case R.id.menu_subscribe:
        Intent subscribeIntent = new Intent(activity, SubscribeDialog.class);
        subscribeIntent.putExtra(EmailSubscriberTask.EXTRA_SEARCH, mCurrentSearch);
        startActivity(subscribeIntent);
        getTracker(activity).trackEvent(CATEGORY_SUBSCRIBE, ACTION_OPEN, LABEL_DIALOG);
        break;
      case R.id.menu_delete:
        bus.post(new RemoveSearch(mCurrentSearch));
        getTracker(activity).trackEvent(CATEGORY_SEARCH, ACTION_REMOVE, mCurrentSearch.getSearch());
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public Loader<List<Job>> onCreateLoader(int id, Bundle args) {
    FragmentActivity activity = getActivity();
    if (activity == null || !isAdded()) {
      return null;
    }
    return new JobListLoader(activity, mCurrentSearch);
  }

  @Override
  public void onLoadFinished(Loader<List<Job>> listLoader, List<Job> data) {
    mAdapter.updateItems(data);
    if (data.isEmpty()) {
      removeFooterFromList();
    }
  }

  @Override
  public void onLoaderReset(Loader<List<Job>> listLoader) {
    mAdapter.clear();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (mAdapter.getCount() <= position) {
      // ignore invalid clicks
      return;
    }
    Job job = mAdapter.getItem(position);
    ArrayList<String> ids = mAdapter.getItemsIds();
    Intent intent = new Intent(getActivity(), JobDetailsActivity.class);
    intent.putExtra(JobDetailsActivity.EXTRA_CURRENT_JOB_ID, job.getId());
    intent.putExtra(JobDetailsActivity.EXTRA_JOBS_IDS, ids);
    startActivity(intent);
    getTracker(getActivity()).trackEvent(CATEGORY_JOBS, ACTION_OPEN, job.getTitle() + "," + job.getUrl());
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
  }

  @Override
  public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    if (mMoreRootView == null) {
      return;
    }
    if (totalItemCount <= 1) {
      return;
    }
    totalItemCount -= mList.getHeaderViewsCount();
    if (!mLoading && mLastTotalItemCount != totalItemCount && (totalItemCount - visibleItemCount) == firstVisibleItem) {
      mLoading = true;
      mLastTotalItemCount = totalItemCount;
      loadMore();
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    if (menu.findItem(R.id.menu_subscribe) == null && !mCurrentSearch.isDefault()) {
      inflater.inflate(R.menu.jobs_list_menu, menu);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(KEY_SEARCH, mCurrentSearch);
    outState.putBoolean(KEY_LOADING, mLoading);
    outState.putInt(KEY_LAST_TOTAL_ITEM_COUNT, mLastTotalItemCount);
  }

  private void queryList() {
    try {
      FragmentActivity activity = getActivity();
      if (activity == null || !isAdded()) {
        return;
      }
      LoaderManager loaderManager = activity.getSupportLoaderManager();
      Loader<Object> loader = loaderManager.getLoader(mCurrentSearch.hashCode());
      if (loader == null) {
        loaderManager.initLoader(mCurrentSearch.hashCode(), null, this);
      } else {
        loaderManager.restartLoader(mCurrentSearch.hashCode(), null, this);
      }
    } catch (IllegalStateException e) {
      // happens when activity is closed. We can't use isResumed since it will be false when the activity is
      // not being shown, thus it will cause problems if user loads another screen while this is still loading
    }
  }

  private void removeFooterFromList() {
    if (mMoreRootView == null) {
      return;
    }
    mList.removeFooterView(mMoreRootView);
    mMoreRootView = null;
  }

  private void addFooterToList() {
    if (mMoreRootView == null) {
      Context activity = getActivity();
      if (activity == null || !isAdded()) {
        return;
      }
      mMoreRootView = LayoutInflater.from(activity).inflate(R.layout.list_footer, null);
      mList.addFooterView(mMoreRootView);
    } else if (mList.getFooterViewsCount() == 0) {
      mList.addFooterView(mMoreRootView);
    }
  }

  private void loadMore() {
    mCurrentSearch.setPage(mCurrentSearch.getPage() + 1);
    triggerJobSearch();
  }

  private void triggerJobSearch() {
    Bundle extras = new Bundle();
    extras.putParcelable(SearchJobsTask.EXTRA_SEARCH_PACK, mCurrentSearch);

    mLoading = true;
    HomeActivity activity = (HomeActivity) getActivity();
    if (activity == null || !isAdded()) {
      return;
    }
    SearchReceiver receiver = activity.getSearchReceiver();
    Groundy.create(getActivity(), SearchJobsTask.class)
        .receiver(receiver.getReceiver())
        .params(extras)
        .execute();
    ((SherlockFragmentActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
  }

  @Subscribe public void onFinished(SearchFinished searchFinished) {
    if (!shouldProcess(searchFinished)) {
      return;
    }
    ArrayList<Parcelable> parcelableArrayList = searchFinished.resultData.getParcelableArrayList(SearchJobsTask.DATA_JOBS);
    if (parcelableArrayList == null) {
      return;
    }
    ArrayList<Job> jobs = new ArrayList<Job>();
    for (Parcelable parcelable : parcelableArrayList) {
      jobs.add((Job) parcelable);
    }
    mAdapter.addItems(jobs);
    if (jobs.size() == 0) {
      removeFooterFromList();
    } else {
      addFooterToList();
    }
    mLoading = false;
  }

  @Subscribe public void onError(SearchError searchError) {
    if (shouldProcess(searchError)) {
      removeFooterFromList();
      mLoading = false;
    }
  }

  @Subscribe public void onProgressChanged(SearchProgressChanged searchProgressChanged) {
    if (shouldProcess(searchProgressChanged)) {
      mLoading = searchProgressChanged.syncing;
    }
  }

  private boolean shouldProcess(SearchEvent searchEvent) {
    return searchEvent.searchPack != null && searchEvent.searchPack.equals(mCurrentSearch);
  }
}
