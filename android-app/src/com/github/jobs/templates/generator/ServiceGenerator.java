package com.github.jobs.templates.generator;

import android.content.Context;
import com.github.jobs.R;
import com.github.jobs.bean.TemplateService;

/**
 * @author cristian
 */
public abstract class ServiceGenerator {

    private final Context mContext;

    public ServiceGenerator(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public String getString(int resId) {
        return mContext.getString(resId);
    }

    public String getString(int resId, Object... args) {
        return mContext.getString(resId, args);
    }

    public String generate(TemplateService templateService) {
        return getString(R.string.cover_letter_footer, getLabel(), templateService.getData());
    }

    protected abstract String getLabel();
}
