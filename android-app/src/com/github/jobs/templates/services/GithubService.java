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
public class GithubService implements ServiceContract {
    private static final String GITHUB_URL = "http://github.com/%s";

    @Override
    public int getId() {
        return R.id.service_github;
    }

    @Override
    public int getServiceLabel() {
        return R.string.lbl_github;
    }

    @Override
    public int getDrawable() {
        return R.drawable.logo_github;
    }

    @Override
    public int getHint() {
        return R.string.your_github_username;
    }

    @Override
    public int getAddServiceLabel() {
        return R.string.get_github_user_info;
    }

    @Override
    public String getType() {
        return "github";
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
                String url = String.format(GITHUB_URL, username);
                return getString(R.string.cover_letter_footer, getLabel(), url);
            }
        };
    }
}
