package com.github.jobs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.codeslap.github.jobs.api.Job;
import com.github.jobs.R;
import com.github.jobs.ui.JobDetailsActivity;
import com.github.jobs.utils.RelativeDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author cristian
 */
public class JobsAdapter extends BaseAdapter {

    private final List<Job> mJobs = new ArrayList<Job>();
    private final LayoutInflater mInflater;
    private final SimpleDateFormat mDateParser;
    private final Context mContext;

    public JobsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDateParser = new SimpleDateFormat("EEE MMM dd kk:mm:ss 'UTC' yyyy");
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

        holder.title.setText(job.getTitle());
        holder.location.setText(job.getLocation());
        if (JobDetailsActivity.FULL_TIME.equals(job.getType())) {
            holder.company.setText(String.format("%s - ", job.getCompany()));
        } else {
            holder.company.setText(String.format("%s - "));
        }
        holder.type.setText(job.getType());
        try {
            Date parsed = mDateParser.parse(job.getCreatedAt());
            holder.date.setText(getTimeAgo(parsed));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    private String getTimeAgo(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return RelativeDate.getRelativeDate(mContext, calendar);
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

    private static class ViewHolder {
        TextView title;
        TextView location;
        TextView company;
        TextView type;
        TextView date;
    }
}
