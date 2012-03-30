package com.github.jobs;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class HomeActivity extends SherlockFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
