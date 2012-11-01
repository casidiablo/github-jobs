package com.github.jobs.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.github.jobs.R;

import static com.github.jobs.utils.AnalyticsHelper.*;

public class WebsiteHelper {

    public static boolean launchWebsite(Context context, String url) {
        try {
            getTracker(context).trackEvent(CATEGORY_ABOUT, ACTION_OPEN, url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, R.string.could_not_launch_url, Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
