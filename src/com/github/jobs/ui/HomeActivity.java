package com.github.jobs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.view.Window;
import com.codeslap.github.jobs.api.Job;
import com.codeslap.groundy.DetachableResultReceiver;
import com.codeslap.groundy.Groundy;
import com.codeslap.persistence.Persistence;
import com.codeslap.topy.BaseActivity;
import com.github.jobs.R;
import com.github.jobs.resolver.SearchJobsResolver;
import com.github.jobs.utils.UIUtils;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private SearchStatusFragment mSearchStatusFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setSupportProgressBarIndeterminateVisibility(false);
        findViewById(R.id.dump).setOnClickListener(this);
        findViewById(R.id.dummy).setOnClickListener(this);

        FragmentManager fm = getSupportFragmentManager();
        mSearchStatusFragment = (SearchStatusFragment) fm.findFragmentByTag(SearchStatusFragment.TAG);
        if (mSearchStatusFragment == null) {
            mSearchStatusFragment = new SearchStatusFragment();
            fm.beginTransaction().add(mSearchStatusFragment, SearchStatusFragment.TAG).commit();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dump:
                Groundy.queue(HomeActivity.this, SearchJobsResolver.class, mSearchStatusFragment.mReceiver, null);
                break;
            case R.id.dummy:
                startActivity(new Intent(this, UIUtils.getDummyClass(this)));
                break;
        }
    }

    public static class SearchStatusFragment extends Fragment implements DetachableResultReceiver.Receiver {
        public static final String TAG = SearchStatusFragment.class.getName();

        private boolean mSyncing = false;
        private DetachableResultReceiver mReceiver;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            mReceiver = new DetachableResultReceiver(new Handler());
            mReceiver.setReceiver(this);
        }

        /**
         * {@inheritDoc}
         */
        public void onReceiveResult(int resultCode, Bundle resultData) {
            HomeActivity activity = (HomeActivity) getActivity();
            if (activity == null) {
                return;
            }

            switch (resultCode) {
                case Groundy.STATUS_RUNNING: {
                    mSyncing = true;
                    break;
                }
                case Groundy.STATUS_FINISHED: {
                    mSyncing = false;
                    System.out.println(Persistence.quick(activity).findFirst(Job.class, null, null).getCompanyUrl());
                    break;
                }
                case Groundy.STATUS_ERROR: {
                    mSyncing = false;
                    Toast.makeText(activity, "Pailas", Toast.LENGTH_LONG).show();
                    break;
                }
            }

            ((HomeActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(mSyncing);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ((HomeActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(mSyncing);
        }
    }
}
