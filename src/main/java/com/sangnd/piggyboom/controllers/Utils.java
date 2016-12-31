package com.sangnd.piggyboom.controllers;

import com.mashape.unirest.http.JsonNode;
import org.json.JSONObject;

/**
 * Created by sang on 12/31/16.
 */
public class Utils {
    public JSONObject saveLoginData(JsonNode data){
        JSONObject res;
        res =  data.getObject().getJSONObject("_d");
        res.put("_t", data.getObject().getLong("_t"));
        return res;
    }

    public void sleep(int time){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean checkResponse(JsonNode responseData) {
        if (responseData.getObject().getJSONObject("_d").getInt("ret") == 0) {
            return true;
        }
        return false;
    }
}
