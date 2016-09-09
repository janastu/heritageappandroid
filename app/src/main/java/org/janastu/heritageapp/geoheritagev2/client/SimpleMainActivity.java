package org.janastu.heritageapp.geoheritagev2.client;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.janastu.heritageapp.geoheritagev2.client.activity.AboutActivity;
import org.janastu.heritageapp.geoheritagev2.client.activity.LoginActivity;
import org.janastu.heritageapp.geoheritagev2.client.activity.RegisterActivity;
import org.janastu.heritageapp.geoheritagev2.client.activity.SettingsActivity;
import org.janastu.heritageapp.geoheritagev2.client.activity.fragments.DefaultMapActivityFragment;
import org.janastu.heritageapp.geoheritagev2.client.activity.fragments.MapActivity;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.fragments.CaptureImageFragment;
import org.janastu.heritageapp.geoheritagev2.client.fragments.CaptureImageFragment;
import org.janastu.heritageapp.geoheritagev2.client.fragments.CaptureTextFragment;
import org.janastu.heritageapp.geoheritagev2.client.fragments.LoginFragment;
import org.janastu.heritageapp.geoheritagev2.client.fragments.RegisterFragment;
import org.janastu.heritageapp.geoheritagev2.client.fragments.SelectAppFragment;
import org.janastu.heritageapp.geoheritagev2.client.fragments.UploadFragment;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.DownloadResultReceiver;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.FileUploadService;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.MarkerResultReceiver;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.UpdateAppInfoService;
import org.janastu.heritageapp.geoheritagev2.client.pojo.DownloadInfo;
import org.janastu.heritageapp.geoheritagev2.client.services.HeritageLocationProvider;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationService;
import org.janastu.heritageapp.geoheritagev2.client.services.ReverseGeocodingService;
import org.osmdroid.util.GeoPoint;

import static android.location.LocationManager.*;

public class SimpleMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CaptureImageFragment.OnCaptureImageFragmentInteractionListener, UploadFragment.OnUploadFragmentInteractionListener, LoginFragment.OnLoginFragmentInteractionListener, RegisterFragment.OnRegisterFragmentInteractionListener, DefaultMapActivityFragment.OnDefaultMapFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DownloadResultReceiver.Receiver, CaptureTextFragment.OnCaptureTextFragmentInteractionListener,MarkerResultReceiver.MarkerReceiver,  HeritageLocationProvider.LocationCallback, LocationResultListener {


    ///STORE TOKEN

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String PREFS_USERNAME = "username";
    public static final String PREFS_PASSWORD = "password";
    public static final String LOGIN_DATE = "logindate";

    public static final String PREFS_ACCESS_TOKEN = "access_token";
    public static final String PREFS_JSON_FEATURECOLLECTION = "geojsonstring";
    public static final String PREFS_JSON_APPINFO = "appinfo";

    private static final String TAG = "HOME";
    private Location currentLocation;
    private TextView lblLocation;
    TextView userNameHeader;
    private Location mLastLocation;
    GeoTagMediaDBHelper geoTagMediaDBHelper;
    private HeritageLocationProvider mLocationProvider;

