package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.pojo.AppConstants;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageCategory;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageGroup;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageLanguage;
import org.janastu.heritageapp.geoheritagev2.client.services.AddressResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationService;
import org.janastu.heritageapp.geoheritagev2.client.services.ReverseGeocodingService;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;






public class BrowseAndUploadActivity extends Activity implements LocationResultListener, AddressResultListener {

    private static final String TAG =  "CaptureAudioActivity";
    Button browse;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;

    LocationService locationService;
    private Address currentAddress;
    private Location currentLocation;

    private EditText editTextTitle, editTextDescription;
    GeoTagMediaDBHelper geoTagMediaDBHelper = new GeoTagMediaDBHelper(this);

    String heritageCategory;
    String heritageLanguage;
    String heritageGroup  ;

    private static final int REQUEST_PICK_FILE = 1;
    private File selectedFile;
    private TextView filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //     setContentView(R.layout.activity_capture_audio);
        setContentView(R.layout.activity_design_browse_upload_screen);

        browse = (Button)findViewById(R.id.button3);


        filePath = (TextView)findViewById(R.id.file_path);
        Calendar cal = Calendar.getInstance();
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String formattedTime = outputFormat.format(cal.getTime());



        //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+formattedTime +"_heritageaudio.3gp";;



        initSpinnerAndMap(savedInstanceState);





    }


    public long pushToDB(String title, String description, String address, Double latitude,
                         Double longitude, String consolidatedTags, String urlOrfileLink,
                         String heritageCategory, String heritageLanguage,  Integer mediaType, Long fileSize)

    {

        long id = geoTagMediaDBHelper.insertWaypoint(title, description, address, latitude, longitude, consolidatedTags, urlOrfileLink, heritageCategory, heritageLanguage, heritageLanguage, mediaType, fileSize);
        return id;



    }

    public void onBrowse( View v)
    {

        Intent intent = new Intent(this, FilePicker.class);
        startActivityForResult(intent, REQUEST_PICK_FILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK) {

            switch(requestCode) {

                case REQUEST_PICK_FILE:

                    if(data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {

                        selectedFile = new File
                                (data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        filePath.setText(selectedFile.getPath());
                    }
                    break;
            }
        }
    }
    private void initSpinnerAndMap(Bundle savedInstanceState) {


        locationService = new LocationService();
        locationService.getLocation(getApplicationContext(), this);


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
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterGroups = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groups);
        // Drop down layout style - list view with radio button
        dataAdapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        Spinner spinnerGroup = (Spinner) findViewById(R.id.spinnerGroup);
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


        ///atore audio

        Button storeButton  = (Button) findViewById(R.id.btnStoreAudio);

        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = editTextTitle.getText().toString();
                String description =  editTextDescription.getText().toString();
                if(title == null || title.isEmpty() || description == null || description.isEmpty() )
                {
                    Toast.makeText(getApplicationContext(), " Pls fill in the Title/Description", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(currentLocation == null  )
                {
                    Toast.makeText(getApplicationContext(), "  Enable- GPS  ", Toast.LENGTH_SHORT).show();
                    return;
                }
                Double latitude = (  currentLocation.getLatitude()  );
                Double longitude =  ( currentLocation.getLongitude() );

                if(latitude == null || longitude == null)
                {
                    Toast.makeText(getApplicationContext(), "Pls enable GPS - GPS Co-ordinates missing ", Toast.LENGTH_SHORT).show();
                    return;
                }
                String address = currentAddress.toString();
                Integer mediaType = AppConstants.AUDIOTYPE;

                String urlOrfileLink;
                if(outputFile != null) {
                    urlOrfileLink = outputFile;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), " Video was not taken - Pl record  ", Toast.LENGTH_SHORT).show();
                    return;
                }
                long fileSize = 0;
                //Log.d(TAG, "video file size"+fileSize);
                String consolidatedTags ="Tag Video";

                long result = pushToDB(  title,   description,   address,   latitude,
                        longitude,   consolidatedTags,   urlOrfileLink,
                        heritageCategory,   heritageLanguage,    mediaType, fileSize);


                if(result == -1)
                {
                    Toast.makeText(getApplicationContext(), "Storage Issue  ", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Stored for Uplload  ", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MaterialMainActivity.class);
                    startActivity(i);

                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationResultAvailable(Location location) {

        Log.d("location", "location" + location.toString());
        currentLocation = location;

        new ReverseGeocodingService(getApplicationContext(), this).execute(location);


    }

    @Override
    public void onAddressAvailable(Address address) {

        currentAddress = address;
        Log.d(TAG, "currentAddress" + address.toString());

    }
}