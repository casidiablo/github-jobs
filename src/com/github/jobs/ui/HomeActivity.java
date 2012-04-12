package com.github.jobs.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.groundy.ReceiverFragment;
import com.codeslap.topy.BaseActivity;
import com.github.jobs.R;
import com.viewpagerindicator.TabPageIndicator;

public class HomeActivity extends BaseActivity {
    private static final int SEARCH_REQUEST = 534;

    private ReceiverFragment mReceiverFragment;
    private TabPageIndicator mIndicator;
    private SearchJobFragmentAdapter mSearchJobFragmentAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentManager fm = getSupportFragmentManager();
        mReceiverFragment = (ReceiverFragment) fm.findFragmentByTag(ReceiverFragment.TAG);
        if (mReceiverFragment == null) {
            mReceiverFragment = new SearchReceiverFragment();
            fm.beginTransaction().add(mReceiverFragment, ReceiverFragment.TAG).commit();
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mSearchJobFragmentAdapter = new SearchJobFragmentAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mSearchJobFragmentAdapter);

        mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != SEARCH_REQUEST || resultCode != Activity.RESULT_OK) {
            return;
        }
        SearchPack searchPack = new SearchPack();
        searchPack.search = data.getStringExtra(SearchDialog.EXTRA_DESCRIPTION);
        searchPack.location = data.getStringExtra(SearchDialog.EXTRA_LOCATION);
        searchPack.fullTime = data.getBooleanExtra(SearchDialog.EXTRA_FULL_TIME, true);
        mSearchJobFragmentAdapter.addSearch(searchPack);
        mIndicator.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
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

    public void showTabs() {
        if (mIndicator == null) {
            return;
        }
        mIndicator.setVisibility(View.VISIBLE);
    }

    public void hideTabs() {
        if (mIndicator == null) {
            return;
        }
        mIndicator.setVisibility(View.GONE);
    }

    public void selectTab(int i) {
        mIndicator.setCurrentItem(i);
        mViewPager.setCurrentItem(i);
    }

    public void removeSearch(SearchPack searchPack) {
        mSearchJobFragmentAdapter.removeItem(searchPack);
        mIndicator.notifyDataSetChanged();
        selectTab(0);
        if (mSearchJobFragmentAdapter.getCount() <= 1) {
            hideTabs();
        }
    }
}
