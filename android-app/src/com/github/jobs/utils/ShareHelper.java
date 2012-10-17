package com.github.jobs.utils;

import android.content.Intent;
import com.github.bean.Job;

/**
 * @author cristian
 */
public class ShareHelper {
    private static final String JOB_URL = "https://jobs.github.com/positions/%s";

    public static Intent getShareIntent(Job job) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, job.getTitle());
        String format = "%s (%s, %s): %s";
        String message = String.format(format, job.getTitle(), job.getCompany(), job.getLocation(),
                String.format(JOB_URL, job.getId()));
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        return shareIntent;
    }
}
