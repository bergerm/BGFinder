package com.bgonline.bgfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.app.AlertDialog;
import android.text.InputType;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.bgonline.bgfinder.databinding.NewTableBinding;
import com.bgonline.bgfinder.databinding.UserInfoBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // objects used in different windows
    private UserInfo userInfo;
    private ArrayList<String> arrayOfGames;
    private ArrayList<GameTable> arrayOfTables;
    private TablesListAdapter tablesListAdapter;

    // used to handle navigation
    private static boolean isWindowChanged;
    private Activity mainActivity;
    private int currentLayoutId;

    // quit if pressed back twice
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isWindowChanged = false;

        mainActivity = this;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        LinearLayout l = (LinearLayout) findViewById(R.id.lay);
        final LayoutInflater inflater = LayoutInflater.from(l.getContext());
        handleNavigateTables(l, inflater);
        handleNavigateGames(l, inflater);
        handleNavigateSummary(l, inflater);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                handleLeaveView();
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    private void handleNavigateSummary(LinearLayout l, final LayoutInflater inflater) {
        l.removeAllViews();
        View summaryView = inflater.inflate(R.layout.summary, null);
        l.addView(summaryView);

        final LinearLayout mainLayout = l;
        CardView tablesCard = (CardView) findViewById(R.id.summary_tables_card);
        tablesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNavigateTables(mainLayout, inflater);
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

        CardView gamesCard = (CardView) findViewById(R.id.summary_games_card);
        gamesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNavigateGames(mainLayout, inflater);
            }
        });

        TextView gamesSummary = (TextView) summaryView.findViewById(R.id.summary_games_card_text);
        String gamesString = "You've got " + arrayOfGames.size() + " games";
        gamesSummary.setText(gamesString);

        CardView friendsCard = (CardView) findViewById(R.id.summary_friends_card);
        friendsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNavigateNotImplemented(mainLayout, inflater);
            }
        });

        TextView friendsSummary = (TextView) summaryView.findViewById(R.id.summary_friends_card_text);
        String friendsString = "You've got 0 friends";
        friendsSummary.setText(friendsString);

        CardView messagesCard = (CardView) findViewById(R.id.summary_messages_card);
        messagesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNavigateNotImplemented(mainLayout, inflater);
            }
        });

        TextView messagesSummary = (TextView) summaryView.findViewById(R.id.summary_messages_card_message);
        String messagesString = "You've got 0 messages";
        friendsSummary.setText(friendsString);


        currentLayoutId = R.layout.summary;
    }

    public void handleLeaveSummary() {
        return;
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

    private void handleNavigateAccount(LinearLayout l, LayoutInflater inflater) {
        l.removeAllViews();
        userInfo = new UserInfo();
        populateUserInfo(userInfo);
        UserInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.user_info, null, true);
        binding.setUserInfo(userInfo);
        l.addView(binding.getRoot());
        currentLayoutId = R.layout.user_info;
    }

    public void handleLeaveAccount() {
        saveUserInfo(userInfo);
        userInfo = null;
    }

    private void handleNavigateGames(LinearLayout l, LayoutInflater inflater) {
        l.removeAllViews();

        String json = "";
        try {
            InputStream inputStream = getApplicationContext().openFileInput("arrayOfGames.json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                json = stringBuilder.toString();
            }
        }
        catch (Exception e) {
            // LOG here
        }

        if (!json.equals("null") && !json.equals("")) {
            Gson gson = new Gson();
            arrayOfGames = gson.fromJson(json, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        else {
            arrayOfGames = new ArrayList<String>();
        }

        View gameListView = inflater.inflate(R.layout.games_list, null);
        l.addView(gameListView);

        ListView list = (ListView) findViewById(R.id.list_of_games);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(l.getContext(), android.R.layout.simple_list_item_1 , arrayOfGames);
        list.setAdapter(adapter);

        Button addGameButton = (Button) gameListView.findViewById(R.id.game_list_add_game);
        addGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add new game!");

                // Set up the input
                final EditText input = new EditText(getApplicationContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayOfGames.add(input.getText().toString());
                        Collections.sort(arrayOfGames, new Comparator<String>() {
                            @Override
                            public int compare(String s1, String s2) {
                                return s1.compareToIgnoreCase(s2);
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                try {
                    builder.show();
                }
                catch (Exception e) {
                    //LOG ERROR
                }
            }
        });

        final LinearLayout mainLayout = l;
        Button importGamesButton = (Button) gameListView.findViewById(R.id.game_list_import_games);
        importGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNavigateNotImplemented(mainLayout,null);
            }
        });

        ListView listView = (ListView) gameListView.findViewById(R.id.list_of_games);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                final int itemId = (int) pos;
                String gameName = arrayOfGames.get((int)itemId);
                String dialogString = "Remove " + gameName + "?";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(dialogString);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayOfGames.remove(itemId);
                        Collections.sort(arrayOfGames, new Comparator<String>() {
                            @Override
                            public int compare(String s1, String s2) {
                                return s1.compareToIgnoreCase(s2);
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
        });

        currentLayoutId = R.layout.games_list;
    }

    public void handleLeaveGames() {
        Gson gson = new Gson();
        String json = gson.toJson(arrayOfGames);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("arrayOfGames.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            // Log here
        }
    }

    private void handleNavigateTables(LinearLayout l, LayoutInflater inflater) {
        l.removeAllViews();

        String json = "";
        try {
            InputStream inputStream = getApplicationContext().openFileInput("arrayOfTables.json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                json = stringBuilder.toString();
            }
        }
        catch (Exception e) {
            // LOG here
        }

        if (!json.equals("null") && !json.equals("")) {
            Gson gson = new Gson();
            arrayOfTables = gson.fromJson(json, new TypeToken<ArrayList<GameTable>>() {
            }.getType());
        }
        else {
            arrayOfTables = new ArrayList<GameTable>();
        }

        View tablesListView = inflater.inflate(R.layout.tables_list, null);
        ListView tablesList = (ListView)tablesListView.findViewById(R.id.tables);
        tablesListAdapter = new TablesListAdapter(l.getContext(), R.layout.game_table, arrayOfTables);
        tablesList.setAdapter(tablesListAdapter);

        l.addView(tablesListView);

        Button newTableButton = (Button) tablesListView.findViewById(R.id.new_table_button);
        newTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewTableActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        tablesList.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                final int itemId = (int) pos;
                String tableName = arrayOfTables.get((int)itemId).getTableName();
                String dialogString = "Remove " + tableName + "?";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(dialogString);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayOfTables.remove(itemId);
                        tablesListAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
        });

        currentLayoutId = R.layout.tables_list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        String result = data.getStringExtra("RESULT");
        if (!result.equals("SUCCESS")) {
            return;
        }

        String newTableJson=data.getStringExtra("NEW_TABLE");
        if (!newTableJson.equals("null") && !newTableJson.equals("")) {
            Gson gson = new Gson();
            GameTable newTable = gson.fromJson(newTableJson, GameTable.class);
            arrayOfTables.add(newTable);
            tablesListAdapter.notifyDataSetChanged();
        }
    }


    public void handleLeaveTables() {
        Gson gson = new Gson();
        String json = gson.toJson(arrayOfTables);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("arrayOfTables.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            // Log here
        }
    }

    private void handleNavigateNewTable(LinearLayout l, LayoutInflater inflater) {
        /*l.removeAllViews();
        GameTable newTable = new GameTable(mainActivity.getApplicationContext(), 0);
        NewTableBinding binding = DataBindingUtil.inflate(inflater, R.layout.new_table, null, true);
        binding.setGameTable(newTable);
        l.addView(binding.getRoot());
        currentLayoutId = R.layout.new_table;*/
        handleNavigateTables(l, inflater);
        Button newTableButton = (Button) findViewById(R.id.new_table_button);
        newTableButton.callOnClick();
    }

    public void handleLeaveNewTable() {
        return;
    }

    private void handleNavigateNotImplemented(LinearLayout l, LayoutInflater inflater) {
        Snackbar mySnackbar = Snackbar.make(l, "Sorry, this feature is not yet implemented!" , 1500);
        mySnackbar.show();
    }

    private void handleLeaveView() {
        // Handle leaving from current layout
        switch (currentLayoutId) {
            case R.layout.summary:
                handleLeaveSummary();
                break;
            case R.layout.user_info:
                handleLeaveAccount();
                break;
            case R.layout.games_list:
                handleLeaveGames();
                break;
            case R.layout.tables_list:
                handleLeaveTables();
                break;
            case R.layout.new_table:
                handleLeaveNewTable();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        LinearLayout l = (LinearLayout) findViewById(R.id.lay);
        final LayoutInflater inflater = LayoutInflater.from(l.getContext());

        handleLeaveView();

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //saveUserInfo(userInfo);
        switch (id) {
            case R.id.nav_summary:
                handleNavigateSummary(l, inflater);
                break;
            case R.id.nav_account:
                handleNavigateAccount(l, inflater);
                break;
            case R.id.nav_games:
                handleNavigateGames(l, inflater);
                break;
            case R.id.nav_tables:
                handleNavigateTables(l, inflater);
                break;
            case R.id.nav_new_table:
                handleNavigateNewTable(l, inflater);
                break;
            case R.id.nav_friends:
            case R.id.nav_login:
            case R.id.nav_logout:
            case R.id.nav_invite:
            case R.id.nav_messages:
            default:
                handleNavigateNotImplemented(l, inflater);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
