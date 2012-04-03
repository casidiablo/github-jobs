package com.github.jobs.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.internal.widget.ActionBarView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.groundy.Groundy;
import com.codeslap.groundy.ReceiverFragment;
import com.codeslap.persistence.Persistence;
import com.codeslap.topy.BaseActivity;
import com.github.jobs.R;
import com.github.jobs.resolver.SearchJobsResolver;

public class HomeActivity extends BaseActivity implements TextView.OnEditorActionListener {

    private ReceiverFragment mReceiverFragment;
    private String mCurrentFilter;
    private TextView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentManager fm = getSupportFragmentManager();
        mReceiverFragment = (ReceiverFragment) fm.findFragmentByTag(ReceiverFragment.TAG);
        if (mReceiverFragment == null) {
            mReceiverFragment = new SearchReceiverFragment();
            fm.beginTransaction().add(mReceiverFragment, ReceiverFragment.TAG).commit();
            triggerJobSearch();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        View searchBar = getLayoutInflater().inflate(R.layout.search, new LinearLayout(this), false);
        mSearchView = (TextView) searchBar.findViewById(R.id.edit_search);
        mSearchView.setOnEditorActionListener(this);
        menu.add(R.string.search)
                .setIcon(R.drawable.ic_search)
                .setActionView(searchBar)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        return true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mCurrentFilter = v.getText().toString().trim();
            triggerJobSearch();
        }
        return false;
    }

    private void triggerJobSearch() {
        if (mSearchView != null) {
            ActionBarView actionBar = (ActionBarView) findViewById(com.actionbarsherlock.R.id.abs__action_bar);
            actionBar.collapseActionView();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        }

        Bundle extras = new Bundle();
        extras.putString(SearchJobsResolver.EXTRA_QUERY, mCurrentFilter);
        Groundy.queue(this, SearchJobsResolver.class, mReceiverFragment.getReceiver(), extras);
    }

    private static class SearchReceiverFragment extends ReceiverFragment {
        @Override
        protected void onFinished(Bundle resultData) {
            super.onFinished(resultData);
            System.out.println(":::::: FINISHED " + Persistence.quick(getActivity()).count(Job.class));
        }

        @Override
        protected void onProgressChanged(boolean running) {
            ((BaseActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(running);
        }
    }
}
