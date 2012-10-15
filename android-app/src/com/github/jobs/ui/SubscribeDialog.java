package com.github.jobs.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.codeslap.groundy.Groundy;
import com.codeslap.groundy.ReceiverFragment;
import com.github.jobs.R;
import com.github.jobs.resolver.EmailSubscriberResolver;

/**
 * @author cristian
 */
public class SubscribeDialog extends SherlockFragmentActivity implements View.OnClickListener {

    private EditText mEmail;

    private EmailSubscriberReceiver mEmailSubscriberReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_dialog);

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
                Bundle extras = new Bundle();
                if (getIntent().getExtras() != null) {
                    extras.putAll(getIntent().getExtras());
                }
                extras.putString(EmailSubscriberResolver.EXTRA_EMAIL, mEmail.getText().toString());
                Groundy.queue(this, EmailSubscriberResolver.class, mEmailSubscriberReceiver.getReceiver(), extras);
                break;
        }
    }

    private static class EmailSubscriberReceiver extends ReceiverFragment {
        @Override
        protected void onFinished(Bundle resultData) {
            super.onFinished(resultData);
            Toast.makeText(getActivity(), R.string.subscribed, Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        @Override
        protected void onError(Bundle resultData) {
            super.onError(resultData);
            Toast.makeText(getActivity(), R.string.error_subscribing, Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        @Override
        protected void onProgressChanged(boolean running) {
            ((SubscribeDialog) getActivity()).progress(running);
        }
    }

    private void progress(boolean running) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (running) {
            imm.hideSoftInputFromWindow(mEmail.getWindowToken(), 0);
        } else {
            mEmail.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
