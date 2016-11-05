package com.bgonline.bgfinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ShowUserInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final UserInfo userInfo = (UserInfo) getIntent().getExtras().getSerializable("USER");

        View userInfoView = getLayoutInflater().inflate(R.layout.show_user_info, null);

        TextView email = (TextView)userInfoView.findViewById(R.id.show_user_info_email);
        email.setText(userInfo.getEmail());
        TextView description = (TextView)userInfoView.findViewById(R.id.show_user_info_description);
        description.setText(userInfo.getDescription());
        TextView firstName = (TextView) userInfoView.findViewById(R.id.show_user_info_first_name);
        firstName.setText(userInfo.getFirstName());
        TextView lastName = (TextView) userInfoView.findViewById(R.id.show_user_info_last_name);
        lastName.setText(userInfo.getLastName());
        TextView city = (TextView) userInfoView.findViewById(R.id.show_city);
        city.setText(userInfo.getCity());
        TextView birthDate = (TextView) userInfoView.findViewById(R.id.show_birth_date);
        birthDate.setText(userInfo.getBirthDate());
        TextView bggUser = (TextView) userInfoView.findViewById(R.id.show_bgg_account);
        bggUser.setText(userInfo.getBggAccount());

        ImageView image = (ImageView) userInfoView.findViewById(R.id.show_user_info_image);
        final UserImage userImage = new UserImage(userInfo.getUserId(), image, ShowUserInfoActivity.this);

        ImageButton bggButton = (ImageButton) userInfoView.findViewById(R.id.show_bgg_button);
        bggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bggUser = userInfo.getBggAccount();
                if (!bggUser.isEmpty()) {
                    String url = "https://www.boardgamegeek.com/user/" + bggUser;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            }
        });

        Button showGamesButton = (Button) userInfoView.findViewById(R.id.show_games_list_button);
        showGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowUserInfoActivity.this, ShowUserGamesActivity.class);
                intent.putExtra("USERID", userInfo.getUserId());
                startActivity(intent);
            }
        });

        setContentView(userInfoView);
    }
}
