package com.github.jobs.templates;

import android.content.Context;
import com.github.jobs.R;

/**
 * @author cristian
 */
public class WebsiteGenerator extends ServiceGenerator {
    public WebsiteGenerator(Context context) {
        super(context);
    }

    @Override
    protected String getLabel() {
        return getString(R.string.lbl_website);
    }
}
