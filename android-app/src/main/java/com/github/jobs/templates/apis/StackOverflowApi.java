/*
 * Copyright 2014 Some Dev
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

package com.github.jobs.templates.apis;

import retrofit.http.GET;
import retrofit.http.Query;

public interface StackOverflowApi {
  StackOverflowApi INSTANCE = ApiUtils.get("https://api.stackexchange.com/2.1", StackOverflowApi.class);

  @GET("/users?order=desc&sort=reputation&site=stackoverflow&key=pi7fgVg11VspVuG0kdB2PA((")
  com.github.jobs.bean.SOSearchResponse findUser(@Query("inname") String username);
}
