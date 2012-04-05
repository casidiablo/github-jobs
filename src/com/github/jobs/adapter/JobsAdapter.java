package com.github.jobs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.codeslap.github.jobs.api.Job;
import com.github.jobs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cristian
 */
public class JobsAdapter extends BaseAdapter {

    private final List<Job> mJobs = new ArrayList<Job>();
    private final LayoutInflater mInflater;

    public JobsAdapter(Context context) {
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

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Job job = getItem(position);

        holder.title.setText(job.getTitle());
        holder.location.setText(job.getLocation());
        holder.company.setText(job.getCompany());
        holder.type.setText(job.getType());

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

    private static class ViewHolder{
        TextView title;
        TextView location;
        TextView company;
        TextView type;
    }
}
