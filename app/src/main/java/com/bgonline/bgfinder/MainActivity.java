package com.bgonline.bgfinder;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;

import android.util.Log;

import android.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SynchronizedLoadFragment.OnHeadlineSelectedListener,
        SummaryFragment.OnHeadlineSelectedListener,
        GamesFragment.OnHeadlineSelectedListener,
        TablesFragment.OnHeadlineSelectedListener,
        LogInFragment.OnHeadlineSelectedListener,
        SignUpFragment.OnHeadlineSelectedListener,
        ForgotPasswordFragment.OnHeadlineSelectedListener {

    // objects used in different windows
    private FirebaseUser connectedUser;
    private UserImage currentUserImage;
    private UserInfo userInfo;
    private ArrayList<String> arrayOfGames;
    private ArrayList<GameTable> arrayOfTables;

    // used to handle navigation
    private Activity mainActivity;
    private int currentLayoutId;
    private Fragment currentFragment;
    private long timeSinceLastFragmentChange;
    private LinkedList<Fragment> pendingChangeFragments;
    private boolean initialized;

    private DatabaseReference database;
    private ValueEventListener pendingInvitationsListener = null;

    private static final String TAG = "BGFinderMainActivity";

    // quit if pressed back twice
    boolean doubleBackToExitPressedOnce = false;

    // FireBase Connection
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialized = false;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivity = this;
        currentFragment = null;
        pendingChangeFragments = new LinkedList<Fragment>();

        connectedUser = null;
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                connectedUser = firebaseAuth.getCurrentUser();
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                Menu navMenu = navigationView.getMenu();

                NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
                View navHeader = navView.getHeaderView(0);

                RoundedImageView currentUserImageView = (RoundedImageView) navHeader.findViewById(R.id.currentUserImage);
                TextView currentUserEmail = (TextView) navHeader.findViewById(R.id.logged_user);
                TextView currentUserString = (TextView) navHeader.findViewById(R.id.logged_user_string);

                if (connectedUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + connectedUser.getUid());
                    navMenu.findItem(R.id.nav_login).setVisible(false);
                    navMenu.findItem(R.id.nav_logout).setVisible(true);

                    currentUserImage = new UserImage(connectedUser.getUid(),currentUserImageView,getApplicationContext());
                    currentUserEmail.setText(connectedUser.getEmail());
                    currentUserString.setText("User description should go here!");

                    if (!initialized) {
                        onChangeFragment(new SummaryFragment());
                    }
                    initialized = true;

                    database = FirebaseDatabase.getInstance().getReference();
                    pendingInvitationsListener = database.child("pendingInvitations").child(connectedUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot invitation : dataSnapshot.getChildren() ) {
                                final String tableId = invitation.getKey();
                                final String invitingUser = invitation.getValue().toString();

                                database.child("tables").child(tableId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final Dialog invitationDialog = new Dialog(MainActivity.this);
                                        invitationDialog.setContentView(R.layout.invitation);

                                        TextView userNameView = (TextView) invitationDialog.findViewById(R.id.invitation_sent_by);
                                        userNameView.setText(invitingUser);

                                        TextView tableNameView = (TextView) invitationDialog.findViewById(R.id.invitation_table_name);
                                        tableNameView.setText(dataSnapshot.child("name").getValue().toString());

                                        TextView gameNameView = (TextView) invitationDialog.findViewById(R.id.invitation_game_name);
                                        userNameView.setText(dataSnapshot.child("GameName").getValue().toString());

                                        TextView whereView = (TextView) invitationDialog.findViewById(R.id.invitation_where);
                                        tableNameView.setText(dataSnapshot.child("where").getValue().toString());

                                        TextView whenView = (TextView) invitationDialog.findViewById(R.id.invitation_when);
                                        tableNameView.setText(dataSnapshot.child("when").getValue().toString());

                                        Button acceptButton = (Button) invitationDialog.findViewById(R.id.invitation_accept_button);
                                        acceptButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Toast.makeText(getApplicationContext(), "You joined the table!", Toast.LENGTH_LONG);
                                                invitation.getRef().removeValue();
                                                invitationDialog.cancel();
                                            }
                                        });

                                        Button declineButton = (Button) invitationDialog.findViewById(R.id.invitation_decline_button);
                                        declineButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Toast.makeText(getApplicationContext(), "You declined the invitation :(", Toast.LENGTH_LONG);
                                                database.child("tablesForUsers").child(connectedUser.getUid()).child(tableId).removeValue();
                                                database.child("usersInTables").child(tableId).child(connectedUser.getUid()).removeValue();
                                                invitation.getRef().removeValue();
                                                invitationDialog.cancel();
                                            }
                                        });

                                        invitationDialog.show();
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

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    navMenu.findItem(R.id.nav_login).setVisible(true);
                    navMenu.findItem(R.id.nav_logout).setVisible(false);

                    currentUserImage = null;
                    currentUserImageView.setImageDrawable(getDrawable(R.drawable.meeples_pic));
                    currentUserEmail.setText(getString(R.string.unsigned_user_name));
                    currentUserString.setText(getString(R.string.unsigned_user_String));

                    onChangeFragment(new LogInFragment());

                    initialized = false;
                }
            }
        };

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        arrayOfGames = (new GamesFragment()).getArrayOfGames(getApplicationContext());
        arrayOfTables = (new TablesFragment()).getArrayOfTables(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                //super.onBackPressed();
                finish();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment newFragment = null;

        boolean doChangeFraction = true;
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_summary:
                newFragment = new SummaryFragment();
                break;
            case R.id.nav_account:
                newFragment = new AccountFragment();
                break;
            case R.id.nav_games:
                newFragment = new GamesFragment();
                break;
            case R.id.nav_tables:
                newFragment = new TablesFragment();
                break;
            case R.id.nav_search:
                newFragment = new SearchPlayersFragment();
                break;
            case R.id.nav_login:
                newFragment = new LogInFragment();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                doChangeFraction = false;
                break;
            case R.id.nav_new_table:
            case R.id.nav_friends:
            case R.id.nav_invite:
            case R.id.nav_messages:
            default:
                newFragment = null;
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (doChangeFraction) {
            onChangeFragment(newFragment);
        }

        return false;
    }

    private void showNotImplementedSnackbar() {
        FrameLayout fragmentContainer = (FrameLayout)findViewById(R.id.fragment_container);
        Snackbar mySnackbar = Snackbar.make(fragmentContainer, "Sorry, this feature is not yet implemented!" , 1500);
        mySnackbar.show();
    }

    @Override
    public void onSynchronizedFragmentLoad() {
        if (pendingChangeFragments.isEmpty()) {
            return;
        }

        Fragment newFragment = pendingChangeFragments.removeFirst();

        if (newFragment == null) {
            Log.e(TAG, "Trying to load NULL pending fragment.");
            // Automatically load next fragment if the current one failed.
            onSynchronizedFragmentLoad();
            return;
        }

        Bundle fragmentArgs = new Bundle();
        if (connectedUser != null) {
            fragmentArgs.putString("connectedUserId", connectedUser.getUid());
        } else {
            fragmentArgs.putString("connectedUserId", "");
        }
        fragmentArgs.putSerializable("currentUserImage", currentUserImage);
        fragmentArgs.putSerializable("arrayOfTables", arrayOfTables);
        fragmentArgs.putSerializable("arrayOfGames", arrayOfGames);
        newFragment.setArguments(fragmentArgs);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        currentFragment = newFragment;
        timeSinceLastFragmentChange = System.currentTimeMillis();
    }

    @Override
    public void onChangeFragment(Fragment newFragment) {
        if (System.currentTimeMillis() - timeSinceLastFragmentChange < 1000) {
            return;
        }

        if (connectedUser == null) {
            if (!newFragment.getClass().equals(LogInFragment.class) &&
                    !newFragment.getClass().equals(SignUpFragment.class) &&
                    !newFragment.getClass().equals(ForgotPasswordFragment.class))
            newFragment = new LogInFragment();
        }

        if (newFragment == null) {
            showNotImplementedSnackbar();
            return;
        }

        if (currentFragment != null && newFragment.getClass().equals(currentFragment.getClass())) {
            return;
        }

        Fragment toRemove = null;
        if (pendingChangeFragments.size() != 0) {
            toRemove = pendingChangeFragments.getLast();
        } else {
            toRemove = currentFragment;
        }
        pendingChangeFragments.add(newFragment);

        if (toRemove != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(toRemove);
            transaction.commit();
        } else {
            onSynchronizedFragmentLoad();
        }
        timeSinceLastFragmentChange = System.currentTimeMillis();
    }

    @Override
    public void onUpdateGamesArray(ArrayList<String> games) {
        arrayOfGames = games;
    }

    @Override
    public void onUpdateTablesArray(ArrayList<GameTable> tables) {
        arrayOfTables = tables;
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
