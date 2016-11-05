package com.bgonline.bgfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class ShowUserGamesActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayOfGames;
    DatabaseReference database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arrayOfGames = new ArrayList<String>();
        database = FirebaseDatabase.getInstance().getReference();

        LinearLayout layout = new LinearLayout(this);
        ListView list = new ListView(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , arrayOfGames);
        list.setAdapter(adapter);
        layout.addView(list);

        String userId = getIntent().getExtras().getString("USERID");

        database.child("games").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot game : dataSnapshot.getChildren()) {
                    adapter.add(game.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setContentView(layout);
    }
}
