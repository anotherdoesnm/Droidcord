package com.atipls.chat.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atipls.chat.R;
import com.atipls.chat.State;
import com.atipls.chat.data.Messages;
import com.atipls.chat.model.Message;

public class MessageListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Drawable defaultAvatar;
    private State s;
    private Messages messages;
    private int iconSize;

    public MessageListAdapter(Context context, State s, Messages messages) {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.defaultAvatar = context.getResources().getDrawable(R.drawable.ic_launcher);
        this.s = s;
        this.messages = messages;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24 + 0.5f, metrics);
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entry tag;
        Message message = (Message) getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message, null);
            tag = new Entry(convertView);
            convertView.setTag(tag);
        }

        tag = (Entry) convertView.getTag();

        s.icons.load(tag.avatar, defaultAvatar, message.author);
        s.guildInformation.load(tag.author, message.author);
        tag.timestamp.setText(message.timestamp);
        tag.content.setText(message.content);

        if (!message.showAuthor && message.recipient == null) {
            tag.metadata.setVisibility(View.GONE);
            tag.avatar.getLayoutParams().height = 0;
        } else {
            tag.metadata.setVisibility(View.VISIBLE);
            tag.avatar.getLayoutParams().height = iconSize;
        }

        if (message.recipient != null) {
            tag.reply.setVisibility(View.VISIBLE);
            s.icons.load(tag.replyAvatar, defaultAvatar, message.recipient);
            s.guildInformation.load(tag.replyAuthor, message.recipient);
            tag.replyContent.setText(message.refContent);
        } else {
            tag.reply.setVisibility(View.GONE);
        }

        if (message.isStatus) {
            tag.message.setVisibility(View.GONE);
            tag.status.setVisibility(View.VISIBLE);

            SpannableStringBuilder sb = new SpannableStringBuilder(
                    message.author.name + " " + message.content);
            sb.setSpan(new StyleSpan(Typeface.BOLD), 0,
                    message.author.name.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            tag.statusText.setText(sb);
            tag.statusTimestamp.setText(message.timestamp);
        } else {
            tag.message.setVisibility(View.VISIBLE);
            tag.status.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private static class Entry {
        View message;
        TextView author;
        TextView timestamp;
        TextView content;
        ImageView avatar;
        View metadata;
        View reply;
        TextView replyAuthor;
        TextView replyContent;
        ImageView replyAvatar;
        View status;
        TextView statusText;
        TextView statusTimestamp;

        public Entry(View view) {
            message = view.findViewById(R.id.message);
            author = (TextView) view.findViewById(R.id.msg_author);
            timestamp = (TextView) view.findViewById(R.id.msg_timestamp);
            content = (TextView) view.findViewById(R.id.msg_content);
            avatar = (ImageView) view.findViewById(R.id.msg_avatar);
            metadata = view.findViewById(R.id.msg_metadata);
            reply = view.findViewById(R.id.msg_reply);
            replyAuthor = (TextView) view.findViewById(R.id.reply_author);
            replyContent = (TextView) view.findViewById(R.id.reply_content);
            replyAvatar = (ImageView) view.findViewById(R.id.reply_avatar);
            status = view.findViewById(R.id.status);
            statusText = (TextView) view.findViewById(R.id.status_text);
            statusTimestamp = (TextView) view
                    .findViewById(R.id.status_timestamp);
        }
    }
}