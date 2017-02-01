package org.vai.vari.bsiandroid;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Sample application demonstrating how to connect to the network and fetch raw
 * HTML. It uses AsyncTask to do the fetch on a background thread. To establish
 * the network connection, it uses HttpURLConnection.
 *
 * This sample uses the logging framework to display log output in the log
 * fragment (LogFragment).
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BsiConnector.OnLoginListener {

    static final int LOGIN_REQUEST = 1;

    DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private ReqTasksPagerFragment mReqTasksPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(this);
        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.app_title);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                myToolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );


        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mReqTasksPagerFragment = new ReqTasksPagerFragment();

        BsiConnector.getInstance().mCallback = this;
        if (BsiConnector.getInstance().SessionID.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            BsiConnector.getInstance().UpdateUI();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.username_header || v.getId() == R.id.database_header) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            mDrawerLayout.closeDrawer(mNavigationView);
            getSupportFragmentManager().beginTransaction().remove(mReqTasksPagerFragment).commit();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // update the main content by replacing fragments
        switch (item.getItemId()) {
            case R.id.menu_req_tasks:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mReqTasksPagerFragment).commit();
                break;
            case R.id.menu_change_password:
                Intent intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
        }


        mDrawerLayout.closeDrawer(mNavigationView);
        return true;
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void OnLogin(String username, String database, String sessionID) {
        TextView usernameView = (TextView)mNavigationView.getHeaderView(0).findViewById(R.id.username_header);
        usernameView.setText(username);
        TextView databaseView = (TextView)mNavigationView.getHeaderView(0).findViewById(R.id.database_header);
        databaseView.setText(database);
    }

}
