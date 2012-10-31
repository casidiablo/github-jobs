package com.github.jobs.templates.services;

import android.content.Context;
import com.github.jobs.R;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.templates.ServiceContract;
import com.github.jobs.templates.ServiceGenerator;

/**
 * @author cristian
 * @version 1.0
 */
public class SkypeService implements ServiceContract {
    private static final String SKYPE_USERNAME = "<strong>%s</strong>";

    @Override
    public int getId() {
        return R.id.service_skype;
    }

    @Override
    public int getServiceLabel() {
        return R.string.lbl_skype;
    }

    @Override
    public int getDrawable() {
        return R.drawable.logo_skype;
    }

    @Override
    public int getHint() {
        return R.string.your_skype_username;
    }

    @Override
    public int getAddServiceLabel() {
        return R.string.get_skype_user_info;
    }

    @Override
    public String getType() {
        return "skype";
    }

    @Override
    public ServiceGenerator getGenerator(Context context) {
        return new ServiceGenerator(context) {
            @Override
            protected String getLabel() {
                return getString(getServiceLabel());
            }

            @Override
            public String generate(TemplateService templateService) {
                String username = templateService.getData();
                String data = String.format(SKYPE_USERNAME, username);
                return getString(R.string.cover_letter_footer, getLabel(), data);
            }
        };
    }
}
