package com.github.jobs.ui.phone;

import android.support.v4.app.Fragment;
import com.codeslap.topy.BaseSinglePaneActivity;
import com.github.jobs.ui.DummyFragment;

public class DummyActivity extends BaseSinglePaneActivity {
    @Override
    protected Fragment onCreatePane() {
        return new DummyFragment();
    }
}
