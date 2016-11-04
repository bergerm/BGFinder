package com.bgonline.bgfinder;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class ChatBubblesListAdapter extends ArrayAdapter<ChatBubble> {
    private String connectedUserId;

    public ChatBubblesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        connectedUserId = "";
    }

    public ChatBubblesListAdapter(Context context, int resource, List<ChatBubble> items) {
        super(context, resource, items);
        connectedUserId = "";
    }

    public void setConnectedUserId (String id) {
        connectedUserId = id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        ChatBubble bubble = getItem(position);

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            if (bubble.senderId.equals(connectedUserId)) {
                //v = vi.inflate(R.layout.chat_bubble_sent, null);
                v = vi.inflate(R.layout.chat_bubble_received, null);

                /*TextView userNameTextView = (TextView) v.findViewById(R.id.chat_bubble_user_name);
                userNameTextView.setVisibility(View.GONE);

                TextView messageTextView = (TextView)v.findViewById(R.id.chat_bubble_message);
                messageTextView.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.bubble2, null));
                messageTextView.setGravity(Gravity.RIGHT);
                messageTextView.setPadding(10,10,30,10);*/
            } else {
                v = vi.inflate(R.layout.chat_bubble_received, null);
            }
        }

        if (bubble != null) {
            TextView bubbleSender = (TextView) v.findViewById(R.id.chat_bubble_user_name);
            bubbleSender.setText(bubble.sender);

            TextView bubbleMessage = (TextView) v.findViewById(R.id.chat_bubble_message);
            bubbleMessage.setText(bubble.message);
        }

        return v;
    }

}