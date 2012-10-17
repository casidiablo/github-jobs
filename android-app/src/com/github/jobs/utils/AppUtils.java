package com.github.jobs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.ui.activity.HomeActivity;
import com.github.jobs.ui.activity.TemplatesActivity;

import static com.github.jobs.utils.AnalyticsHelper.*;

/**
 * @author cristian
 * @version 1.0
 */
public class AppUtils {

    public static final boolean IN_DEVELOPMENT = true;

    public static void goHome(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
        getTracker(activity).trackEvent(CATEGORY_JOBS, ACTION_BACK, LABEL_FROM_DETAILS);
    }

    public static void goTemplatesList(Activity activity) {
        Intent intent = new Intent(activity, TemplatesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
        getTracker(activity).trackEvent(CATEGORY_JOBS, ACTION_BACK, LABEL_FROM_DETAILS);
    }

    public static Template getDefaultTemplate(Context context) {
        Template defaultTemplate = new Template();
        defaultTemplate.setName(context.getString(R.string.default_template_name));
        defaultTemplate.setContent(context.getString(R.string.default_template_content));
        defaultTemplate.setLastUpdate(System.currentTimeMillis());
        return defaultTemplate;
    }
}
