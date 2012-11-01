package com.github.jobs.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.codeslap.groundy.ReceiverFragment;
import com.github.jobs.R;
import com.github.jobs.bean.SOUser;
import com.github.jobs.resolver.StackOverflowUserResolver;
import com.github.jobs.utils.AppUtils;

import java.util.ArrayList;

public class SOUserFetcherReceiver extends ReceiverFragment {

    @Override
    protected void onError(Bundle resultData) {
        super.onError(resultData);
        FragmentActivity activity = getActivity();
        if (activity == null || !isAdded()) {
            return;
        }
        if (!AppUtils.isOnline(activity)) {
            Toast.makeText(activity, R.string.error_fetching_search_result_network, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, R.string.error_fetching_search_result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onFinished(Bundle resultData) {
        super.onFinished(resultData);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.base_container);
        if (fragment instanceof SOUserPickerFragment) {
            SOUserPickerFragment soUserPickerFragment = (SOUserPickerFragment) fragment;
            ArrayList<SOUser> users = resultData.getParcelableArrayList(StackOverflowUserResolver.RESULT_USERS);
            soUserPickerFragment.updateItems(users);
        } else {
            Log.wtf("FragmentReceiver", "The fragment isn't an instance of SOUserFetcherReceiver");
        }
    }

    @Override
    protected void onProgressChanged(boolean running) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        ((SherlockFragmentActivity) activity).setSupportProgressBarIndeterminateVisibility(running);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (running) {
            FragmentManager manager = getFragmentManager();
            Fragment fragmentById = manager.findFragmentById(R.id.base_container);
            if (fragmentById instanceof SOUserPickerFragment) {
                SOUserPickerFragment pickerFragment = (SOUserPickerFragment) fragmentById;
                imm.hideSoftInputFromWindow(pickerFragment.getWindowToken(), 0);
            }

        }
    }
}
