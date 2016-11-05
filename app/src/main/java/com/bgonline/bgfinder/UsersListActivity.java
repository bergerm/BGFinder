package com.bgonline.bgfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class UsersListActivity extends AppCompatActivity {
    DatabaseReference database;
    String connectedUserId;
    ArrayList<UserInfo> arrayOfUsers;
    UserInfoListAdapter userInfoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        connectedUserId = getIntent().getExtras().getString("CONNECTED_USER_ID");
        final String searchUserName = getIntent().getExtras().getString("SEARCH_USER_NAME");
        final String searchUserCity = getIntent().getExtras().getString("SEARCH_USER_CITY");
        final String searchUserGame = getIntent().getExtras().getString("SEARCH_USER_GAME");

        arrayOfUsers = new ArrayList<UserInfo>();
        userInfoListAdapter = new UserInfoListAdapter(this, R.layout.show_user_summary, arrayOfUsers);

        LinearLayout searchResultLayout = new LinearLayout(this);
        ListView listView = new ListView(this);
        listView.setAdapter(userInfoListAdapter);

        /*listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start user info activity here
            }
        });*/

        searchResultLayout.addView(listView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        setContentView(searchResultLayout);

        arrayOfUsers.clear();
        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot user : dataSnapshot.getChildren()) {
                    final String userId = user.getKey();
                    if (userId.equals(connectedUserId)) {
                        continue;
                    }

                    database.child("games").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean addToArray = false;
                            String userName = user.child("userName").getValue().toString();
                            String userCity = user.child("city").getValue().toString();

                            if (!searchUserName.isEmpty() && userName.toLowerCase().contains(searchUserName)) {
                                addToArray = true;
                            }

                            if (!addToArray && !searchUserCity.isEmpty() && userCity.toLowerCase().contains(searchUserCity)) {
                                addToArray = true;
                            }

                            if (!addToArray && !searchUserGame.isEmpty()) {
                                for (DataSnapshot game : dataSnapshot.getChildren()) {
                                    if (game.getValue().toString().toLowerCase().contains(searchUserGame)) {
                                        addToArray = true;
                                        break;
                                    }
                                }
                            }

                            if (addToArray) {
                                UserInfo userInfo = new UserInfo();
                                userInfo.setEmail(user.child("email").getValue().toString());
                                userInfo.setFirstName(user.child("firstName").getValue().toString());
                                userInfo.setBirthDate(user.child("birthDate").getValue().toString());
                                userInfo.setBggAccount(user.child("bggUserName").getValue().toString());
                                userInfo.setCountry(user.child("country").getValue().toString());
                                userInfo.setCity(user.child("city").getValue().toString());
                                userInfo.setDescription(user.child("description").getValue().toString());
                                userInfo.setUserId(user.getKey());
                                userInfo.setLastName(user.child("lastName").getValue().toString());
                                userInfo.setUserName(user.child("userName").getValue().toString());
                                userInfoListAdapter.add(userInfo);
                                userInfoListAdapter.notifyDataSetChanged();
                            }

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
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
