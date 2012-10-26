package com.github.jobs.utils;

import android.content.Context;
import com.github.jobs.R;
import com.github.jobs.bean.Service;
import roboguice.inject.InjectView;

import java.util.Arrays;
import java.util.List;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplateServicesUtil {

    @InjectView
    private static List<Service> sServices;

    public static List<Service> getServices(Context context) {
        if (sServices == null) {
            Service github = new Service();
            github.setName(context.getString(R.string.lbl_github));
            github.setDrawable(R.drawable.logo_github);

            Service so = new Service();
            so.setName(context.getString(R.string.lbl_stackoverflow));
            so.setDrawable(R.drawable.logo_so);

            Service twitter = new Service();
            twitter.setName(context.getString(R.string.lbl_twitter));
            twitter.setDrawable(R.drawable.logo_twitter);

            Service skype = new Service();
            skype.setName(context.getString(R.string.lbl_skype));
            skype.setDrawable(R.drawable.logo_skype);

            Service aboutMe = new Service();
            aboutMe.setName(context.getString(R.string.lbl_about_me));
            aboutMe.setDrawable(R.drawable.logo_about_me);

            sServices = Arrays.asList(github, so, twitter, skype, aboutMe);
        }
        return sServices;
    }
}
