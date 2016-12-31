package com.sangnd.piggyboom.model;

/**
 * Created by sang on 12/31/16.
 */
public class LoginData {
    private String access_token;

    public LoginData(String token) {
        access_token = token;
    }

    public String toString() {
        return String.format("access_token=%s&loginType=1", access_token);
    }
}
