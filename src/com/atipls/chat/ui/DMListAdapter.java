package com.atipls.chat.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.atipls.chat.R;
import com.atipls.chat.State;
import com.atipls.chat.model.DirectMessage;

public class DMListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Drawable defaultAvatar;

    private State s;
    private ArrayList<DirectMessage> dms;

    static class Entry {
        ImageView icon;
        TextView name;
    }

    public DMListAdapter(Context context, State s, ArrayList<DirectMessage> dms) {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.defaultAvatar = context.getResources().getDrawable(R.drawable.ic_launcher);

        this.s = s;
        this.dms = dms;
    }

    @Override
    public Object getItem(int position) {
        return dms.get(position);
    }

    @Override
    public int getCount() {
        return dms.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DirectMessage dm = (DirectMessage) getItem(position);

        Entry tag = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dm_list_item, null);
            tag = new Entry();

            tag.icon = (ImageView) convertView.findViewById(R.id.dm_item_icon);
            tag.name = (TextView) convertView.findViewById(R.id.dm_item_name);

            convertView.setTag(tag);
        }

        tag = (Entry) convertView.getTag();
        
        s.icons.load(tag.icon, defaultAvatar, dm);
        tag.name.setText(dm.name);
        
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}