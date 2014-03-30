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

package com.github.jobs.templates.fetcher;

import android.text.TextUtils;
import com.github.jobs.bean.SOSearchResponse;
import com.github.jobs.bean.SOUser;
import com.github.jobs.utils.JsonMapper;
import com.github.kevinsawicki.http.HttpRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cristian
 * @version 1.0
 */
public class StackOverflowUsersFetcher {
  public static final String URL =
      "https://api.stackexchange.com/2.1/users?order=desc&sort=reputation&inname=%s&site=stackoverflow&key=pi7fgVg11VspVuG0kdB2PA((";

  public List<SOUser> findUser(String username) {
    if (TextUtils.isEmpty(username)) {
      return new ArrayList<SOUser>();
    }
    String url = String.format(URL, username);
    try {
      InputStream response = HttpRequest.get(url).stream();
      SOSearchResponse soSearchResponse = JsonMapper.getObject(response, SOSearchResponse.class);
      if (soSearchResponse == null) {
        return null;
      }
      return soSearchResponse.getItems();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
