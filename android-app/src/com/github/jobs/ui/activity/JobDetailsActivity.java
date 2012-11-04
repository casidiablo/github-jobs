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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.persistence.Persistence;
import com.github.bean.Job;
import com.github.jobs.R;
import com.github.jobs.adapter.JobsDetailsAdapter;
import com.github.jobs.ui.dialog.HowToApplyDialog;

import java.util.List;

import static com.github.jobs.utils.AnalyticsHelper.*;

/**
 * @author cristian
 */
public class JobDetailsActivity extends TrackActivity implements ViewPager.OnPageChangeListener {

    public static final String EXTRA_CURRENT_JOB_ID = "com.github.jobs.CURRENT_JOB_ID";
    public static final String EXTRA_JOBS_IDS = "com.github.jobs.JOBS_IDS";

    private static final int HOW_TO_APPLY = 4734;
    public static final String FULL_TIME = "Full Time";

    private ViewPager mJobsPager;
    private List<String> mJobsIds;
    private int mCurrentJobPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_details_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() == null || getIntent().getExtras() == null) {
            finish();
            return;
        }
        // get the selected job and the list of ids
        Bundle extras = getIntent().getExtras();
        String jobId = extras.getString(EXTRA_CURRENT_JOB_ID);
        mJobsIds = extras.getStringArrayList(EXTRA_JOBS_IDS);
        mCurrentJobPosition = mJobsIds.indexOf(jobId);

        // prepare the view pager to show current job
        mJobsPager = (ViewPager) findViewById(R.id.jobs_pager);
        mJobsPager.setAdapter(new JobsDetailsAdapter(getSupportFragmentManager(), mJobsIds));
        mJobsPager.setCurrentItem(mCurrentJobPosition);
        mJobsPager.setOnPageChangeListener(this);
        Drawable drawable = getResources().getDrawable(R.drawable.view_pager_separator);
        mJobsPager.setPageMargin(drawable.getIntrinsicWidth());
        mJobsPager.setPageMarginDrawable(drawable);

        getTracker(this).trackPageView(NAME_DETAILS + "?id=" + jobId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, HOW_TO_APPLY, 0, getString(R.string.apply))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case HOW_TO_APPLY:
                Job job = findCurrentJob();
                // start a new activity with the application details
                Intent howToApplyDialog = new Intent(this, HowToApplyDialog.class);
                howToApplyDialog.putExtra(HowToApplyDialog.EXTRA_TITLE, job.getTitle());
                howToApplyDialog.putExtra(HowToApplyDialog.EXTRA_HOW_TO_APPLY, job.getHowToApply());
                startActivity(howToApplyDialog);
                getTracker(this).trackEvent(CATEGORY_JOBS, ACTION_APPLY, job.getTitle() + "," + job.getUrl());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Job findCurrentJob() {
        // find current job id
        int currentItem = mJobsPager.getCurrentItem();
        String jobId = mJobsIds.get(currentItem);

        // get job object from database
        Job job = new Job();
        job.setId(jobId);
        job = Persistence.getAdapter(this).findFirst(job);
        return job;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position > mCurrentJobPosition) {
            getTracker(this).trackEvent(CATEGORY_JOBS, ACTION_SWIPE, LABEL_LEFT);
        } else if (position < mCurrentJobPosition) {
            getTracker(this).trackEvent(CATEGORY_JOBS, ACTION_SWIPE, LABEL_RIGHT);
        }
        mCurrentJobPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}