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

package com.github.jobs.api;

import com.github.jobs.bean.Job;
import com.github.jobs.templates.apis.ApiUtils;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;

public interface GithubJobsApi {

  GithubJobsApi INSTANCE = ApiUtils.get("https://jobs.github.com/", GithubJobsApi.class);

  @GET("/positions.json")
  List<Job> search(@Query("search") String search,
                   @Query("location") String location,
                   @Query("page") int page,
                   @Query("full_time") boolean isFulltime);

  @POST("/subscribe")
  @Multipart
  String subscribe(@Part("email") String email,
                   @Part("description") String description,
                   @Part("location") String location,
                   @Part("full_time") boolean fullTime);
}
