package com.atipls.chat.model;

import java.util.ArrayList;

import com.atipls.chat.State;

import android.util.Log;
import cc.nnproject.json.JSONObject;

public class GuildMember extends User {
    public User user;
    public String username;
    public String name;
    public String nickname;
    public ArrayList<Role> roles;
    public long permissions;

    public GuildMember(State s, Guild g, JSONObject data) {
        super(s, data.getObject("user"));
        Log.w("GuildMember", "Parsing member: " + data.toString());

        username = data.getString("username", null);
        name = data.getString("global_name", username);
        if (data.has("nick"))
            nickname = data.getString("nick", null);

        try {
            if (data.has("roles")) {
                roles = Role.parseGuildMemberRoles(g, data.getArray("roles"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            roles = new ArrayList<Role>();
        }

        if (s.iconType == State.ICON_TYPE_NONE)
            return;

        iconHash = data.getString("avatar", null);
    }

    @Override
    public String getIconHash() {
        return iconHash;
    }

    public void iconLoaded(State s) {
    }

    public void largeIconLoaded(State s) {
        iconLoaded(s);
    }
}
