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

package com.github.jobs.templates.apis;

import com.github.jobs.bean.AboutMeUser;

import retrofit.http.GET;
import retrofit.http.Path;

public interface AboutMeApi {

  AboutMeApi INSTANCE = ApiUtils.get("https://api.about.me/api/v2/json/", AboutMeApi.class);

  @GET("/user/view/{user}?client_id=8c63fb1f3ee9fe2bf1d4e0f7888d992607ba7ad2&on_match=true&extended=true")
  AboutMeUser getAboutMeUser(@Path("user") String user);
}
