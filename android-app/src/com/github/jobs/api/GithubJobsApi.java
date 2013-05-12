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

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.jobs.bean.Job;
import com.github.jobs.bean.Search;
import com.github.jobs.utils.JsonMapper;
import com.github.kevinsawicki.http.HttpRequest;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** @author cristian */
public class GithubJobsApi {

  public static final String SUBSCRIPTION_EMAIL_PARAM = "email";
  public static final String SUBSCRIPTION_DESCRIPTION_PARAM = "description";
  public static final String SUBSCRIPTION_LOCATION_PARAM = "location";
  public static final String SUBSCRIPTION_FULL_TIME_PARAM = "full_time";
  public static final String SUBSCRIPTION_OK_PARAM = "ok";

  public static List<Job> search(Search search) {
    ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
    if (search.getSearch() != null) {
      pairs.add(new NameValuePair(ApiConstants.SEARCH, search.getSearch()));
    }
    if (search.getLocation() != null) {
      pairs.add(new NameValuePair(ApiConstants.LOCATION, search.getLocation()));
    } else if (search.getLatitude() != 0 && search.getLongitude() != 0) {
      pairs.add(new NameValuePair(ApiConstants.LATITUDE, String.valueOf(search.getLatitude())));
      pairs.add(new NameValuePair(ApiConstants.LONGITUDE, String.valueOf(search.getLongitude())));
    }
    if (search.getPage() > 0) {
      pairs.add(new NameValuePair(ApiConstants.PAGE, String.valueOf(search.getPage())));
    }
    if (search.isFullTime()) {
      pairs.add(new NameValuePair(ApiConstants.FULL_TIME, String.valueOf(search.isFullTime())));
    }
    try {
      String url = createUrl(ApiConstants.POSITIONS_URL, pairs);
      InputStream responseStream = HttpRequest.get(url).acceptGzipEncoding().acceptJson().stream();
      if (responseStream == null) {
        throw new RuntimeException("Error calling API; it returned null.");
      }
      return JsonMapper.getList(responseStream, new TypeReference<List<Job>>() {
      });
    } catch (URIException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean subscribe(String email, String description, String location,
      boolean fullTime) {
    HashMap<String, String> parameters = new HashMap<String, String>();
    parameters.put(SUBSCRIPTION_EMAIL_PARAM, email);
    parameters.put(SUBSCRIPTION_DESCRIPTION_PARAM, description);
    parameters.put(SUBSCRIPTION_LOCATION_PARAM, location);
    parameters.put(SUBSCRIPTION_FULL_TIME_PARAM, String.valueOf(fullTime));
    String response = HttpRequest.post(ApiConstants.EMAIL_SUBSCRIPTION_URL)
        .part(SUBSCRIPTION_EMAIL_PARAM, email)
        .part(SUBSCRIPTION_DESCRIPTION_PARAM, description)
        .part(SUBSCRIPTION_LOCATION_PARAM, location)
        .part(SUBSCRIPTION_FULL_TIME_PARAM, String.valueOf(fullTime))
        .body();
    return SUBSCRIPTION_OK_PARAM.equals(response);
  }

  private static String createUrl(String url, List<NameValuePair> pairs) throws URIException {
    HttpMethod method = new GetMethod(url);
    NameValuePair[] nameValuePairs = pairs.toArray(new NameValuePair[pairs.size()]);
    method.setQueryString(nameValuePairs);
    return method.getURI().getEscapedURI();
  }
}
