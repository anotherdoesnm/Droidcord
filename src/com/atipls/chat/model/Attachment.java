package com.atipls.chat.model;

import com.atipls.chat.State;
import com.atipls.chat.Util;

import android.content.res.Resources;

import cc.nnproject.json.JSONObject;

public class Attachment {
    private static final String[] nonTextFormats = { ".zip", ".rar", ".7z",
            ".exe", ".jar", ".apk", ".sis", ".sisx", ".bin", ".mp3", ".wav",
            ".ogg", ".m4a", ".amr", ".flac", ".mid", ".mmf", ".mp4", ".3gp" };

    public String url;
    public String previewUrl;
    public String name;
    public int size;
    public boolean supported;
    public boolean isText;

    public Attachment(State s, JSONObject data) {
        String proxyUrl = data.getString("proxy_url");
        url = s.cdn + proxyUrl.substring("https://media.discordapp.net".length());

        name = data.getString("filename", "Unnamed file");
        size = data.getInt("size", 0);

        if (!data.has("width")) {
            supported = false;
            isText = (Util.indexOfAny(name.toLowerCase(), nonTextFormats, 0) == -1);
            return;
        }

        supported = true;
        int imageWidth = data.getInt("width");
        int imageHeight = data.getInt("height");

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        int[] size = Util.resizeFit(imageWidth, imageHeight, screenWidth,
                screenHeight);

        previewUrl = "http://" + proxyUrl.substring("https://".length())
                + "&format=png&width=" + size[0] + "&height=" + size[1];
    }
}