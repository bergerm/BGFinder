package com.bgonline.bgfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

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

        GameTable p = getItem(position);

        if (p != null) {
            /*CheckBox cb1 = (CheckBox) v.findViewById(R.id.checkBox);
            CheckBox cb2 = (CheckBox) v.findViewById(R.id.checkBox2);

            if (cb1 != null) {
                cb1.setChecked(true);
            }

            if (cb2 != null) {
                cb2.setChecked(false);
            }*/
        }

        return v;
    }

}