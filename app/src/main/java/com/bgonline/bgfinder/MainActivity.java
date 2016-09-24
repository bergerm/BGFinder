package com.bgonline.bgfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bgonline.bgfinder.databinding.UserInfoBinding;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        LinearLayout l = (LinearLayout) findViewById(R.id.lay);

        // This will show the user info
        /*userInfo = new UserInfo();
        populateUserInfo(userInfo);

        LayoutInflater inflater = LayoutInflater.from(l.getContext());
        UserInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.user_info, null, true);
        binding.setUserInfo(userInfo);
        l.addView(binding.getRoot());*/

        // This will show the games list
        //setContentView(R.layout.games_list);

        // This will show the tables
        LayoutInflater inflater = LayoutInflater.from(l.getContext());
        View tablesListView = inflater.inflate(R.layout.tables_list, null);
        ListView tablesList = (ListView)tablesListView.findViewById(R.id.tables);
        tablesList.setAdapter(new TablesListAdapter(l.getContext(), R.layout.game_table));
        View newTable = inflater.inflate(R.layout.game_table, null);
        tablesList.addFooterView(newTable);
        View newTable2 = inflater.inflate(R.layout.game_table, null);
        tablesList.addFooterView(newTable2);
        l.addView(tablesListView);
    }

    public void populateUserInfo(UserInfo userInfo) {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        userInfo.setUserId(sharedPref.getInt(getResources().getString(R.string.user_info_id), 0));

        if (userInfo.getUserId() == 213) {
            userInfo.setBirthDate("");
            userInfo.setBggAccount("");
            userInfo.setCity("");
            userInfo.setCountry("Israel");
            userInfo.setDescription("Please log in!");
            userInfo.setFirstName("");
            userInfo.setLastName("");
            userInfo.setEmail("You are not logged in");
        } else {
            userInfo.setFirstName(sharedPref.getString(getResources().getString(R.string.user_info_first_name), ""));
            userInfo.setLastName(sharedPref.getString(getResources().getString(R.string.user_info_last_name), ""));
            userInfo.setEmail(sharedPref.getString(getResources().getString(R.string.user_info_email), ""));
            userInfo.setBggAccount(sharedPref.getString(getResources().getString(R.string.user_info_bgg_account), ""));
            userInfo.setDescription(sharedPref.getString(getResources().getString(R.string.user_info_description), ""));
            userInfo.setCountry(sharedPref.getString(getResources().getString(R.string.user_info_country), ""));
            userInfo.setCity(sharedPref.getString(getResources().getString(R.string.user_info_city), ""));
            userInfo.setBirthDate(sharedPref.getString(getResources().getString(R.string.user_info_year_of_birth), ""));
        }
    }

    public void saveUserInfo(UserInfo userInfo) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(getResources().getString(R.string.user_info_id), userInfo.getUserId());
        editor.putString(getResources().getString(R.string.user_info_first_name), userInfo.getFirstName());
        editor.putString(getResources().getString(R.string.user_info_last_name), userInfo.getLastName());
        editor.putString(getResources().getString(R.string.user_info_email), userInfo.getEmail());
        editor.putString(getResources().getString(R.string.user_info_bgg_account), userInfo.getBggAccount());
        editor.putString(getResources().getString(R.string.user_info_description), userInfo.getDescription());
        editor.putString(getResources().getString(R.string.user_info_country), userInfo.getCountry());
        editor.putString(getResources().getString(R.string.user_info_city), userInfo.getCity());
        editor.putString(getResources().getString(R.string.user_info_year_of_birth), userInfo.getBirthDate());

        editor.commit();
    }

    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*// Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);*/ return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        saveUserInfo(userInfo);
        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }
}
