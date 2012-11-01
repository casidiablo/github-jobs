package com.github.jobs.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.codeslap.groundy.ReceiverFragment;
import com.github.jobs.R;
import com.github.jobs.ui.dialog.SubscribeDialog;

/**
 * @author cristian
 * @version 1.0
 */
public class EmailSubscriberReceiver extends ReceiverFragment {
    @Override
    protected void onFinished(Bundle resultData) {
        super.onFinished(resultData);
        FragmentActivity activity = getActivity();
        if (activity == null || !isAdded()) {
            return;
        }
        Toast.makeText(activity, R.string.subscribed, Toast.LENGTH_LONG).show();
        activity.finish();
    }

    @Override
    protected void onError(Bundle resultData) {
        super.onError(resultData);
        FragmentActivity activity = getActivity();
        if (activity == null || !isAdded()) {
            return;
        }
        Toast.makeText(activity, R.string.error_subscribing, Toast.LENGTH_LONG).show();
        activity.finish();
    }

    @Override
    protected void onProgressChanged(boolean running) {
        FragmentActivity activity = getActivity();
        if (activity instanceof SubscribeDialog) {
            ((SubscribeDialog) activity).progress(running);
        }
    }
}
