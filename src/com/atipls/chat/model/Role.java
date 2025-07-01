package com.atipls.chat.model;

import java.util.ArrayList;

import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;

public class Role extends Snowflake {
    public int color;
    public long permissions = 0;
    // public int position;

    public Role(JSONObject data) {
        super(Long.parseLong(data.getString("id")));
        color = data.getInt("color");
        // permissions = Long.parseLong(data.getString("permissions"));
        // position = data.getInt("position");
    }

    public static ArrayList<Role> parseRoles(JSONArray arr) {
        ArrayList<Role> result = new ArrayList<Role>();

        for (int i = arr.size() - 1; i >= 0; i--) {
            for (int a = arr.size() - 1; a >= 0; a--) {
                JSONObject data = arr.getObject(i);
                if (data.getInt("position", i) != i)
                    continue;

                result.add(new Role(data));
            }
        }

        return result;
    }

    public static ArrayList<Role> parseGuildMemberRoles(Guild g, JSONArray arr) {
        ArrayList<Role> result = new ArrayList<Role>();

        for (int i = arr.size() - 1; i >= 0; i--) {
            for (Role role : g.roles) {
                if (role.id == Long.parseLong(arr.getString(i)))
                    result.add(role);
            }
        }

        return result;
    }
}
