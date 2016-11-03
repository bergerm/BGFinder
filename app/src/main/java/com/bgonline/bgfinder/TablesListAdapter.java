package com.bgonline.bgfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class TablesListAdapter extends ArrayAdapter<GameTable> {

    public TablesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public TablesListAdapter(Context context, int resource, List<GameTable> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.game_table, null);
        }

        GameTable table = getItem(position);

        if (table != null) {
            TextView tableNameView = (TextView) v.findViewById(R.id.game_table_name);
            tableNameView.setText(table.getTableName());

            TextView tableGame = (TextView) v.findViewById(R.id.game_table_game_name);
            tableGame.setText(table.getGameName());

            TextView tablePlayer1 = (TextView) v.findViewById(R.id.game_table_player1);
            tablePlayer1.setText(table.getPlayer1UserName());

            TextView tablePlayer2 = (TextView) v.findViewById(R.id.game_table_player2);
            tablePlayer2.setText(table.getPlayer2UserName());

            TextView tablePlayer3 = (TextView) v.findViewById(R.id.game_table_player3);
            tablePlayer3.setText(table.getPlayer3UserName());

            TextView tablePlayer4 = (TextView) v.findViewById(R.id.game_table_player4);
            tablePlayer4.setText(table.getPlayer4UserName());

            /*TextView tablePlayer5 = (TextView) v.findViewById(R.id.game_table_player5);
            tablePlayer5.setText(table.getPlayer5());

            TextView tablePlayer6 = (TextView) v.findViewById(R.id.game_table_player6);
            tablePlayer6.setText(table.getPlayer6());*/

            TextView tableWhere= (TextView) v.findViewById(R.id.game_table_where);
            tableWhere.setText(table.getLocation());

            TextView tableWhen= (TextView) v.findViewById(R.id.game_table_when);
            tableWhen.setText(table.getDate());

            LinearLayout player1View = (LinearLayout) v.findViewById(R.id.player1);
            if (table.getPlayer1() != "") {
                player1View.setVisibility(View.VISIBLE);
                RoundedImageView imageView1 = (RoundedImageView) v.findViewById(R.id.game_table_player1_image);
                UserImage newImage = new UserImage(table.getPlayer1(), imageView1, getContext());
            }
            else {
                player1View.setVisibility(View.INVISIBLE);
            }

            LinearLayout player2View = (LinearLayout) v.findViewById(R.id.player2);
            if (table.getPlayer2() != "") {
                player2View.setVisibility(View.VISIBLE);
                RoundedImageView imageView2 = (RoundedImageView) v.findViewById(R.id.game_table_player2_image);
                UserImage newImage = new UserImage(table.getPlayer2(), imageView2, getContext());
            }
            else {
                player2View.setVisibility(View.INVISIBLE);
            }

            LinearLayout player3View = (LinearLayout) v.findViewById(R.id.player3);
            if (table.getPlayer3() != "") {
                player3View.setVisibility(View.VISIBLE);
                RoundedImageView imageView3 = (RoundedImageView) v.findViewById(R.id.game_table_player3_image);
                UserImage newImage = new UserImage(table.getPlayer3(), imageView3, getContext());
            }
            else {
                player3View.setVisibility(View.INVISIBLE);
            }

            LinearLayout player4View = (LinearLayout) v.findViewById(R.id.player4);
            if (table.getPlayer4() != "") {
                player4View.setVisibility(View.VISIBLE);
                RoundedImageView imageView4 = (RoundedImageView) v.findViewById(R.id.game_table_player4_image);
                UserImage newImage = new UserImage(table.getPlayer4(), imageView4, getContext());
            }
            else {
                player4View.setVisibility(View.INVISIBLE);
            }
        }

        return v;
    }

}