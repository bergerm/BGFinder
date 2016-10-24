package com.bgonline.bgfinder;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.os.Handler;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;

import java.util.ArrayList;
import java.util.LinkedList;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;

import android.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SyncrhonizedLoadFragment.OnHeadlineSelectedListener,
        SummaryFragment.OnHeadlineSelectedListener,
        GamesFragment.OnHeadlineSelectedListener,
        TablesFragment.OnHeadlineSelectedListener {

    // objects used in different windows
    private UserInfo userInfo;
    private ArrayList<String> arrayOfGames;
    private ArrayList<GameTable> arrayOfTables;

    // used to handle navigation
    private Activity mainActivity;
    private int currentLayoutId;
    Fragment currentFragment;
    LinkedList<Fragment> pendingChangeFragments;

    private static final String TAG = "BGFinderMainActivity";

    // quit if pressed back twice
    boolean doubleBackToExitPressedOnce = false;

    // FireBase Connection
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivity = this;
        currentFragment = null;
        pendingChangeFragments = new LinkedList<Fragment>();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mAuth.signInWithEmailAndPassword("manueldavidb@mail.afeka.ac.il", "qwerty")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

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
        onChangeFragment(new SummaryFragment());
    }

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment newFragment;

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
            case R.id.nav_new_table:
            case R.id.nav_friends:
            case R.id.nav_login:
            case R.id.nav_logout:
            case R.id.nav_invite:
            case R.id.nav_messages:
            default:
                newFragment = null;
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        onChangeFragment(newFragment);

        return true;
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
        fragmentArgs.putSerializable("arrayOfTables", arrayOfTables);
        fragmentArgs.putSerializable("arrayOfGames", arrayOfGames);
        newFragment.setArguments(fragmentArgs);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        currentFragment = newFragment;
    }

    @Override
    public void onChangeFragment(Fragment newFragment) {
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
}
