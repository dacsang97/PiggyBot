package com.sangnd.piggyboom.model;

import org.json.JSONObject;

/**
 * Created by sang on 12/31/16.
 */
public class PlayData {
    private String mtkey;
    private String skey;
    private int uid;
    private String version = "2.5.0";
    private Long time;

    public PlayData(JSONObject data) {
        mtkey = data.getString("mtkey");
        skey = data.getString("skey");
        uid = data.getInt("uid");
        time = data.getLong("_t");
    }

    @Override
    public String toString() {
        return String.format("_mtkey=%s&_skey=%s&_uid=%s&_version=%s&time=%s", mtkey, skey, Integer.toString(uid), version, time);
    }
}
