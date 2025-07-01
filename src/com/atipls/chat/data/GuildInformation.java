package com.atipls.chat.data;

import java.util.Hashtable;

import com.atipls.chat.State;
import com.atipls.chat.model.Message;
import com.atipls.chat.model.User;

import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONObject;

public class GuildInformation {
    public boolean activeRequest;
    private State s;

    private Hashtable<String, Integer> colors;
    private Hashtable<String, String> names;

    private Hashtable<String, ArrayList<WeakReference<TextView>>> views;
    private ArrayList<String> keys;

    public GuildInformation(State s) {
        this.s = s;
        colors = new Hashtable<String, Integer>();
        names = new Hashtable<String, String>();
        views = new Hashtable<String, ArrayList<WeakReference<TextView>>>();
        keys = new ArrayList<String>();
    }

    public void fetch() {
        JSONObject reqData = new JSONObject();
        reqData.put("guild_id", s.selectedGuild.id);

        JSONArray requestIds = new JSONArray();

        for (int i = 0; i < s.messages.size(); i++) {
            Message msg = (Message) s.messages.get(i);
            long userId = msg.author.id;
            if (requestIds.indexOf(userId) == -1
                    && !has(msg.author)) {
                requestIds.add(userId);
            }
        }

        reqData.put("user_ids", requestIds);

        JSONObject msg = new JSONObject();
        msg.put("op", 8);
        msg.put("d", reqData);
        s.gateway.send(msg);
    }

    public int get(long userId) {
        // name colors are not applicable in non-guild contexts
        if (s.isDM || s.selectedGuild == null)
            return 0;

        String key = String.valueOf(userId) + String.valueOf(s.selectedGuild.id);

        Integer result = (Integer) colors.get(key);
        if (result != null)
            return result.intValue();

        // name colors cannot be fetched without gateway (technically can but
        // isn't practical)
        if (!s.gatewayActive())
            return 0;

        if (!activeRequest) {
            activeRequest = true;
            fetch();
        }
        return 0;
    }

    public int get(User user) {
        return get(user.id);
    }

    public void set(final String key, final int color, final String name) {
        if (!colors.containsKey(key) && colors.size() >= 50) {
            String firstHash = (String) keys.get(0);
            colors.remove(firstHash);
            names.remove(firstHash);
            keys.remove(0);
        }

        colors.put(key, Integer.valueOf(color));
        if (name != null) names.put(key, name);
        keys.add(key);

        if (!views.containsKey(key))
            return;

        s.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<WeakReference<TextView>> references = views.get(key);
                views.remove(key);
                if (references == null || references.size() == 0)
                    return;
                for (WeakReference<TextView> ref : references) {
                    TextView textView = ref.get();
                    if (textView != null) {
                        textView.setTextColor(color | 0xFF000000);
                        textView.setText(name != null ? name : textView.getText());
                    }
                }
                references.clear();
            }
        });
    }

    public boolean has(User user) {
        if (s.isDM || s.selectedGuild == null)
            return false;
        String key = String.valueOf(user.id) + String.valueOf(s.selectedGuild.id);
        return colors.containsKey(key);
    }

    public void load(TextView textView, User user) {
        if (s.isDM || s.selectedGuild == null) {
            textView.setText(user.name);
            return;
        }

        String key = String.valueOf(user.id) + String.valueOf(s.selectedGuild.id);
        if (has(user)) {
            Integer color = colors.get(key);
            String name = names.get(key);

            textView.setText(name != null ? name : user.name);
            textView.setTextColor(color != null ? color | 0xFF000000 : 0xFFFFFFFF);
            return;
        }

        if (!views.containsKey(key)) {
            views.put(key, new ArrayList<WeakReference<TextView>>());
        }

        final ArrayList<WeakReference<TextView>> references = views.get(key);
        references.add(new WeakReference<TextView>(textView));

        textView.setText(user.name);
        textView.setTextColor(0xFFFFFFFF);
    }
}