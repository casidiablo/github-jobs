package com.github.jobs.templates.services;

import android.content.Context;
import com.github.jobs.R;
import com.github.jobs.templates.ServiceContract;
import com.github.jobs.templates.ServiceGenerator;

/**
 * @author cristian
 * @version 1.0
 */
public class AboutMeService implements ServiceContract {
    @Override
    public int getId() {
        return R.id.service_about_me;
    }

    @Override
    public int getServiceLabel() {
        return R.string.lbl_about_me;
    }

    @Override
    public int getDrawable() {
        return R.drawable.logo_about_me;
    }

    @Override
    public int getHint() {
        return R.string.your_about_me_username;
    }

    @Override
    public int getAddServiceLabel() {
        return R.string.get_about_me_user_info;
    }

    @Override
    public String getType() {
        return "about.me";
    }

    @Override
    public ServiceGenerator getGenerator(Context context) {
        return null;
    }
}
