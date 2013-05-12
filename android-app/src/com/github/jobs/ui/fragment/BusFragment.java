package com.github.jobs.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.github.jobs.GithubJobsApplication;
import com.squareup.otto.Bus;
import javax.inject.Inject;

public class BusFragment extends Fragment {
  @Inject Bus bus;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((GithubJobsApplication) getActivity().getApplication()).inject(this);
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override public void onPause() {
    super.onPause();
    bus.unregister(this);
  }
}
