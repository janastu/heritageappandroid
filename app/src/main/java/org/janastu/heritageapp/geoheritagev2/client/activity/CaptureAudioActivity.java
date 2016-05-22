package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;


import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.os.Environment;

import android.widget.*;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

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




public class CaptureAudioActivity extends Activity  implements LocationResultListener, AddressResultListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    private static final String TAG =  "CaptureAudioActivity";
    private static final int RESULT_LOAD_AUDIO = 4343;
    Button play,stop,record;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private MediaPlayer mediaPlayer;
    LocationService locationService;
    private Address currentAddress;
    private Location currentLocation;

    private EditText editTextTitle, editTextDescription;
    GeoTagMediaDBHelper geoTagMediaDBHelper = new GeoTagMediaDBHelper(this);

    String heritageCategory;
    String heritageLanguage;
    String heritageGroup  ;

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(CaptureAudioActivity.this, "Error: " + what + ", " + extra,
                    Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(CaptureAudioActivity.this,
                    "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    boolean recordClickStatus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     setContentView(R.layout.activity_capture_audio);
        setContentView(R.layout.activity_design_capture_audio_screen);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

        play=(Button)findViewById(R.id.button3);
        stop=(Button)findViewById(R.id.button2);
        record=(Button)findViewById(R.id.button);



        stop.setEnabled(false);



        myAudioRecorder=new MediaRecorder();
        initSpinnerAndMap(savedInstanceState);
        record.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if(  mediaPlayer.isPlaying() )
                {
                    mediaPlayer.stop();
                }

                recordClickStatus = true;
                Calendar cal = Calendar.getInstance();
                DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String formattedTime = outputFormat.format(cal.getTime());
                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + formattedTime + "_heritageaudio.3gp";

                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                myAudioRecorder.setOutputFile(outputFile);
                myAudioRecorder.setOnErrorListener(errorListener);
                myAudioRecorder.setOnInfoListener(infoListener);

                String address = "Address not found";
                if (currentAddress != null)
                    address = currentAddress.toString();


                if (currentLocation != null) {


                }


                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                record.setEnabled(false);
                stop.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

                TextView filePath;
                filePath = (TextView)findViewById(R.id.file_path);
                filePath.setText(outputFile);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    stop.setEnabled(true);
                    play.setEnabled(true);

                    stopPlaying();
                    if(recordClickStatus == true) {
                        myAudioRecorder.stop();
                        myAudioRecorder.release();
                        recordClickStatus = false;
                    }




                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
            }
        });

       /* play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {
                Log.d(TAG,"clicked play");
              final MediaPlayer m = new MediaPlayer();
               // final MediaPlayer    m = MediaPlayer.create(this, Uri.parse(""));
                m.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    m.setDataSource(outputFile);
                    m.prepare();

                    m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if (mp == m) {
                                m.start();
                                Toast.makeText(getApplicationContext(), "Playing audio" + outputFile, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch ( Exception e) {
                    e.printStackTrace();
                }









            }
        });*/
        Context context = getApplicationContext();
        play.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {
                stop.setEnabled(true);


                try {
                    mediaPlayer.reset();
                    Log.d(TAG, "mediaPlayer.start  outputFile"+outputFile);
                    mediaPlayer.setDataSource(outputFile);
                    Log.d(TAG, "mediaPlayer.setDataSource   ");
                    mediaPlayer.prepareAsync();
                }catch(Exception e)
                {
                   Log.d(TAG, "exceptin while playing");


                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        Log.d(TAG, "mediaPlayer.start bfored onPrepared");
                        mediaPlayer.start();

                        Log.d(TAG, "mediaPlayer.start  onPrepared");
              //          mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
                //        primarySeekBarProgressUpdater();
                    }
                });
            };
        });


        /*


         */


    }


    public long pushToDB(String title, String description, String address, Double latitude,
                            Double longitude, String consolidatedTags, String urlOrfileLink,
                            String heritageCategory, String heritageLanguage,  Integer mediaType, Long fileSize)

    {

        long id = geoTagMediaDBHelper.insertWaypoint(title, description, address, latitude, longitude, consolidatedTags, urlOrfileLink, heritageCategory, heritageLanguage,heritageLanguage, mediaType, fileSize);
        return id;



    }
    private void initSpinnerAndMap(Bundle savedInstanceState) {


        locationService = new LocationService();
        locationService.getLocation(getApplicationContext(), this);


        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);

        Button butGallery = (Button) findViewById(R.id.btnBrowseAudio);
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
                startActivityForResult(galleryIntent, RESULT_LOAD_AUDIO);
            }
        });

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String contentPath ="";
        if(requestCode == RESULT_LOAD_AUDIO)
        {
            if (resultCode == RESULT_OK) {

                if (data != null) {


                    Uri audioFileUri = data.getData();
                    play.setEnabled(true);
                    outputFile = audioFileUri.getPath();

                    Log.d(TAG, "outputFile" + outputFile);

                }

            }
        }

        TextView filePath;
        filePath = (TextView)findViewById(R.id.file_path);
        filePath.setText(outputFile);

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();


            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
        }


    }
}