package com.github.jobs.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.github.jobs.R;

import static com.github.jobs.utils.AnalyticsHelper.*;

public class WebsiteHelper {

    public static void launchWebsite(Activity activity, String url) {
        try {
            getTracker(activity).trackEvent(CATEGORY_ABOUT, ACTION_OPEN, url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, R.string.could_not_launch_url, Toast.LENGTH_LONG).show();
        }
    }
}
