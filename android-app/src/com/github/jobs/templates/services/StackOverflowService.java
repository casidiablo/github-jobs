package com.github.jobs.templates.services;

import android.content.Context;
import com.github.jobs.R;
import com.github.jobs.templates.ServiceContract;
import com.github.jobs.templates.ServiceGenerator;

/**
 * @author cristian
 * @version 1.0
 */
public class StackOverflowService implements ServiceContract {

    public static final String TYPE = "stackoverflow";

    @Override
    public int getId() {
        return R.id.service_so;
    }

    @Override
    public int getServiceLabel() {
        return R.string.lbl_stack_overflow;
    }

    @Override
    public int getDrawable() {
        return R.drawable.logo_so;
    }

    @Override
    public int getHint() {
        // this service is not configured like the normal ones
        return 0;
    }

    @Override
    public int getAddServiceLabel() {
        // this service is not configured like the normal ones
        return 0;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ServiceGenerator getGenerator(Context context) {
        return new ServiceGenerator(context) {
            @Override
            protected String getLabel() {
                return getString(getServiceLabel());
            }
        };
    }
}
