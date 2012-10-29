package com.github.jobs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.github.jobs.R;
import com.github.jobs.bean.Service;
import com.github.jobs.ui.activity.SOUserPickerActivity;

import java.util.Arrays;
import java.util.List;

import static com.github.jobs.R.*;
import static com.github.jobs.bean.Service.from;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplateServicesUtil {

    private static List<Service> sServices;

    public static List<Service> getServices(Context context) {
        if (sServices == null) {
            Service github = from(context, id.service_github, string.lbl_github, drawable.logo_github);
            Service so = from(context, id.service_so, string.lbl_stackoverflow, drawable.logo_so);
            Service twitter = from(context, id.service_twitter, string.lbl_twitter, drawable.logo_twitter);
            Service skype = from(context, id.service_skype, string.lbl_skype, drawable.logo_skype);
            Service aboutMe = from(context, id.service_about_me, string.lbl_about_me, drawable.logo_about_me);
            sServices = Arrays.asList(github, so, twitter, skype, aboutMe);
        }
        return sServices;
    }

    public static void resolve(Activity activity, Fragment fragment, int id) {
        switch (id) {
            case R.id.service_so:
                Intent intent = new Intent(activity, SOUserPickerActivity.class);
                if (fragment != null) {
                    fragment.startActivityForResult(intent, SOUserPickerActivity.REQUEST_CODE);
                } else {
                    activity.startActivityForResult(intent, SOUserPickerActivity.REQUEST_CODE);
                }
                break;
        }
    }
}
