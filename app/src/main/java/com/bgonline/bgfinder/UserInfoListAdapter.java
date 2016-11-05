package com.bgonline.bgfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class UserInfoListAdapter extends ArrayAdapter<UserInfo> {

    public UserInfoListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public UserInfoListAdapter(Context context, int resource, List<UserInfo> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        UserInfo user = getItem(position);

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.show_user_summary, null);
        }

        if (user != null) {
            TextView userName = (TextView) v.findViewById(R.id.user_info_user_name);
            userName.setText(user.getUserName());

            TextView completeName = (TextView) v.findViewById(R.id.user_info_complete_name);
            completeName.setText(user.getFirstName() + " " + user.getLastName());

            TextView city = (TextView) v.findViewById(R.id.user_info_city);
            city.setText(user.getCity());

            TextView birthDate = (TextView) v.findViewById(R.id.user_info_birth_date);
            birthDate.setText(user.getBirthDate());

        }

        return v;
    }

}
