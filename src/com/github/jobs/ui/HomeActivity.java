package com.github.jobs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.groundy.Groundy;
import com.codeslap.groundy.ReceiverFragment;
import com.codeslap.topy.BaseActivity;
import com.github.jobs.R;
import com.github.jobs.adapter.JobsAdapter;
import com.github.jobs.resolver.EmailSubscriberResolver;
import com.github.jobs.resolver.SearchJobsResolver;

import java.util.List;

public class HomeActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<List<Job>>,AdapterView.OnItemClickListener {
    private static final int SEARCH_REQUEST = 534;
    private static final int JOB_DETAILS = 8474;
    private static final int HOW_TO_APPLY = 5763;

    private ReceiverFragment mReceiverFragment;

    private String mCurrentFilter;
    private String mCurrentLocation;
    private boolean mCurrentFullTime;

    private JobsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mAdapter = new JobsAdapter(this);
        ListView list = (ListView) findViewById(R.id.job_list);
        list.setOnItemClickListener(this);
        list.setAdapter(mAdapter);
        registerForContextMenu(list);

        FragmentManager fm = getSupportFragmentManager();
        mReceiverFragment = (ReceiverFragment) fm.findFragmentByTag(ReceiverFragment.TAG);
        if (mReceiverFragment == null) {
            mReceiverFragment = new SearchReceiverFragment();
            fm.beginTransaction().add(mReceiverFragment,
                    ReceiverFragment.TAG).commit();
            triggerJobSearch();
        }

        queryList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Job job = mAdapter.getItem(info.position);
        menu.setHeaderTitle(job.getTitle());
        menu.add(0, JOB_DETAILS, 0, R.string.job_details);
        menu.add(0, HOW_TO_APPLY, 0, R.string.how_to_apply);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Job job = mAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case JOB_DETAILS:
                Intent jobDetailsIntent = new Intent(this, JobDetailsActivity.class);
                jobDetailsIntent.putExtra(JobDetailsActivity.EXTRA_JOB_ID, job.getId());
                startActivity(jobDetailsIntent);
                return true;
            case HOW_TO_APPLY:
                Intent howToApplyIntent = new Intent(this, HowToApplyDialog.class);
                howToApplyIntent.putExtra(HowToApplyDialog.EXTRA_HOW_TO_APPLY, job.getHowToApply());
                startActivity(howToApplyIntent);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                startActivityForResult(new Intent(this, SearchDialog.class), SEARCH_REQUEST);
                break;
            case R.id.menu_subscribe:
                Intent subscribeIntent = new Intent(this, SubscribeDialog.class);
                subscribeIntent.putExtra(EmailSubscriberResolver.EXTRA_DESCRIPTION, mCurrentFilter);
                subscribeIntent.putExtra(EmailSubscriberResolver.EXTRA_LOCATION, mCurrentLocation);
                subscribeIntent.putExtra(EmailSubscriberResolver.EXTRA_FULL_TIME, mCurrentFullTime);
                startActivity(subscribeIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_REQUEST && resultCode == RESULT_OK) {
            mCurrentFilter = data.getStringExtra(SearchDialog.EXTRA_DESCRIPTION);
            mCurrentLocation = data.getStringExtra(SearchDialog.EXTRA_LOCATION);
            mCurrentFullTime = data.getBooleanExtra(SearchDialog.EXTRA_FULL_TIME, true);
            triggerJobSearch();
        }
    }

    @Override
    public Loader<List<Job>> onCreateLoader(int id, Bundle args) {
        return new JobListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Job>> listLoader, List<Job> data) {
        mAdapter.updateItems(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Job>> listLoader) {
        mAdapter.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Job job = mAdapter.getItem(position);
        Intent intent = new Intent(this, JobDetailsActivity.class);
        intent.putExtra(JobDetailsActivity.EXTRA_JOB_ID, job.getId());
        startActivity(intent);
    }

    private void queryList() {
        try {
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<Object> loader = loaderManager.getLoader(0);
            if (loader == null) {
                loaderManager.initLoader(0, null, this);
            } else {
                loaderManager.restartLoader(0, null, this);
            }
        } catch (IllegalStateException e) {
            // happens when activity is closed. We can't use isResumed since it will be false when the activity is
            // not being shown, thus it will cause problems if user loads another screen while this is still loading
        }
    }

    private void triggerJobSearch() {
        Bundle extras = new Bundle();
        extras.putString(SearchJobsResolver.EXTRA_QUERY, mCurrentFilter);
        extras.putString(SearchJobsResolver.EXTRA_LOCATION, mCurrentLocation);
        extras.putBoolean(SearchJobsResolver.EXTRA_FULL_TIME, mCurrentFullTime);
        Groundy.queue(this, SearchJobsResolver.class, mReceiverFragment.getReceiver(), extras);
        setSupportProgressBarIndeterminateVisibility(true);
    }

    private static class SearchReceiverFragment extends ReceiverFragment {
        @Override
        protected void onFinished(Bundle resultData) {
            super.onFinished(resultData);
            ((HomeActivity) getActivity()).queryList();
        }

        @Override
        protected void onProgressChanged(boolean running) {
            ((HomeActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(running);
        }
    }
}
