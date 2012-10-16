package com.github.jobs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.github.jobs.ui.fragment.JobDetailsFragment;

import java.util.List;

/**
 * @author cristian
 */
public class JobsDetailsAdapter extends MyFragmentStatePagerAdapter {
    private final List<String> mIds;

    public JobsDetailsAdapter(FragmentManager fm, List<String> ids) {
        super(fm);
        mIds = ids;
    }

    @Override
    protected Fragment getItem(int position) {
        return JobDetailsFragment.newInstance(mIds.get(position));
    }

    @Override
    public int getCount() {
        return mIds.size();
    }
}
