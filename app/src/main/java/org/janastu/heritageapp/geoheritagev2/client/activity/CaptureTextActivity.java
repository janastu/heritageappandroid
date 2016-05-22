package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Address;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;


import org.geojson.Point;
import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.pojo.*;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.geojson.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.pojo.AppConstants;
import org.janastu.heritageapp.geoheritagev2.client.services.AddressResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationService;


import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.janastu.heritageapp.geoheritagev2.client.services.ReverseGeocodingService;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class CaptureTextActivity extends Activity implements LocationResultListener, AddressResultListener {



    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
    private static final String TAG = "TakeTextActivity";
    GeoTagMediaDBHelper geoTagMediaDBHelper = new GeoTagMediaDBHelper(this);
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;


    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    ImageView ivPhoto;
    File myFilesDir;

    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;


    Uri imageUri = null;
    Uri imageUri2 = null;
    static TextView imageDetails = null;
    static EditText phoneNum = null;
    static EditText name = null;
    public static ImageView showImg = null;


    static TextView imageDetailsAddress = null;



    LocationService locationService;
    private Address currentAddress;
    private Location currentLocation;



    private Camera camera;

    String heritageCategory  ;
    String heritageLanguage  ;



    private String mNumber;


    private EditText editTextTitle, editTextDescription;
    private String heritageGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_capture_text);

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);



        Spinner spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        HeritageAppDTO d = null;
        // Spinner Drop down elements
        try {
            d = SettingsActivity.getCurrentAppInfo();
        }
        catch(Exception e)
        {

        }

        Set<HeritageCategory> categSet  = d.getCategorys();
        List<String> categories = new ArrayList<String>();
        for(HeritageCategory c:categSet)
        {
            categories.add(c.getCategoryName());

        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerCategory.setAdapter(dataAdapter);


        spinnerCategory.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        heritageCategory = parent.getItemAtPosition(pos).toString();
                        Log.d(TAG, "selected heritageCategory "+heritageCategory.toString());     //prints the text in spinner item.

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        Set<HeritageGroup> groupSet  = d.getGroups();
        List<String> groups = new ArrayList<String>();
        for(HeritageGroup g:groupSet)
        {
            groups.add(g.getName() );

        }
        Spinner spinnerGroup = (Spinner) findViewById(R.id.spinnerGroup);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterGroups = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groups);
        // Drop down layout style - list view with radio button
        dataAdapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner

        spinnerGroup.setAdapter(dataAdapterGroups);


        spinnerGroup.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        heritageGroup = parent.getItemAtPosition(pos).toString();
                        Log.d(TAG, "selected heritageGroup "+heritageGroup.toString());     //prints the text in spinner item.

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        Set<HeritageLanguage> langSet  = d.getLanguages();
        List<String> languages = new ArrayList<String>();
        for(HeritageLanguage l:langSet)
        {
            languages.add(l.getHeritageLanguage());
        }



        Spinner spinnerLanguage = (Spinner) findViewById(R.id.spinnerLanguage);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterLanguages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        // Drop down layout style - list view with radio button
        dataAdapterLanguages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerLanguage.setAdapter(dataAdapterLanguages);

        spinnerLanguage.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        heritageLanguage = parent.getItemAtPosition(pos).toString();
                        Log.d(TAG, "selected  Language "+heritageLanguage.toString());     //prints the text in spinner item.

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        final Button storeToDB = (Button) findViewById(R.id.btnStoreToDB);

        storeToDB.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                Log.d(TAG, "storing into DB");

                String title = editTextTitle.getText().toString();

                String description =  editTextDescription.getText().toString();

                if(title == null || title.isEmpty() || description == null || description.isEmpty() )
                {
                    Toast.makeText(getApplicationContext(), " Pls fill in the Title/Description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(currentLocation == null)
                {
                    Toast.makeText(getApplicationContext(), " Unable to get the GPS Co-ordinates - Location is null - Check GPS ", Toast.LENGTH_SHORT).show();
                    return;
                }
                Double latitude = (  currentLocation.getLatitude()  );
                Double longitude =  ( currentLocation.getLongitude() );
                if(latitude == null || longitude == null)
                {
                    Toast.makeText(getApplicationContext(), "     Try - GPS Co-ordinates missing ", Toast.LENGTH_SHORT).show();
                    return;
                }
                String address ="na";
                if(currentAddress != null)
                {
                    address  = currentAddress.toString();
                }

                Integer mediaType = AppConstants.TEXTTYPE;


                String consolidatedTags ="dd";


                Log.d(TAG, "storing into DB" +title+"title"+   description+"description"+   address+"address" );
                long result = pushToDB(  title,   description,   address,   latitude,
                        longitude,   consolidatedTags,   "",
                        heritageCategory,   heritageLanguage, heritageGroup,   mediaType, 0);
                if(result == -1)
                {
                    Toast.makeText(getApplicationContext(), "Storage Issue  ", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Stored for Upload  ", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MaterialMainActivity.class);
                    startActivity(i);

                }
            }
        });




        // Set up the login form.



        initSpinnerAndMap(savedInstanceState);

    }

    private void initSpinnerAndMap(Bundle savedInstanceState) {




        GeoPoint gPt = new GeoPoint(11.857674384942547, 79.79164123535155);


        locationService = new LocationService();
        locationService.getLocation(getApplicationContext(), this);


        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);

        // Spinner element

        Spinner spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        HeritageAppDTO d = null;
        // Spinner Drop down elements
        try {
            d = SettingsActivity.getCurrentAppInfo();
        }
        catch(Exception e)
        {

        }

        Set<HeritageCategory> categSet  = d.getCategorys();
        List<String> categories = new ArrayList<String>();
        for(HeritageCategory c:categSet)
        {
            categories.add(c.getCategoryName());

        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerCategory.setAdapter(dataAdapter);


        spinnerCategory.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        heritageCategory = parent.getItemAtPosition(pos).toString();
                        Log.d(TAG, "selected heritageCategory " + heritageCategory.toString());     //prints the text in spinner item.

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        Set<HeritageGroup> groupSet  = d.getGroups();
        List<String> groups = new ArrayList<String>();
        for(HeritageGroup g:groupSet)
        {
            groups.add(g.getName() );

        }
        Spinner spinnerGroup = (Spinner) findViewById(R.id.spinnerGroup);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterGroups = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groups);
        // Drop down layout style - list view with radio button
        dataAdapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner

        spinnerGroup.setAdapter(dataAdapterGroups);


        spinnerGroup.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        heritageGroup = parent.getItemAtPosition(pos).toString();
                        Log.d(TAG, "selected heritageGroup " + heritageGroup.toString());     //prints the text in spinner item.

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        Set<HeritageLanguage> langSet  = d.getLanguages();
        List<String> languages = new ArrayList<String>();
        for(HeritageLanguage l:langSet)
        {
            languages.add(l.getHeritageLanguage());

        }

        Spinner spinnerLanguage = (Spinner) findViewById(R.id.spinnerLanguage);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterLanguages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        // Drop down layout style - list view with radio button
        dataAdapterLanguages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerLanguage.setAdapter(dataAdapterLanguages);

        spinnerLanguage.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        heritageLanguage = parent.getItemAtPosition(pos).toString();
                        Log.d(TAG, "selected  Language "+heritageLanguage.toString());     //prints the text in spinner item.

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

    }


    public long pushToDB(String title, String description, String address, Double latitude,
                         Double longitude, String consolidatedTags, String urlOrfileLink,
                         String heritageCategory, String heritageLanguage,String heritageGroup , Integer mediaType, long fileSize)

    {

        long result = geoTagMediaDBHelper.insertWaypoint(title, description, address, latitude, longitude, consolidatedTags, urlOrfileLink, heritageCategory, heritageLanguage, heritageGroup,mediaType, fileSize);
        Log.d("Inserted"  , result +"Insert Way Point"+ fileSize + urlOrfileLink);

        return result;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //   getMenuInflater().inflate(R.menu.menu_multimedia, menu);
        return true;
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */


    @Override
    public void onLocationResultAvailable(Location location) {

        Log.d("location", "location" + location.toString());
        currentLocation = location;

        //   mapView.setCenterCoordinate(new LatLng(, ));
        //   mapView.setZoomLevel(15);

        GeoPoint gPt = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
      //  mMapController.setCenter(gPt);

        new ReverseGeocodingService(getApplicationContext(), this).execute(location);


    }

    @Override
    public void onAddressAvailable(Address address) {

        currentAddress = address;
        if(address != null)
            Log.d(TAG, "currentAddress" + address.toString());
        else
            Log.d(TAG, "currentAddress" + "is null");

    }




}