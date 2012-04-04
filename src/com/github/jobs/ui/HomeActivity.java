package com.github.jobs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.groundy.Groundy;
import com.codeslap.groundy.ReceiverFragment;
import com.codeslap.persistence.Persistence;
import com.codeslap.topy.BaseActivity;
import com.github.jobs.R;
import com.github.jobs.resolver.SearchJobsResolver;

import java.util.List;

public class HomeActivity extends BaseActivity implements TextView.OnEditorActionListener, LoaderManager.LoaderCallbacks<List<Job>> {

    private static final int SEARCH_REQUEST = 534;
    private static final int SEARCH_ITEM = 848;

    private ReceiverFragment mReceiverFragment;

    private String mCurrentFilter;
    private String mCurrentLocation;
    private boolean mCurrentFullTime;

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
        menu.add(0, SEARCH_ITEM, 0, R.string.search).setIcon(R.drawable.ic_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SEARCH_ITEM:
                startActivityForResult(new Intent(this, SearchDialog.class), SEARCH_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mCurrentFilter = v.getText().toString().trim();
            triggerJobSearch();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_REQUEST && resultCode == RESULT_OK) {
            mCurrentFilter = data.getStringExtra(SearchDialog.EXTRA_DESCRIPTION);
            mCurrentLocation = data.getStringExtra(SearchDialog.EXTRA_LOCATION);
            mCurrentFullTime = data.getBooleanExtra(SearchDialog.EXTRA_FULL_TIME, false);
        }
    }

    @Override
    public Loader<List<Job>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Job>> listLoader, List<Job> data) {
    }

    @Override
    public void onLoaderReset(Loader<List<Job>> listLoader) {
    }

    private void triggerJobSearch() {
        Bundle extras = new Bundle();
        extras.putString(SearchJobsResolver.EXTRA_QUERY, mCurrentFilter);
        extras.putString(SearchJobsResolver.EXTRA_LOCATION, mCurrentLocation);
        extras.putBoolean(SearchJobsResolver.EXTRA_FULL_TIME, mCurrentFullTime);
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
