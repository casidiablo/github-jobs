package com.github.api;

import com.github.bean.User;
import com.github.util.HttpHandler;
import com.google.gson.Gson;

/**
 * @author cristian
 * @version 1.0
 */
public class GithubApi {
    public User getUser(String username) {
        String url = String.format(ApiConstants.API_URL, String.format(ApiConstants.GET_USER, username));
        try {
            String response = HttpHandler.getInstance().getRequest(url);
            // convert json to object
            Gson gson = new Gson();
            return gson.fromJson(response, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
