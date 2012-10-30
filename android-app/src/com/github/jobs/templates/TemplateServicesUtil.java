package com.github.jobs.templates;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.github.jobs.R;
import com.github.jobs.bean.Service;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.ui.activity.SOUserPickerActivity;

import java.lang.reflect.Constructor;
import java.util.*;

import static com.github.jobs.R.*;
import static com.github.jobs.bean.Service.from;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplateServicesUtil {
    private static final String TAG = "github:jobs:templates";

    public static final String STACK_OVERFLOW = "stack_overflow";
    public static final String WEBSITE = "website";

    private static final Map<String, Class<? extends ServiceGenerator>> MAP = new HashMap<String, Class<? extends ServiceGenerator>>();
    static {
        MAP.put(STACK_OVERFLOW, StackOverflowGenerator.class);
        MAP.put(WEBSITE, WebsiteGenerator.class);
    }

    private static List<Service> sServices;

    public static List<Service> getServices(Context context) {
        if (sServices == null) {
            Service github = from(context, id.service_github, string.lbl_github, drawable.logo_github);
            Service so = from(context, id.service_so, string.lbl_stack_overflow, drawable.logo_so);
            Service twitter = from(context, id.service_twitter, string.lbl_twitter, drawable.logo_twitter);
            Service skype = from(context, id.service_skype, string.lbl_skype, drawable.logo_skype);
            Service aboutMe = from(context, id.service_about_me, string.lbl_about_me, drawable.logo_about_me);
            sServices = Arrays.asList(github, so, twitter, skype, aboutMe);
        }
        return sServices;
    }

    public static void resolve(Activity activity, int id) {
        switch (id) {
            case R.id.service_so:
                Intent intent = new Intent(activity, SOUserPickerActivity.class);
                activity.startActivityForResult(intent, SOUserPickerActivity.REQUEST_CODE);
                break;
        }
    }

    /**
     * @param templateServices the list of services to check
     * @return true if any of the templates services is a website
     */
    public static boolean containsWebsite(ArrayList<TemplateService> templateServices) {
        for (TemplateService templateService : templateServices) {
            if (WEBSITE.equals(templateService.getType())) {
                return true;
            }
        }
        return false;
    }

    public static String getContent(Context context, TemplateService templateService) {
        if (templateService == null) {
            return null;
        }
        String serviceType = templateService.getType();
        if (MAP.containsKey(serviceType)) {
            Class<? extends ServiceGenerator> theClass = MAP.get(serviceType);
            try {
                Constructor<? extends ServiceGenerator> constructor = theClass.getConstructor(Context.class);
                ServiceGenerator serviceGenerator = constructor.newInstance(context);
                return serviceGenerator.generate(templateService);
            } catch (Exception e) {
                Log.wtf(TAG, "Failed to generate template for: " + templateService, e);
            }
        }
        return context.getString(string.basic_cover_letter_footer, templateService.getType(), templateService.getData());
    }
}
