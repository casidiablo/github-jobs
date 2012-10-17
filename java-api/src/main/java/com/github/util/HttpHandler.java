/*
 * Copyright 2012 CodeSlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author cristian
 */
public class HttpHandler {
    private static HttpHandler instance = new HttpHandler();

    private HttpHandler() {
    }

    public static HttpHandler getInstance() {
        return instance;
    }

    public String getRequest(String url) {
        return executeRequest(new HttpGet(url));
    }

    public String postRequest(String url, Map<String, String> parameters) {
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(mapToEntity(parameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return executeRequest(httpPost);
    }

    private String executeRequest(HttpUriRequest request) {
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            HttpResponse execute = client.execute(request);
            HttpEntity response = execute.getEntity();
            return streamToString(response.getContent());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Convert a Map to UrlEncodedFormEntity
     *
     * @param parameters A Map of key:value
     * @return UrlEncodedFormEntity using UTF-8 encoding
     * @throws java.io.UnsupportedEncodingException
     *          if UTF-8 encoding is not supported
     */
    private static UrlEncodedFormEntity mapToEntity(Map<String, String> parameters)
            throws UnsupportedEncodingException {
        if (parameters == null) {
            throw new IllegalArgumentException("Invalid parameters unable map to Entity");
        }

        if (parameters.isEmpty()) {
            return null;
        }

        ArrayList<NameValuePair> parametersList = new ArrayList<NameValuePair>();

        for (@SuppressWarnings("rawtypes") Map.Entry element : parameters.entrySet()) {
            NameValuePair nameValuePair = new BasicNameValuePair((String) element.getKey(), (String) element.getValue());
            parametersList.add(nameValuePair);
        }

        return new UrlEncodedFormEntity(parametersList, HTTP.UTF_8);
    }


    private String streamToString(InputStream is) throws IOException {
        StringWriter writer = new StringWriter();
        char[] buffer = new char[1024];
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, n);
        }
        is.close();
        return writer.toString();
    }
}