    private DownloadResultReceiver mReceiver;
    private MarkerResultReceiver markerResultReceiver;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    //All fragments global decalaration
    UploadFragment uploadFragment;
    DefaultMapActivityFragment defaultMapActivityFragment;
    LoginFragment secFragment;
    LocationService locationService;
    private Location currentLocation2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_main);

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uploadFragment = new UploadFragment();
        secFragment = new LoginFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        geoTagMediaDBHelper = new GeoTagMediaDBHelper(getApplicationContext());
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        userNameHeader = (TextView) header.findViewById(R.id.userNameHeader);

        //check if user already has a valid login


        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        markerResultReceiver = new MarkerResultReceiver(new Handler());
        markerResultReceiver.setReceiver(this);

        mLocationProvider = new HeritageLocationProvider(this, this);
        mLocationProvider.connect();

        ///trying the second one;
        locationService = new LocationService();
        locationService.getLocation(getApplicationContext(), this);

        if (checkPlayServices()) {

            // Building the GoogleApi client
            mLastLocation =  getCurrentLocation();
            displayLocation();
            defaultMapActivityFragment =   DefaultMapActivityFragment.newInstance(mLastLocation);

            showDefaultMapActivityFragment();

        }



    }


    @Override
    public void onLocationResultAvailable(Location location) {

        Log.d("location", "location" + location.toString());
        currentLocation2 = location;

        //   mapView.setCenterCoordinate(new LatLng(, ));
        //   mapView.setZoomLevel(15);




    }
    private void showDefaultMapActivityFragment() {


        // defaultMapActivityFragment.setArguments(getIntent().getExtras());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        defaultMapActivityFragment.updateLocation(currentLocation2);
        transaction.replace(R.id.fragment_container, defaultMapActivityFragment);
        transaction.commit();
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
        if (id == R.id.miCamera) {

            if (isLoggedIn() == false) {
                Toast.makeText(getBaseContext(), "Pls Login to add Image ", Toast.LENGTH_LONG).show();
                return true;
            } else {
                mLastLocation =  getCurrentLocation();
                CaptureImageFragment cameraFragment = CaptureImageFragment.newInstance(mLastLocation);
                //cameraFragment.setArguments(getIntent().getExtras());

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_container, cameraFragment);
                // Add the fragment to the 'fragment_container' FrameLayout
                // Commit the transaction
                transaction.commit();
            }
            return true;
        }


        if (id == R.id.miText) {

            if (isLoggedIn() == false) {
                Toast.makeText(getBaseContext(), "Pls Login to add Text ", Toast.LENGTH_LONG).show();
                return true;
            } else {
                mLastLocation =  getCurrentLocation();
                CaptureTextFragment textFragment = CaptureTextFragment.newInstance(mLastLocation);
                textFragment.setArguments(getIntent().getExtras());

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_container, textFragment);
                // Add the fragment to the 'fragment_container' FrameLayout
                // Commit the transaction
                transaction.commit();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //SelectAppFragment


//
        if (id == R.id.nav_home) {

            this.showDefaultMapActivityFragment();

            // Handle the camera action
        }
        if (id == R.id.nav_login) {

            showLoginScreen();

            // Handle the camera action
        }
        //DefaultMapActivityFragment
        else if (id == R.id.nav_upload) {

            //    uploadFragment.setArguments(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment_container, uploadFragment);
            transaction.commit();
        } else if (id == R.id.nav_settings) {


            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;

        } else if (id == R.id.nav_about_app) {
            Intent i = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_sign_out) {

            logout();


        } else if (id == R.id.nav_explore_maps) {
            Intent i = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isLoggedIn() {
        boolean result = false;
        SharedPreferences settings = getSharedPreferences(SimpleMainActivity.PREFS_NAME, 0);
        String userName = settings.getString(SimpleMainActivity.PREFS_USERNAME, "");
        String password = settings.getString(SimpleMainActivity.PREFS_PASSWORD, "");
        String currentToken = settings.getString(SimpleMainActivity.PREFS_ACCESS_TOKEN, "");
        Log.d(TAG, "SharedPreferences userName" + userName);
        Log.d(TAG, "SharedPreferences password" + password);
        Log.d(TAG, "SharedPreferences currentToken" + currentToken);
        if (userName == null || password == null) {
            result = false;
        }


        if (userName.isEmpty() || password.isEmpty()) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }

    private void logout() {

        SharedPreferences settings = getSharedPreferences(SimpleMainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SimpleMainActivity.PREFS_USERNAME, "");
        editor.putString(SimpleMainActivity.PREFS_ACCESS_TOKEN, "");
        editor.putString(SimpleMainActivity.PREFS_PASSWORD, "");
        editor.putString(SimpleMainActivity.LOGIN_DATE, "");
        editor.commit();
        userNameHeader.setText("You are not logged in ! Pls login ");
        showDefaultMapActivityFragment();
        showLoginScreen();

    }

    private void showLoginScreen() {


        // secFragment.setArguments(getIntent().getExtras());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, secFragment);
        // Add the fragment to the 'fragment_container' FrameLayout
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onLoginFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCaptureImageFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCaptureTextFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLoginSuccess(String username, String password, LoginResponse result, String d) {
        SharedPreferences settings = getSharedPreferences(SimpleMainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SimpleMainActivity.PREFS_USERNAME, username);
        editor.putString(SimpleMainActivity.PREFS_ACCESS_TOKEN, result.getToken());
        editor.putString(SimpleMainActivity.PREFS_PASSWORD, password);
        editor.putString(SimpleMainActivity.LOGIN_DATE, d);
        editor.commit();

        userNameHeader.setText("Logged in as " + username);
        showDefaultMapActivityFragment();

    }

    @Override
    public void onLoginFailure(LoginResponse result) {

        Toast.makeText(getBaseContext(), "Login Failure" + result.getToken(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void registerBtnClicked() {
        RegisterFragment regFragment = new RegisterFragment();
        regFragment.setArguments(getIntent().getExtras());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, regFragment);
        transaction.commit();
    }

    @Override
    public void onLoginDateStillValid() {

    }


    @Override
    public void onRegisterFragmentInteraction(Uri uri) {
        LoginFragment secFragment = new LoginFragment();
        secFragment.setArguments(getIntent().getExtras());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, secFragment);
        transaction.commit();
    }

    @Override
    public void onDefaultMapFragmentInteraction(Uri uri) {

        getAppInfoService();

    }

    @Override
    public void onUploadFragmentInteraction(Uri uri) {
///send the information
    }

    @Override
    public void onUploadGeoMediaToServer(DownloadInfo info) {


        Intent uploadIntent = new Intent(Intent.ACTION_SYNC, null, this, FileUploadService.class);
        uploadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        Log.d(TAG, "activity upload info" + info);
        Log.d(TAG, "activity upload info file " + info.getUrlOrfileLink());

        uploadIntent.putExtra("id", info.getId());

        uploadIntent.putExtra("title", info.getTitle());
        uploadIntent.putExtra("description", info.getDescription());
        uploadIntent.putExtra("category", info.getHeritageCategory());
        uploadIntent.putExtra("language", info.getHeritageLanguage());
        uploadIntent.putExtra("group", info.getHeritageGroup());
        uploadIntent.putExtra("latitude", info.getLatitude());
        uploadIntent.putExtra("longitude", info.getLongitude());
        uploadIntent.putExtra("mediatype", info.getMediaType());
        uploadIntent.putExtra("fileName", info.getUrlOrfileLink());
        uploadIntent.putExtra("currentApp", SettingsActivity.getCurrentApp());
        uploadIntent.putExtra("currentUser", getCurrentUser());
        //getCurrentApp()

        uploadIntent.putExtra("receiver", mReceiver);
        startService(uploadIntent);
    }

    public void getAppInfoService()
    {
        Intent appIntent = new Intent(Intent.ACTION_SYNC, null, this, UpdateAppInfoService.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        appIntent.putExtra("markreceiver", markerResultReceiver);
        startService(appIntent);
    }

    @Override
    public Location getCurrentLocation() {

         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
      //      return mLastLocation;
        }

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if(mLastLocation !=null) {
            Log.d(TAG, "current location getting lat" + mLastLocation.getLatitude());
            Log.d(TAG, "current location getting lang" + mLastLocation.getLongitude());
        }
        else
        {
            Toast.makeText( getApplicationContext(), "(Couldn't get the location. Make sure location is enabled on the device)", Toast.LENGTH_SHORT).show();

        }


        return mLastLocation;
    }

    private String getCurrentUser()
    {
        SharedPreferences settings = getSharedPreferences(SimpleMainActivity.PREFS_NAME, 0);
        String userName = settings.getString(SimpleMainActivity.PREFS_USERNAME, "");
        return userName;
    }


    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        //    Toast.makeText( getApplicationContext(), "(Couldn't get the location. Make sure location is enabled on the device)", Toast.LENGTH_SHORT).show();

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            Toast.makeText( getApplicationContext(), "GPS"+latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
            defaultMapActivityFragment.setCenterAndRedraw(latitude, longitude);


        } else {

            Toast.makeText( getApplicationContext(), "(Couldn't get the location. Make sure location is enabled on the device)", Toast.LENGTH_SHORT).show();


        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /* Class My Location Listener */
    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {


        switch (resultCode) {
            case FileUploadService.STATUS_RUNNING:

                Log.i(TAG, "FileUploadService.STATUS_RUNNING ");

                //start the progress bar
                uploadFragment.startProgress();
                break;
            case FileUploadService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */

                //
                int id = resultData.getInt("ID");
                Log.i(TAG, "FileUploadService.STATUS_FINISHED" + id);
                geoTagMediaDBHelper.updateWaypointToDownloaded("", id);
                uploadFragment.stopProgress();
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(uploadFragment);
                ft.attach(uploadFragment);
                ft.commit();




                break;
            case FileUploadService.STATUS_ERROR:
                /* Handle the error */
                Log.i(TAG, "FileUploadService.STATUS_ERROR ");


                break;
        }
    }


    @Override
    public void onMarkerReceiveResult(int resultCode, Bundle resultData) {
        //trigger another task that will syns the map ;

        defaultMapActivityFragment.startLoadMarkerTask();;
    }

    ///
    @Override
    public void handleNewLocation(Location location) {

        Log.i(TAG, "RECEING LONG" + location.getLongitude() +"LAT"+location.getLatitude() );
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
     //   mLastLocation.setLatitude(currentLatitude);
      //  mLastLocation.setLatitude(currentLongitude);
        mLastLocation = location;
        defaultMapActivityFragment.updateLocation(location);
    }

    ////
}
