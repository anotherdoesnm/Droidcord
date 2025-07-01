package com.atipls.chat.model;

import com.atipls.chat.State;

import cc.nnproject.json.JSONObject;

public class User extends Snowflake implements HasIcon {
    public String name;
    public String iconHash;

    public User(State s, JSONObject data) {
        super(Long.parseLong(data.getString("id")));

        name = data.getString("global_name", null);
        if (name == null)
            name = data.getString("username", "(no name)");

        iconHash = data.getString("avatar", null);
    }

    public Long getIconID() {
        return id;
    }

    public String getIconHash() {
        return iconHash;
    }

    public String getIconType() {
        return "/avatars/";
    }

    public void iconLoaded(State s) {
    }

    public void largeIconLoaded(State s) {
        iconLoaded(s);
    }
}
