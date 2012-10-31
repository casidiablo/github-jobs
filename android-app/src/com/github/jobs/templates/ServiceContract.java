package com.github.jobs.templates;

import android.content.Context;

/**
 * @author cristian
 * @version 1.0
 */
public interface ServiceContract {
    int getId();

    int getServiceLabel();

    int getDrawable();

    int getHint();

    int getAddServiceLabel();

    String getType();

    ServiceGenerator getGenerator(Context context);
}
