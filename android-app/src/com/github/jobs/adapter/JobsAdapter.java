package com.github.jobs.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codeslap.groundy.adapter.Layout;
import com.codeslap.groundy.adapter.ListBaseAdapter;
import com.codeslap.groundy.adapter.ResourceId;
import com.github.bean.Job;
import com.github.jobs.R;
import com.github.jobs.ui.activity.JobDetailsActivity;
import com.github.jobs.utils.RelativeDate;
import com.github.jobs.utils.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author cristian
 */
public class JobsAdapter extends ListBaseAdapter<Job, JobsAdapter.ViewHolder> {
    public static final DateTimeFormatter DATE_PARSER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy").withLocale(Locale.ENGLISH);
    private static final String TAG = "github:jobs:jobsAdapter";

    public JobsAdapter(Context context) {
        super(context, ViewHolder.class);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId().hashCode();
    }

    @Override
    public void populateHolder(int position, View view, ViewGroup parent, Job job, ViewHolder holder) {
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
            String timeAgo = RelativeDate.getTimeAgo(getContext(), parsed.getMillis());
            holder.date.setText(timeAgo);
        } catch (Exception e) {
            Log.wtf(TAG, "Could not parse date: " + job.getCreatedAt(), e);
        }
    }

    public ArrayList<String> getItemsIds() {
        ArrayList<String> ids = new ArrayList<String>();
        for (Job job : getItems()) {
            ids.add(job.getId());
        }
        return ids;
    }

    @Layout(R.layout.job_row)
    public static class ViewHolder {
        TextView title;
        TextView location;
        TextView company;
        TextView type;
        TextView date;
        @ResourceId(ignore = true)
        TextView tales;
    }
}
