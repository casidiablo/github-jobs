package com.github.jobs.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.github.bean.Job;
import com.github.jobs.R;
import com.github.jobs.ui.activity.JobDetailsActivity;
import com.github.jobs.utils.RelativeDate;
import com.github.jobs.utils.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cristian
 */
public class JobsAdapter extends BaseAdapter {

    private final List<Job> mJobs = new ArrayList<Job>();
    private final LayoutInflater mInflater;
    public static final DateTimeFormatter DATE_PARSER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy");

    private final Context mContext;
    private static final String TAG = "github:jobs:jobsAdapter";

    public JobsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mJobs.size();
    }

    @Override
    public Job getItem(int position) {
        return mJobs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mJobs.get(position).getId().hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.job_row, null);

            holder.title = (TextView) view.findViewById(R.id.title);
            holder.location = (TextView) view.findViewById(R.id.location);
            holder.company = (TextView) view.findViewById(R.id.company);
            holder.type = (TextView) view.findViewById(R.id.type);
            holder.date = (TextView) view.findViewById(R.id.date);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Job job = getItem(position);

        holder.title.setText(StringUtils.trim(job.getTitle()));
        holder.location.setText(StringUtils.trim(job.getLocation()));
        if (JobDetailsActivity.FULL_TIME.equals(job.getType())) {
            holder.company.setText(String.format("%s - ", job.getCompany()));
        } else {
            holder.company.setText(job.getCompany());
        }
        holder.type.setText(job.getType());
        try {
            DateTime parsed = DATE_PARSER.withZoneUTC().parseDateTime(job.getCreatedAt());
            String timeAgo = RelativeDate.getTimeAgo(mContext, parsed.getMillis());
            holder.date.setText(timeAgo);
        } catch (Exception e) {
            Log.wtf(TAG, "Could not parse date: " + job.getCreatedAt(), e);
        }

        return view;
    }

    public void updateItems(List<Job> data) {
        mJobs.clear();
        mJobs.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        mJobs.clear();
    }

    public void addItems(ArrayList<Job> jobs) {
        mJobs.addAll(jobs);
        notifyDataSetChanged();
    }

    public ArrayList<String> getItemsIds() {
        ArrayList<String> ids = new ArrayList<String>();
        for (Job job : mJobs) {
            ids.add(job.getId());
        }
        return ids;
    }

    private static class ViewHolder {
        TextView title;
        TextView location;
        TextView company;
        TextView type;
        TextView date;
    }
}
