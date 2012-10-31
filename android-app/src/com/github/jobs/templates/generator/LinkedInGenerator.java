package com.github.jobs.templates.generator;

import android.content.Context;
import com.github.jobs.R;
import com.github.jobs.bean.TemplateService;

/**
 * @author cristian
 * @version 1.0
 */
public class LinkedInGenerator extends ServiceGenerator {

    private static final String LINKED_IN_URL = "http://www.linkedin.com/in/%s";

    public LinkedInGenerator(Context context) {
        super(context);
    }

    @Override
    protected String getLabel() {
        return getString(R.string.lbl_linked_in);
    }

    @Override
    public String generate(TemplateService templateService) {
        String username = templateService.getData();
        String url = String.format(LINKED_IN_URL, username);
        return getString(R.string.cover_letter_footer, getLabel(), url);
    }
}
