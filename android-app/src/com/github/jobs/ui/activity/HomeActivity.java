package com.github.jobs.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.jobs.R;
import com.github.jobs.adapter.SearchJobFragmentAdapter;
import com.github.jobs.bean.SearchPack;
import com.github.jobs.ui.dialog.SearchDialog;
import com.github.jobs.ui.fragment.JobListFragment;
import com.github.jobs.ui.fragment.SearchReceiverFragment;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

import static com.github.jobs.utils.AnalyticsHelper.*;

public class HomeActivity extends TrackActivity {
    private static final int SEARCH_REQUEST = 534;

    private TabPageIndicator mIndicator;
    private SearchJobFragmentAdapter mSearchJobFragmentAdapter;
    private ViewPager mViewPager;
    private State mState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTracker().trackPageView(NAME_HOME);
        setContentView(R.layout.main);

        mState = (State) getLastCustomNonConfigurationInstance();
        if (mState == null) {
            mState = new State();
            mState.receiver = new SearchReceiverFragment(this);
        } else {
            mState.receiver.setActivity(this);
            if (mState.loading) {
                setSupportProgressBarIndeterminateVisibility(mState.loading);
            }
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mSearchJobFragmentAdapter = new SearchJobFragmentAdapter(this, getSupportFragmentManager());
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

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        super.setSupportProgressBarIndeterminateVisibility(visible);
        if (mState != null) {
            mState.loading = visible;
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        mState.currentTab = mViewPager.getCurrentItem();
        return mState;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != SEARCH_REQUEST || resultCode != Activity.RESULT_OK) {
            return;
        }
        SearchPack searchPack = new SearchPack();
        searchPack.setSearch(data.getStringExtra(SearchDialog.EXTRA_DESCRIPTION));
        searchPack.setLocation(data.getStringExtra(SearchDialog.EXTRA_LOCATION));
        searchPack.setFullTime(data.getBooleanExtra(SearchDialog.EXTRA_FULL_TIME, true));
        showTabs();
        mSearchJobFragmentAdapter.addSearch(searchPack);
        if (mState.searchPacks == null) {
            mState.searchPacks = new ArrayList<SearchPack>();
        }
        mState.searchPacks.add(searchPack);
        mIndicator.notifyDataSetChanged();
        int position = mSearchJobFragmentAdapter.positionFor(searchPack);
        selectTab(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                getTracker().trackEvent(CATEGORY_SEARCH, ACTION_OPEN, LABEL_DIALOG);
                startActivityForResult(new Intent(this, SearchDialog.class), SEARCH_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    private void showTabs() {
        if (mIndicator == null) {
            return;
        }
        mIndicator.setVisibility(View.VISIBLE);
    }

    void hideTabs() {
        if (mIndicator == null) {
            return;
        }
        mIndicator.setVisibility(View.GONE);
    }

    void selectTab(int i) {
        mIndicator.setCurrentItem(i);
        mViewPager.setCurrentItem(i);
    }

    public void removeSearch(SearchPack searchPack) {
        int position = mSearchJobFragmentAdapter.positionFor(searchPack);
        mSearchJobFragmentAdapter.removeItem(searchPack);
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

    public SearchReceiverFragment getSearchReceiver() {
        return mState.receiver;
    }

    public void onFinished(Bundle resultData, SearchPack searchPack) {
        JobListFragment fragment = getJobListFragment(searchPack);
        if (fragment == null) {
            return;
        }
        fragment.onFinished(resultData);
    }

    public void onError(SearchPack searchPack) {
        JobListFragment fragment = getJobListFragment(searchPack);
        if (fragment == null) {
            return;
        }
        fragment.onError();
    }

    public void onProgressChanged(boolean running, SearchPack searchPack) {
        JobListFragment fragment = getJobListFragment(searchPack);
        if (fragment == null) {
            return;
        }
        fragment.onProgressChanged(running);
    }

    private JobListFragment getJobListFragment(SearchPack searchPack) {
        int position = mSearchJobFragmentAdapter.positionFor(searchPack);
        return (JobListFragment) mSearchJobFragmentAdapter.getFragment(position);
    }

    private class State {
        SearchReceiverFragment receiver;
        boolean loading;
        List<SearchPack> searchPacks;
        int currentTab;
    }
}
