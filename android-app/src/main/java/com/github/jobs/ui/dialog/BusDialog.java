package com.github.jobs.ui.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.github.jobs.GithubJobsApplication;
import com.squareup.otto.Bus;
import javax.inject.Inject;

public class BusDialog extends DialogFragment {
  @Inject Bus bus;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((GithubJobsApplication) getActivity().getApplication()).inject(this);
  }
}
