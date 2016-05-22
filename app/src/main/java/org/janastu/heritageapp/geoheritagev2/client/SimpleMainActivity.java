package org.janastu.heritageapp.geoheritagev2.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.janastu.heritageapp.geoheritagev2.client.activity.AboutActivity;
import org.janastu.heritageapp.geoheritagev2.client.activity.LoginActivity;
import org.janastu.heritageapp.geoheritagev2.client.activity.RegisterActivity;
import org.janastu.heritageapp.geoheritagev2.client.activity.SettingsActivity;
import org.janastu.heritageapp.geoheritagev2.client.activity.fragments.MapActivity;
import org.janastu.heritageapp.geoheritagev2.client.fragments.LoginFragment;
import org.janastu.heritageapp.geoheritagev2.client.fragments.RegisterFragment;

public class SimpleMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , LoginFragment.OnLoginFragmentInteractionListener, RegisterFragment.OnRegisterFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.simple_main, menu);
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
    }
/*

    <item
            android:id="@+id/nav_login"
            android:icon="@drawable/ic_login"
            android:title="Login" />
        <item
            android:id="@+id/nav_register"
            android:icon="@drawable/ic_register_icon_150x150"
            android:title="Register" />
        <item
            android:id="@+id/nav_upload"
            android:icon="@drawable/ic_upload_xxl"
            android:title="Upload" />
        <item
            android:id="@+id/nav_logs"
            android:icon="@drawable/ic_menu_send"
            android:title="Logs" />

        <item
        android:id="@+id/nav_settings"
        android:icon="@drawable/ic_settings"
        android:title="Settings" />

        <item
            android:id="@+id/nav_about_app"
            android:icon="@drawable/ic_infobox_info_icon"
            android:title="About" />

        <item
            android:id="@+id/nav_sign_out"
            android:icon="@drawable/ic_basic2_010_exit_logout_512"
            android:title="Sign Out" />
 */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login)
        {


            LoginFragment secFragment = new LoginFragment();
            secFragment.setArguments(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment_container, secFragment);
            // Add the fragment to the 'fragment_container' FrameLayout
            // Commit the transaction
            transaction.commit();
            // Handle the camera action
        }
        else if (id == R.id.nav_register)
        {
            RegisterFragment regFragment = new RegisterFragment();
            regFragment.setArguments(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment_container, regFragment);
            transaction.commit();
        }
        else if (id == R.id.nav_logs)
        {
            //

        }
        else if (id == R.id.nav_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_about_app)
        {
            Intent i = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_sign_out) {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLoginFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLoginSuccess(String username, String password, LoginResponse result, String d ) {
        SharedPreferences settings = getSharedPreferences(MaterialMainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(MaterialMainActivity.PREFS_USERNAME, username);
        editor.putString(MaterialMainActivity.PREFS_ACCESS_TOKEN, result.getToken());
        editor.putString(MaterialMainActivity.PREFS_PASSWORD, password);
        editor.putString(MaterialMainActivity.LOGIN_DATE, d);
        editor.commit();
        //move to map fragment //

        Intent i = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(i);
    }

    @Override
    public void onLoginFailure(LoginResponse result ) {

        Toast.makeText(getBaseContext(), "Login Failure" + result.getToken(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginDateStillValid( ) {

    }


    @Override
    public void onRegisterFragmentInteraction(Uri uri) {

    }
}
