/*
 * Copyright 2014 Some Dev
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

package com.github.jobs.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.jobs.R;
import com.github.jobs.bean.Job;
import com.github.jobs.utils.RelativeDate;
import com.github.jobs.utils.StringUtils;
import java.util.ArrayList;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/** @author cristian */
public class JobsAdapter extends ListBaseAdapter<Job, JobsAdapter.ViewHolder> {
  public static final DateTimeFormatter DATE_PARSER =
      DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy").withLocale(Locale.ENGLISH);
  private static final String TAG = "github:jobs:jobsAdapter";

  public JobsAdapter(Context context) {
    super(context, ViewHolder.class);
  }

  @Override
  public void populateHolder(int position, View view, ViewGroup parent, Job job,
      ViewHolder holder) {
    holder.title.setText(StringUtils.trim(job.getTitle()));
    holder.location.setText(StringUtils.trim(job.getLocation()));
    holder.company.setText(job.getCompany());
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
    TextView date;
  }
}
