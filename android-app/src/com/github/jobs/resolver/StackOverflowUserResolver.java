package com.github.jobs.resolver;

import android.os.Bundle;
import com.codeslap.groundy.CallResolver;
import com.codeslap.groundy.Groundy;
import com.github.jobs.bean.SOUser;
import com.github.jobs.templates.fetcher.StackOverflowUsersFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cristian
 * @version 1.0
 */
public class StackOverflowUserResolver extends CallResolver {
    public static final String EXTRA_SEARCH = "com.github.jobs.extra.search";
    public static final String RESULT_USERS = "com.github.jobs.result.users";

    private List<SOUser> mUsers;

    @Override
    protected void updateData() {
        Bundle parameters = getParameters();
        String search = parameters.getString(EXTRA_SEARCH);

        StackOverflowUsersFetcher stackOverflowUsersFetcher = new StackOverflowUsersFetcher();
        mUsers = stackOverflowUsersFetcher.findUser(search);
    }

    @Override
    protected void prepareResult() {
        if (mUsers == null) {
            setResultCode(Groundy.STATUS_ERROR);
            return;
        }

        // pack the result in an parcelable array list
        Bundle resultData = getResultData();
        ArrayList<SOUser> SOUsers = new ArrayList<SOUser>(mUsers);
        resultData.putParcelableArrayList(RESULT_USERS, SOUsers);

        // everything went fine :)
        setResultCode(Groundy.STATUS_FINISHED);
    }
}
