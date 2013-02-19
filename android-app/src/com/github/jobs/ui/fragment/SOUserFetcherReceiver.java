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
import com.github.jobs.resolver.StackOverflowUserTask;
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
            ArrayList<SOUser> users = resultData.getParcelableArrayList(StackOverflowUserTask.RESULT_USERS);
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
