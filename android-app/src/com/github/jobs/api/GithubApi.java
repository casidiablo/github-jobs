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

package com.github.jobs.api;

import com.github.jobs.bean.User;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

/**
 * @author cristian
 * @version 1.0
 */
public class GithubApi {
    public User getUser(String username) {
        String url = String.format(ApiConstants.API_URL, String.format(ApiConstants.GET_USER, username));
        try {
            String response = HttpRequest.get(url).body();
            // convert json to object
            Gson gson = new Gson();
            return gson.fromJson(response, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
