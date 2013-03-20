/*
 * Copyright 2012 CodeSlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jobs.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ViewSwitcher;
import com.github.jobs.R;
import com.github.jobs.resolver.EmailSubscriberTask;
import com.github.jobs.ui.fragment.EmailSubscriberReceiver;
import com.telly.groundy.Groundy;
import com.telly.groundy.ReceiverFragment;

import java.util.regex.Matcher;

import static com.github.jobs.utils.AnalyticsHelper.NAME_SUBSCRIBE_DIALOG;
import static com.github.jobs.utils.AnalyticsHelper.getTracker;

/**
 * @author cristian
 */
public class SubscribeDialog extends TrackFragmentDialog implements View.OnClickListener {

  private EditText mEmail;

  private EmailSubscriberReceiver mEmailSubscriberReceiver;
  private ViewSwitcher mContentSwitcher;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getTracker(this).trackPageView(NAME_SUBSCRIBE_DIALOG);
    setContentView(R.layout.subscribe_dialog);

    mContentSwitcher = (ViewSwitcher) findViewById(R.id.switcher_email_subscription);
    mEmail = (EditText) findViewById(R.id.edit_email);
    findViewById(R.id.btn_subscribe).setOnClickListener(this);

    FragmentManager fm = getSupportFragmentManager();
    mEmailSubscriberReceiver = (EmailSubscriberReceiver) fm.findFragmentByTag(ReceiverFragment.TAG);
    if (mEmailSubscriberReceiver == null) {
      mEmailSubscriberReceiver = new EmailSubscriberReceiver();
      fm.beginTransaction().add(mEmailSubscriberReceiver, ReceiverFragment.TAG).commit();
      progress(false);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_subscribe:
        String emailAddress = mEmail.getText().toString().trim();
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(emailAddress);
        if (!matcher.find()) {
          mEmail.setError(getString(R.string.invalid_email_address));
          mEmail.requestFocus();
          return;
        }

        Bundle extras = new Bundle();
        if (getIntent().getExtras() != null) {
          extras.putAll(getIntent().getExtras());
        }
        extras.putString(EmailSubscriberTask.EXTRA_EMAIL, emailAddress);
        Groundy.create(this, EmailSubscriberTask.class)
            .receiver(mEmailSubscriberReceiver.getReceiver())
            .params(extras)
            .queue();
        break;
    }
  }

  public void progress(boolean running) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    if (running) {
      imm.hideSoftInputFromWindow(mEmail.getWindowToken(), 0);
      mContentSwitcher.showNext();
    } else {
      mEmail.requestFocus();
      getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
      mContentSwitcher.showPrevious();
    }
  }
}
