package com.github.jobs.templates.generator;

import android.content.Context;
import com.github.jobs.R;

/**
 * @author cristian
 */
public class StackOverflowGenerator extends ServiceGenerator {
    public StackOverflowGenerator(Context context) {
        super(context);
    }

    @Override
    protected String getLabel() {
        return getString(R.string.lbl_stack_overflow);
    }
}
