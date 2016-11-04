package com.bgonline.bgfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ChatActivity extends AppCompatActivity {
    DatabaseReference database;
    String connectedUserId;
    String tableId;
    ArrayList<ChatBubble> chatArray;
    ChatBubblesListAdapter chatAdapter;
    ValueEventListener chatListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        connectedUserId = getIntent().getExtras().getString("CONNECTED_USER_ID");
        tableId = getIntent().getExtras().getString("TABLE_ID");

        chatArray = new ArrayList<>();

        setContentView(R.layout.chat_layout);
        chatAdapter = new ChatBubblesListAdapter(getApplicationContext(), R.layout.chat_bubble_received, chatArray);
        chatAdapter.setConnectedUserId(connectedUserId);

        ListView listView = (ListView) findViewById(R.id.chat_list_view);
        listView.setAdapter(chatAdapter);

        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatAdapter.clear();
                java.lang.Iterable<DataSnapshot> messages = dataSnapshot.getChildren();
                for(DataSnapshot messageContainer : messages) {
                    Iterator<DataSnapshot> it = messageContainer.getChildren().iterator();
                    DataSnapshot message = it.next();
                    final String senderId = message.getKey();
                    final String messageString = message.getValue().toString();

                    database.child("users").child(senderId).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ChatBubble bubble = new ChatBubble();
                            bubble.senderId = senderId;
                            bubble.sender = dataSnapshot.getValue().toString();
                            bubble.message = messageString;
                            chatAdapter.add(bubble);
                            chatAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        database.child("tableChat").child(tableId).addValueEventListener(chatListener);

        final EditText messageTextField = (EditText) findViewById(R.id.chat_text);
        ImageButton sendMessageButton = (ImageButton) findViewById(R.id.chat_send);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToSend = messageTextField.getText().toString();
                if (!textToSend.equals("")) {
                    database.child("tableChat").child(tableId).push().child(connectedUserId).setValue(textToSend);
                    messageTextField.setText("");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.removeEventListener(chatListener);
    }
}
