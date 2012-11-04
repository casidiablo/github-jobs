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
import com.github.jobs.api.GithubJobsApi;
import com.codeslap.groundy.CallResolver;
import com.codeslap.groundy.Groundy;
import com.github.jobs.bean.SearchPack;

/**
 * @author cristian
 */
public class EmailSubscriberResolver extends CallResolver {

    public static final String EXTRA_EMAIL = "com.github.jobs.extra.email";
    public static final String EXTRA_SEARCH = "com.github.jobs.extra.search";

    private boolean mResult;

    @Override
    protected void updateData() {
        Bundle parameters = getParameters();
        SearchPack searchPack = (SearchPack) parameters.getParcelable(EXTRA_SEARCH);
        String email = parameters.getString(EXTRA_EMAIL);
        String description = searchPack.getSearch();
        String location = searchPack.getLocation();
        boolean fullTime = searchPack.isFullTime();
        mResult = GithubJobsApi.subscribe(email, description, location, fullTime);
    }

    @Override
    protected void prepareResult() {
        if (mResult) {
            setResultCode(Groundy.STATUS_FINISHED);
        }
    }

    @Override
    protected boolean keepWifiOn() {
        return true;
    }
}
