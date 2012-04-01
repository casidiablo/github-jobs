package com.github.jobs.utils;

import android.app.Activity;
import android.content.Context;
import com.codeslap.topy.Topy;
import com.github.jobs.ui.phone.DummyActivity;

public class UIUtils {
    public static Class<? extends Activity> getDummyClass(Context context) {
        if (Topy.isHoneycombTablet(context)) {
            return com.github.jobs.ui.tablet.DummyActivity.class;
        }
        return DummyActivity.class;
    }
}
