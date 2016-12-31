package com.sangnd.piggyboom.model;

/**
 * Created by sang on 12/31/16.
 */
public enum URL {
    LOGIN("gameNew/login/"),
    PLAY("zhuanpan/play/"),
    STEAL("userweapon/steal/"),
    INFO("userplanet/get/"),
    FR_LIST("userfriend/list/");

    public String baseUrl = "http://d2fd20abim5npz.cloudfront.net/planetpigth/m/";
    private String mUrl;

    URL(String url) {
        mUrl = baseUrl + url;
    }

    public String getUrl() {
        return mUrl;
    }
}
