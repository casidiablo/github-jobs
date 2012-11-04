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

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.codeslap.groundy.DetachableResultReceiver;
import com.codeslap.groundy.Groundy;
import com.github.jobs.bean.SearchPack;
import com.github.jobs.resolver.SearchJobsResolver;
import com.github.jobs.ui.activity.HomeActivity;

/**
 * @author cristian
 */
public class SearchReceiverFragment implements DetachableResultReceiver.Receiver {
    private SherlockFragmentActivity mActivity;
    private boolean mSyncing;
    private final DetachableResultReceiver mReceiver;

    public SearchReceiverFragment(SherlockFragmentActivity activity) {
        mActivity = activity;
        mReceiver = new DetachableResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (mActivity == null) {
            return;
        }

        SearchPack searchPack = null;
        if (resultData.containsKey(SearchJobsResolver.DATA_SEARCH_PACK)) {
            searchPack = (SearchPack) resultData.get(SearchJobsResolver.DATA_SEARCH_PACK);
        }

        switch (resultCode) {
            case Groundy.STATUS_RUNNING: {
                mSyncing = true;
                break;
            }
            case Groundy.STATUS_FINISHED: {
                mSyncing = false;
                if (searchPack != null) {
                    ((HomeActivity) mActivity).onFinished(resultData, searchPack);
                }
                break;
            }
            case Groundy.STATUS_ERROR: {
                mSyncing = false;
                Toast.makeText(mActivity, resultData.getString(Groundy.KEY_ERROR), Toast.LENGTH_LONG).show();
                if (searchPack != null) {
                    ((HomeActivity) mActivity).onError(searchPack);
                }
                break;
            }
        }
        mActivity.setSupportProgressBarIndeterminateVisibility(mSyncing);
        if (searchPack != null) {
            ((HomeActivity) mActivity).onProgressChanged(mSyncing, searchPack);
        }
    }

    public ResultReceiver getReceiver() {
        return mReceiver;
    }

    public void setActivity(SherlockFragmentActivity activity) {
        mActivity = activity;
    }
}
