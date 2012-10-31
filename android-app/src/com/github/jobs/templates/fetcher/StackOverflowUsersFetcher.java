package com.github.jobs.templates.fetcher;

import com.github.jobs.bean.SOUser;
import com.github.util.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * @author cristian
 * @version 1.0
 */
public class StackOverflowUsersFetcher {
    public static final String URL = "https://api.stackexchange.com/2.1/users?order=desc&sort=reputation&inname=%s&site=stackoverflow&key=pi7fgVg11VspVuG0kdB2PA((";

    public List<SOUser> findUser(String username) {
        String url = String.format(URL, username);
        try {
            String response = HttpHandler.getInstance().getRequest(url);
            // convert json to object
            Gson gson = new Gson();
            TypeToken<List<SOUser>> typeToken = new TypeToken<List<SOUser>>() {
            };
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            return gson.fromJson(jsonArray.toString(), typeToken.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
