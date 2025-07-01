package com.atipls.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.atipls.chat.R;

public class Message extends LinearLayout implements OnClickListener {

    private ImageView mAvatar;
    private TextView mAuthor;
    private TextView mTimestamp;
    private TextView mContent;
    private Embed mEmbed;

    private ImageView mAttachmentImage;
    private VideoView mAttachmentVideo;

    private View mReply;
    private ImageView mReplyAvatar;
    private TextView mReplyAuthor;
    private TextView mReplyContent;

    public Message(Context context) {
        super(context);
    }

    public Message(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // ImageView mAvatar = (ImageView) findViewById(R.id.msg_avatar);
        // TextView mAuthor = (TextView) findViewById(R.id.msg_author);
        // TextView mTimestamp = (TextView) findViewById(R.id.msg_timestamp);
        // TextView mContent = (TextView) findViewById(R.id.msg_content);

        // View mReply = findViewById(R.id.msg_reply);
        // ImageView mReplyAvatar = (ImageView) findViewById(R.id.reply_avatar);
        // TextView mReplyAuthor = (TextView) findViewById(R.id.reply_author);
        // TextView mReplyContent = (TextView) findViewById(R.id.reply_content);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

}
