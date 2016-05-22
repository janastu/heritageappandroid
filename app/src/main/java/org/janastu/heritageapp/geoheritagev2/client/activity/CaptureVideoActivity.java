package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import android.widget.*;

import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.pojo.AppConstants;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageCategory;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageGroup;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageLanguage;
import org.janastu.heritageapp.geoheritagev2.client.services.ReverseGeocodingService;



import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.services.AddressResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationService;


import android.view.Menu;
import android.view.MenuItem;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;


public class CaptureVideoActivity extends AppCompatActivity   implements LocationResultListener, AddressResultListener {

    private static final String TAG = "CaptureVideoActivity" ;
    LocationService locationService;
    private Address currentAddress;
    private Location currentLocation;
    private static final int RESULT_LOAD_IMAGE = 5164;; //or any int numbers
    private EditText editTextTitle, editTextDescription;
    File mediaFile;
    private String recordFileName="heritagevid";
    GeoTagMediaDBHelper geoTagMediaDBHelper = new GeoTagMediaDBHelper(this);

    String heritageCategory;
    String heritageLanguage;
    Button browse;
    String heritageGroup  ;

    private static final int VIDEO_CAPTURE = 101;
    Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_capture_video);
        setContentView(R.layout.activity_design_capture_video_screen);
        initSpinnerAndMap(savedInstanceState);
    }

    private void initSpinnerAndMap(Bundle savedInstanceState) {


        /** Create a mapView and give it some properties */


        locationService = new LocationService();
        locationService.getLocation(getApplicationContext(), this);


        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        Button butGallery = (Button) findViewById(R.id.btnBrowseVideo);
        butGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                /*
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, ""),
                        PICK_IMAGE);*/

                final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("*/*");
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
       //spiinner
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

        Button storeButton  = (Button) findViewById(R.id.btnStoreVideo);

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
                /// if curr
                if(currentLocation == null)
                {

                    Toast.makeText(getApplicationContext(), " Enable GPS - GPS Co-ordinates missing ", Toast.LENGTH_SHORT).show();
                    return;

                }

                Double latitude = (  currentLocation.getLatitude()  );
                Double longitude =  ( currentLocation.getLongitude() );

                if(latitude == null || longitude == null)
                {
                    Toast.makeText(getApplicationContext(), "Take Video & Try - GPS Co-ordinates missing ", Toast.LENGTH_SHORT).show();
                    return;
                }
                String address ="na";
                if(currentAddress != null)
                {
                    address  = currentAddress.toString();
                }
                Integer mediaType = AppConstants.VIDEOTYPE;
                String urlOrfileLink;
                if(mediaFile != null) {
                    urlOrfileLink = mediaFile.getAbsolutePath();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), " Video was not taken - Pl record  ", Toast.LENGTH_SHORT).show();
                    return;
                }
                long fileSize = mediaFile.length();
                Log.d(TAG, "video file size"+fileSize);
                String consolidatedTags ="Tag Video";

                long result = pushToDB(  title,   description,   address,   latitude,
                        longitude,   consolidatedTags,   urlOrfileLink,
                        heritageCategory,   heritageLanguage,   heritageGroup, mediaType, fileSize);


                if(result == -1)
                {
                    Toast.makeText(getApplicationContext(), "Storage Issue  ", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Stored for Upload  ", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MaterialMainActivity.class);
                    startActivity(i);

                }

            }
        });
    }


    public long pushToDB(String title, String description, String address, Double latitude,
                            Double longitude, String consolidatedTags, String urlOrfileLink,
                            String heritageCategory, String heritageLanguage, String heritageGroup, Integer mediaType, Long fileSize)

    {


        long result = geoTagMediaDBHelper.insertWaypoint(title, description, address, latitude, longitude, consolidatedTags, urlOrfileLink, heritageCategory, heritageLanguage, heritageGroup,mediaType, fileSize);
        Log.d("Inserted", result + "Insert Video Way Point" + fileSize + urlOrfileLink);
        return result;



    }

    public void onPlayLocalVideo(View v) {
        VideoView mVideoView = (VideoView) findViewById(R.id.video_view);
     /*   mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.small_video));*/

        if(recordClicked == true) {
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + recordFileName);
        }

        videoUri = Uri.fromFile(mediaFile);

        mVideoView.setVideoURI(videoUri);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();
        mVideoView.start();
    }

    public void onStreamVideo(View v) {
        final VideoView mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
            }
        });
    }


    boolean recordClicked = false;
    public void onRecordVideo(View v) {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            recordClicked = true;
    ;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            recordFileName = editTextTitle.getText().toString().trim()+"_H_V_"+timeStamp+".mp4";
            recordFileName.trim();

            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+recordFileName);
            videoUri = Uri.fromFile(mediaFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            startActivityForResult(intent, VIDEO_CAPTURE);
        } else {
            Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                VideoView mVideoView = (VideoView) findViewById(R.id.video_view);
                mVideoView.setVideoURI(videoUri);
                mVideoView.setMediaController(new MediaController(this));
                mVideoView.requestFocus();
                mVideoView.start();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",  Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",  Toast.LENGTH_LONG).show();
            }
        }
        String contentPath ="";
        if(requestCode == RESULT_LOAD_IMAGE)
        {
            if (resultCode == RESULT_OK) {

                if (data != null) {
                    Uri selectedUri = data.getData();
                    String[] columns = {MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.MIME_TYPE};

                    Cursor cursor = getContentResolver().query(selectedUri, columns, null, null, null);
                    cursor.moveToFirst();

                    int pathColumnIndex = cursor.getColumnIndex(columns[0]);
                    int mimeTypeColumnIndex = cursor.getColumnIndex(columns[1]);

                    contentPath = cursor.getString(pathColumnIndex);

                    Log.d(TAG, "contentPath" + contentPath);
                    String mimeType = cursor.getString(mimeTypeColumnIndex);
                    cursor.close();

                    if (mimeType.startsWith("image")) {
                        Toast.makeText(getApplicationContext(), " You have selected Image -  - Pl select a video  ", Toast.LENGTH_SHORT).show();
                        return;

                        //It's an image
                    } else if (mimeType.startsWith("video")) {
                        //It's a video
                        mediaFile = new File(contentPath);
                        VideoView mVideoView = (VideoView) findViewById(R.id.video_view);
                        mVideoView.setVideoURI(selectedUri);
                        mVideoView.setMediaController(new MediaController(this));
                        mVideoView.requestFocus();
                        mVideoView.start();
                    }
                } else {
                    // show error or do nothing
                }

            }
        }


        TextView filePath;
        filePath = (TextView)findViewById(R.id.file_path);
        filePath.setText(mediaFile.getAbsolutePath());



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationResultAvailable(Location location) {

        Log.d("location", "location" + location.toString());
        currentLocation = location;

       /* mapView.setCenterCoordinate(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        mapView.setZoomLevel(15);*/

        new ReverseGeocodingService(getApplicationContext(), this).execute(location);

    }

    @Override
    public void onAddressAvailable(Address address) {

        currentAddress = address;
        Log.d(TAG, "currentAddress" + address.toString());

    }
}
