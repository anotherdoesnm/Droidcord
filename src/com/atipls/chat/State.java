package com.atipls.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.atipls.chat.data.API;
import com.atipls.chat.data.GuildInformation;
import com.atipls.chat.data.Icons;
import com.atipls.chat.data.Messages;
import com.atipls.chat.model.Channel;
import com.atipls.chat.model.DirectMessage;
import com.atipls.chat.model.Guild;
import com.atipls.chat.ui.MessageListAdapter;

public class State {
    public static final int ICON_TYPE_NONE = 0;
    public static final int ICON_TYPE_SQUARE = 1;
    public static final int ICON_TYPE_CIRCLE = 2;
    public static final int ICON_TYPE_CIRCLE_HQ = 3;

    public final ExecutorService executor;
    public final Handler handler;

    public HTTP http;
    public API api;
    public GatewayThread gateway;
    public String cdn;

    public boolean use12hTime;
    public int messageLoadCount;
    public int attachmentSize;
    public int iconType = ICON_TYPE_SQUARE;
    public boolean autoReConnect;
    public boolean showRefMessage;

    public Context c;

    public long myUserId;
    public boolean isLiteProxy;

    public Icons icons;
    public GuildInformation guildInformation;
    public UnreadManager unreads;

    public ArrayList<Guild> guilds;
    public Guild selectedGuild;
    // public GuildSelector guildSelector;
    public ArrayList<Guild> subscribedGuilds;

    public ArrayList<Channel> channels;
    public Channel selectedChannel;
    // public ChannelSelector channelSelector;
    public boolean channelIsOpen;

    public Messages messages;
    public ArrayList<String> typingUsers;
    public ArrayList<Long> typingUserIDs;

    public MessageListAdapter messagesAdapter;
    public ListView messagesView;

    // Parameters for message/reply sending
    public String sendMessage;
    public long sendReference; // ID of the message the user is replying to
    public boolean sendPing;

    public boolean isDM;
    public ArrayList<DirectMessage> directMessages;
    public DirectMessage selectedDm;

    public State(Context c) {
        this.executor = Executors.newFixedThreadPool(10);
        this.handler = new Handler(Looper.getMainLooper());
        this.api = new API(this);
        this.subscribedGuilds = new ArrayList<Guild>();
        this.icons = new Icons(this);
        this.guildInformation = new GuildInformation(this);
        // unreads = new UnreadManager(this, c);
        this.guilds = new ArrayList<Guild>();
        this.channels = new ArrayList<Channel>();
        this.directMessages = new ArrayList<DirectMessage>();
        this.messages = new Messages();
    }

    public void login(String apiUrl, String gateway, String cdn, String token) {
        this.cdn = cdn;
        http = new HTTP(this, apiUrl, token);

        this.gateway = new GatewayThread(this, gateway, token);
        this.gateway.start();
    }

    public void error(String message) {
        try {
            Toast toast = Toast.makeText(c, "Error: " + message, Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            System.err.println("Error: " + message);
            e.printStackTrace();
        }
    }

    public boolean gatewayActive() {
        return gateway != null && gateway.isAlive();
    }

    public void updateUnreadIndicators(boolean isDM, long chId) {
        /*if (isDM) {
            if (dmSelector != null) dmSelector.update(chId);
		} else {
			if (channelSelector != null) channelSelector.update(chId);
			if (guildSelector != null) guildSelector.update();
		}*/
    }

    public void platformRequest(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        c.startActivity(browserIntent);
    }

    public void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }
}