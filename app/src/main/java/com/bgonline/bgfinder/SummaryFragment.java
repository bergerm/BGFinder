package com.bgonline.bgfinder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SummaryFragment extends SyncrhonizedLoadFragment {

    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onChangeFragment(Fragment newFragment);
    }

    private ArrayList<GameTable> arrayOfTables;
    private ArrayList<String> arrayOfGames;

    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    public SummaryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View summaryView =  inflater.inflate(R.layout.summary, container, false);

        // This should return the FrameLayout that is the Fragment container.
        final FrameLayout mainLayout = (FrameLayout)container.getRootView();

        // should have correct arrays here
        Bundle fragmentArgs = getArguments();
        arrayOfTables = (ArrayList<GameTable>)fragmentArgs.getSerializable("arrayOfTables");
        arrayOfGames = (ArrayList<String>)fragmentArgs.getSerializable("arrayOfGames");

        CardView tablesCard = (CardView) summaryView.findViewById(R.id.summary_tables_card);
        tablesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangeFragment(new TablesFragment());
            }
        });

        TextView tablesSummary = (TextView) summaryView.findViewById(R.id.summary_tables_card_message);
        String tableString = "You are participating in " + arrayOfTables.size() + " tables";
        tablesSummary.setText(tableString);

        if (arrayOfTables.size() > 0) {
            GameTable table = arrayOfTables.get(0);
            TextView nextTableName = (TextView) summaryView.findViewById(R.id.summary_tables_card_next_game_name);
            nextTableName.setText(table.getTableName());

            TextView nextGameName = (TextView) summaryView.findViewById(R.id.summary_tables_card_next_game);
            nextGameName.setText(table.getGameName());

            TextView nextTableWhen = (TextView) summaryView.findViewById(R.id.summary_tables_card_next_game_when);
            nextTableWhen.setText(table.getDate());

            TextView nextTableWhere = (TextView) summaryView.findViewById(R.id.summary_tables_card_next_game_where);
            nextTableWhere.setText(table.getLocation());
        }

        CardView gamesCard = (CardView) summaryView.findViewById(R.id.summary_games_card);
        gamesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangeFragment(new GamesFragment());
            }
        });

        TextView gamesSummary = (TextView) summaryView.findViewById(R.id.summary_games_card_text);
        String gamesString = "You've got " + arrayOfGames.size() + " games";
        gamesSummary.setText(gamesString);

        CardView friendsCard = (CardView) summaryView.findViewById(R.id.summary_friends_card);
        friendsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangeFragment(null);
            }
        });

        TextView friendsSummary = (TextView) summaryView.findViewById(R.id.summary_friends_card_text);
        String friendsString = "You've got 0 friends";
        friendsSummary.setText(friendsString);

        CardView messagesCard = (CardView) summaryView.findViewById(R.id.summary_messages_card);
        messagesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangeFragment(null);
            }
        });

        TextView messagesSummary = (TextView) summaryView.findViewById(R.id.summary_messages_card_message);
        String messagesString = "You've got 0 messages";
        friendsSummary.setText(friendsString);


        return summaryView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
