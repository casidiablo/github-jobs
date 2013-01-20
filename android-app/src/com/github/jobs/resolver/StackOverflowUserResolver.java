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

package com.github.jobs.resolver;

import android.os.Bundle;
import com.codeslap.groundy.Groundy;
import com.codeslap.groundy.GroundyTask;
import com.github.jobs.bean.SOUser;
import com.github.jobs.templates.fetcher.StackOverflowUsersFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cristian
 * @version 1.0
 */
public class StackOverflowUserResolver extends GroundyTask {
    public static final String EXTRA_SEARCH = "com.github.jobs.extra.search";
    public static final String RESULT_USERS = "com.github.jobs.result.users";

    @Override
    protected void doInBackground() {
        Bundle parameters = getParameters();
        String search = parameters.getString(EXTRA_SEARCH);

        StackOverflowUsersFetcher stackOverflowUsersFetcher = new StackOverflowUsersFetcher();
        List<SOUser> users = stackOverflowUsersFetcher.findUser(search);

        if (users == null) {
            setResultCode(Groundy.STATUS_ERROR);
            return;
        }

        // pack the result in an parcelable array list
        Bundle resultData = getResultData();
        ArrayList<SOUser> SOUsers = new ArrayList<SOUser>(users);
        resultData.putParcelableArrayList(RESULT_USERS, SOUsers);

        // everything went fine :)
        setResultCode(Groundy.STATUS_FINISHED);
    }
}
