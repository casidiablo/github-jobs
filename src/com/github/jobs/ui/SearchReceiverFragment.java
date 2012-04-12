package com.github.jobs.ui;

import android.os.Bundle;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.codeslap.groundy.Groundy;
import com.codeslap.groundy.ReceiverFragment;
import com.github.jobs.resolver.SearchJobsResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cristian
 */
public class SearchReceiverFragment extends ReceiverFragment {
    private final Map<SearchPack, SearchCallback> mSearchListenerMap = new HashMap<SearchPack, SearchCallback>();

    @Override
    protected void onFinished(Bundle resultData) {
        super.onFinished(resultData);
        SearchCallback callback = getCallback(resultData);
        if (callback != null) {
            callback.onFinished(resultData);
        }
    }

    @Override
    protected void onError(Bundle resultData) {
        super.onError(resultData);
        Toast.makeText(getActivity(), resultData.getString(Groundy.KEY_ERROR), Toast.LENGTH_LONG).show();
        SearchCallback callback = getCallback(resultData);
        if (callback != null) {
            callback.onError(resultData);
        }
    }

    private SearchCallback getCallback(Bundle resultData) {
        if (resultData.containsKey(SearchJobsResolver.DATA_SEARCH_PACK)) {
            SearchPack searchPack = (SearchPack) resultData.getSerializable(SearchJobsResolver.DATA_SEARCH_PACK);
            if (mSearchListenerMap.containsKey(searchPack)) {
                return mSearchListenerMap.get(searchPack);
            }
        }
        return null;
    }

    @Override
    protected void onProgressChanged(boolean running) {
        SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
        activity.setSupportProgressBarIndeterminateVisibility(running);
    }

    public void addSearchListener(SearchPack searchPack, SearchCallback searchCallback) {
        mSearchListenerMap.put(searchPack, searchCallback);
        if (mSearchListenerMap.size() > 1) {
            ((HomeActivity) getActivity()).showTabs();
            ((HomeActivity) getActivity()).selectTab(mSearchListenerMap.size() - 1);
        } else {
            ((HomeActivity) getActivity()).hideTabs();
        }
    }

    public interface SearchCallback {
        void onFinished(Bundle resultData);

        void onError(Bundle resultData);

        void onProgressChanged(boolean running);
    }
}
