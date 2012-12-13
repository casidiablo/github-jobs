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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.codeslap.persistence.Persistence;
import com.github.bean.Job;
import com.github.jobs.R;
import com.github.jobs.ui.activity.JobDetailsActivity;
import com.github.jobs.utils.ShareHelper;
import com.github.jobs.utils.StringUtils;
import com.telly.wasp.BitmapHelper;
import com.telly.wasp.CallbackBitmapObserver;

import static com.github.jobs.utils.AnalyticsHelper.*;

/**
 * @author cristian
 */
public class JobDetailsFragment extends SherlockFragment implements View.OnClickListener {

    private static final int SHARE = 484;
    private static final String KEY_JOB_ID = "com.github.jobs.KEY_JOB_ID";

    private Job mJob;
    private ImageView mBackground;

    public static JobDetailsFragment newInstance(String id) {
        JobDetailsFragment jobDetailsFragment = new JobDetailsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_JOB_ID, id);
        jobDetailsFragment.setArguments(args);
        return jobDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.job_details, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        String jobId = arguments.getString(KEY_JOB_ID);
        mJob = new Job();
        mJob.setId(jobId);
        mJob = Persistence.getAdapter(getActivity()).findFirst(mJob);
        if (mJob == null) {
            mJob = new Job();
            Toast.makeText(getActivity(), R.string.error_getting_job_info, Toast.LENGTH_LONG).show();
        }
        setHasOptionsMenu(true);

        TextView title = (TextView) getView().findViewById(R.id.title);
        title.setText(StringUtils.trim(mJob.getTitle()));

        TextView description = (TextView) getView().findViewById(R.id.description);
        description.setText(Html.fromHtml(mJob.getDescription()));
        description.setMovementMethod(LinkMovementMethod.getInstance());

        TextView company = (TextView) getView().findViewById(R.id.company);
        company.setText(mJob.getCompany());

        TextView companyUrl = (TextView) getView().findViewById(R.id.company_url);
        if (mJob.getCompanyUrl() == null) {
            companyUrl.setVisibility(View.GONE);
        } else {
            SpannableString content = new SpannableString(mJob.getCompanyUrl());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            companyUrl.setText(content);
            companyUrl.setOnClickListener(this);
            companyUrl.setVisibility(View.VISIBLE);
        }

        TextView companyLocation = (TextView) getView().findViewById(R.id.company_location);
        if (mJob.getLocation() == null) {
            companyLocation.setVisibility(View.GONE);
        } else {
            companyLocation.setText(mJob.getLocation());
            companyLocation.setVisibility(View.VISIBLE);
        }

        if (!JobDetailsActivity.FULL_TIME.equalsIgnoreCase(mJob.getType())) {
            getView().findViewById(R.id.full_time).setVisibility(View.INVISIBLE);
        }

        mBackground = (ImageView) getView().findViewById(R.id.job_details_background);
        setLogoBackground();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Context themedContext = getSherlockActivity().getSupportActionBar().getThemedContext();
        ShareActionProvider shareActionProvider = new ShareActionProvider(themedContext);
        shareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
            @Override
            public boolean onShareTargetSelected(ShareActionProvider shareActionProvider, Intent intent) {
                getTracker(getActivity()).trackEvent(CATEGORY_JOBS, ACTION_SHARE, intent.getComponent().getPackageName());
                return false;
            }
        });
        menu.add(0, SHARE, 0, R.string.share)
                .setActionProvider(shareActionProvider)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        // Set file with share history to the provider and set the share intent.
        MenuItem actionItem = menu.findItem(SHARE);
        ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        actionProvider.setShareIntent(ShareHelper.getShareIntent(mJob));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.company_url:
                Intent companyUrl = new Intent(Intent.ACTION_VIEW);
                companyUrl.addCategory(Intent.CATEGORY_BROWSABLE);
                companyUrl.setData(Uri.parse(mJob.getCompanyUrl()));
                startActivity(companyUrl);
                break;
        }
    }

    private void setLogoBackground() {
        if (mJob.getCompanyLogo() == null) {
            return;
        }
        CallbackBitmapObserver rawBitmapObserver = new CallbackBitmapObserver(new CallbackBitmapObserver.BitmapCallback() {
            @Override
            public boolean stillNeedsUrl(String uri) {
                return true;
            }

            @Override
            public void receiveBitmap(String uri, Bitmap bitmap) {
                if (bitmap == null) {
                    return;
                }
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                bitmapDrawable.setAlpha(50);
                bitmapDrawable.setGravity(Gravity.CENTER);
                mBackground.setImageDrawable(bitmapDrawable);
            }
        }, mJob.getCompanyLogo(), new Handler());
        BitmapHelper.getInstance().registerBitmapObserver(getActivity(), rawBitmapObserver);
    }
}
