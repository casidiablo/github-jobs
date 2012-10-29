package com.github.jobs.utils;

import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;

/**
 *
 * @author cristian
 */
public abstract class TabListenerAdapter implements ActionBar.TabListener {
    @Override
    public abstract void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft);

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
