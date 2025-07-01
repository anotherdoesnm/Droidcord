package com.atipls.chat;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import java.util.concurrent.atomic.AtomicInteger;

import com.atipls.chat.model.Channel;
import com.atipls.chat.model.Guild;
import com.atipls.chat.ui.DMListAdapter;
import com.atipls.chat.ui.GuildListAdapter;

public class MainActivity extends TabActivity {
    public static State s;
    private Context mContext;

    ExpandableListView mGuildsView;
    ExpandableListAdapter mGuildsAdapter;

    ListView mDmsView;
    ListAdapter mDmsAdapter;

    private class LoadInformationRunnable implements Runnable {
        private final AtomicInteger mLoadCount = new AtomicInteger(0);

        @Override
        public void run() {
            s.api.aFetchGuilds(new Runnable() {
                @Override
                public void run() {
                    mGuildsAdapter = new GuildListAdapter(mContext, s, s.guilds);
                    s.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGuildsView.setAdapter(mGuildsAdapter);
                            if (mLoadCount.incrementAndGet() == 2)
                                showProgress(false);
                        }
                    });
                }
            });

            s.api.aFetchDirectMessages(new Runnable() {
                @Override
                public void run() {
                    mDmsAdapter = new DMListAdapter(mContext, s, s.directMessages);
                    s.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDmsView.setAdapter(mDmsAdapter);
                            if (mLoadCount.incrementAndGet() == 2)
                                showProgress(false);
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_PROGRESS);

        s = new State(this);
        mContext = this;

        TabHost tabHost = getTabHost();
        LayoutInflater.from(this).inflate(R.layout.activity_main, tabHost.getTabContentView(), true);
        tabHost.addTab(tabHost.newTabSpec("servers").setIndicator("Servers").setContent(R.id.server_tab));
        tabHost.addTab(tabHost.newTabSpec("dm").setIndicator("DMs").setContent(R.id.dm_tab));

        mGuildsView = (ExpandableListView) findViewById(R.id.servers);
        mDmsView = (ListView) findViewById(R.id.direct_messages);

        TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            View tab = tabWidget.getChildAt(i);
            ViewGroup.LayoutParams params = tab.getLayoutParams();
            params.height = 32;
            tab.setLayoutParams(params);
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (TextUtils.isEmpty(sp.getString("token", null))) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String apiUrl = sp.getString("api", null);
        String cdnUrl = sp.getString("cdn", null);
        String gatewayUrl = sp.getString("gateway", null);
        String token = sp.getString("token", null);
        int msgLoadCount = sp.getInt("messageLoadCount", 0);

        try {
            s.messageLoadCount = msgLoadCount;
            s.login(apiUrl, gatewayUrl, cdnUrl, token);

            showProgress(true);
            s.executor.execute(new LoadInformationRunnable());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mGuildsView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                s.selectedGuild = (Guild) mGuildsAdapter.getGroup(groupPosition);
                s.selectedChannel = (Channel) mGuildsAdapter.getChild(groupPosition, childPosition);
                startActivity(intent);
                return true;
            }

        });
    }

    public void showProgress(final boolean show) {
        this.setProgressBarVisibility(show);
        this.setProgressBarIndeterminate(show);
    }
}
