package com.atipls.chat.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.atipls.chat.R;
import com.atipls.chat.State;
import com.atipls.chat.model.Channel;
import com.atipls.chat.model.Guild;
import com.atipls.chat.model.Role;

import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONArray;

public class GuildListAdapter extends BaseExpandableListAdapter {
    private LayoutInflater mInflater;
    private Drawable defaultAvatar;

    private State s;
    private ArrayList<Guild> guilds;

    private static final ArrayList<Channel> EMPTY_CHANNELS = new ArrayList<Channel>();

    private static class ChildViewEntry {
        TextView name;
    }

    private static class GroupViewEntry {
        ImageView icon;
        TextView name;
    }

    public GuildListAdapter(Context context, State s, ArrayList<Guild> guilds) {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.defaultAvatar = context.getResources().getDrawable(R.drawable.ic_launcher);

        this.s = s;
        this.guilds = guilds;
    }

    private ArrayList<Channel> getChannelsFor(final Guild guild) {
        if (guild.channels != null)
            return guild.channels;

        s.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (guild.roles == null)
                        guild.roles = Role.parseRoles(JSON.getArray(s.http.get("/guilds/" + guild.id + "/roles")));
                    JSONArray channels = JSON.getArray(s.http.get("/guilds/" + guild.id + "/channels"));
                    guild.channels = Channel.parseChannels(s, guild, channels);

                    s.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return EMPTY_CHANNELS;
    }

    @Override
    public Object getChild(int position, int childPosition) {
        ArrayList<Channel> channels = getChannelsFor(guilds.get(position));
        return channels.get(childPosition);
    }

    @Override
    public long getChildId(int position, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int position, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewEntry tag = null;
        final Channel channel = (Channel) getChild(position, childPosition);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_list_item, null);
            tag = new ChildViewEntry();

            tag.name = (TextView) convertView.findViewById(R.id.channel_item_name);
            convertView.setTag(tag);
        }

        tag = (ChildViewEntry) convertView.getTag();

        tag.name.setText(channel.name);

        return convertView;
    }

    @Override
    public int getChildrenCount(int position) {
        ArrayList<Channel> channels = getChannelsFor(guilds.get(position));
        return channels.size();
    }

    @Override
    public Object getGroup(int position) {
        return guilds.get(position);
    }

    @Override
    public int getGroupCount() {
        return guilds.size();
    }

    @Override
    public long getGroupId(int position) {
        return position;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded,
            View convertView, ViewGroup parent) {
        GroupViewEntry tag = null;
        final Guild guild = (Guild) getGroup(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.guild_list_item, null);
            tag = new GroupViewEntry();

            tag.icon = (ImageView) convertView.findViewById(R.id.guild_item_icon);
            tag.name = (TextView) convertView.findViewById(R.id.guild_item_name);

            convertView.setTag(tag);
        }

        tag = (GroupViewEntry) convertView.getTag();

        s.icons.load(tag.icon, defaultAvatar, guild);
        tag.name.setText(guild.name);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int position, int childPosition) {
        return true;
    }

}