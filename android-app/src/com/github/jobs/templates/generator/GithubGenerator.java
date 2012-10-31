package com.github.jobs.templates.generator;

import android.content.Context;
import com.github.jobs.R;
import com.github.jobs.bean.TemplateService;

/**
 * @author cristian
 * @version 1.0
 */
public class GithubGenerator extends ServiceGenerator {

    private static final String GITHUB_URL = "http://github.com/%s";

    public GithubGenerator(Context context) {
        super(context);
    }

    @Override
    protected String getLabel() {
        return getString(R.string.lbl_github);
    }

    @Override
    public String generate(TemplateService templateService) {
        String username = templateService.getData();
        String url = String.format(GITHUB_URL, username);
        return getString(R.string.cover_letter_footer, getLabel(), url);
    }
}
