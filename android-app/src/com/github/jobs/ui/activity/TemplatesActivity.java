package com.github.jobs.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.github.jobs.R;
import com.github.jobs.ui.fragment.TemplatesListFragment;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplatesActivity extends TrackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.base_fragment_activity);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(TemplatesListFragment.TAG);
        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.container, new TemplatesListFragment(), TemplatesListFragment.TAG);
            ft.commit();
        }
    }
}
