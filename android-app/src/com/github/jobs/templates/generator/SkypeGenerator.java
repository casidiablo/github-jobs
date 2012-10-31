package com.github.jobs.templates.generator;

import android.content.Context;
import com.github.jobs.R;
import com.github.jobs.bean.TemplateService;

/**
 * @author cristian
 * @version 1.0
 */
public class SkypeGenerator extends ServiceGenerator {

    private static final String SKYPE_USERNAME = "<strong>%s</strong>";

    public SkypeGenerator(Context context) {
        super(context);
    }

    @Override
    protected String getLabel() {
        return getString(R.string.lbl_skype);
    }

    @Override
    public String generate(TemplateService templateService) {
        String username = templateService.getData();
        String data = String.format(SKYPE_USERNAME, username);
        return getString(R.string.cover_letter_footer, getLabel(), data);
    }
}
