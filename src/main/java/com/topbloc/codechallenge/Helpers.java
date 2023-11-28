package com.topbloc.codechallenge;

import org.json.simple.JSONObject;

public class Helpers {
    /*
     * Standardizes the structure of the response.
     */
    public static String handleResponse(String message, Object data) {
        JSONObject apiResponse = new JSONObject();

        apiResponse.put("message", message);
        apiResponse.put("data", data);
        return apiResponse.toJSONString();
    }
}
