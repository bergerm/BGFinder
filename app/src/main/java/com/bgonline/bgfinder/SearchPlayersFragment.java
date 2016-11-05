package com.bgonline.bgfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchPlayersFragment extends SynchronizedLoadFragment{

    public static SearchPlayersFragment newInstance() {
        return new SearchPlayersFragment();
    }

    public SearchPlayersFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View searchView = inflater.inflate(R.layout.search_players, container, false);

        final EditText userNameField = (EditText) searchView.findViewById(R.id.search_name);
        final EditText gameNameField = (EditText) searchView.findViewById(R.id.search_game);
        final EditText cityField = (EditText) searchView.findViewById(R.id.search_city);

        Button searchButton = (Button) searchView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UsersListActivity.class);
                intent.putExtra("CONNECTED_USER_ID", getArguments().getString("connectedUserId"));
                intent.putExtra("SEARCH_USER_NAME", userNameField.getText().toString());
                intent.putExtra("SEARCH_USER_CITY", cityField.getText().toString());
                intent.putExtra("SEARCH_USER_GAME", gameNameField.getText().toString());
                startActivity(intent);
            }
        });

        return searchView;
    }
}
