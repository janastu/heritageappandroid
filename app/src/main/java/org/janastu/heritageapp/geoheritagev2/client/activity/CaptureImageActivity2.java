package org.janastu.heritageapp.geoheritagev2.client.activity;

import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.pojo.AppConstants;
import org.janastu.heritageapp.geoheritagev2.client.services.AddressResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationService;
import android.os.Bundle;

import android.view.View;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.widget.*;


import org.janastu.heritageapp.geoheritagev2.client.pojo.*;
import org.osmdroid.views.MapView;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import android.provider.MediaStore.MediaColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import org.janastu.heritageapp.geoheritagev2.client.services.ReverseGeocodingService;

import android.support.v7.app.ActionBarActivity;

import android.view.MenuItem;




public class CaptureImageActivity2 extends ActionBarActivity implements LocationResultListener, AddressResultListener {
    private String selectedImagePath = "";
    final private int PICK_IMAGE = 1;
    final private int CAPTURE_IMAGE = 2;
    ImageView imgView;
    private String imgPath;
    GeoTagMediaDBHelper geoTagMediaDBHelper = new GeoTagMediaDBHelper(this);
    private EditText editTextTitle, editTextDescription;
    private TextView filePath;

    LocationService locationService;
    private Address currentAddress;
    private Location currentLocation;
    private MapView mapView = null;
    Button browse;

    byte[] mpicture = null;
    byte[] mpictureProof = null;
    File mpictureFile = null;
    File mpictureProofFile = null;
    private Camera camera;

    String heritageCategory  ;
    String heritageLanguage  ;
    String heritageGroup  ;
    private static final String TAG = "TakePhotoActivity22";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_capture_image_screen22);


        filePath = (TextView)findViewById(R.id.file_path);
        imgView = (ImageView) findViewById(R.id.showImg);
        Button butCamera = (Button) findViewById(R.id.btnCapturePhoto);
        butCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Intent intent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        setImageUri());
                startActivityForResult(intent, CAPTURE_IMAGE);
            }
        });

        Button butGallery = (Button) findViewById(R.id.btnBrowsePhoto);
        butGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, ""),
                        PICK_IMAGE);
            }
        });

        ////
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        initSpinnerAndMap(savedInstanceState);

        initStoreButton();
        ///

    }
    public void initStoreButton()
    {

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
                    Toast.makeText(getApplicationContext(), "Take Picture & Try - GPS Co-ordinates missing ", Toast.LENGTH_SHORT).show();
                    return;
                }
                String address ="na";
                if(currentAddress != null)
                {
                    address  = currentAddress.toString();
                }

                Integer mediaType = AppConstants.IMAGETYPE;
                String urlOrfileLink = selectedImagePath;
                if(urlOrfileLink == null) {

                    Toast.makeText(getApplicationContext(), " Picture was not taken ", Toast.LENGTH_SHORT).show();
                    return;
                }
//                long fileSize = mpictureFile.length();
                String consolidatedTags ="dd";


                Log.d(TAG, "storing into DB" +title+"title"+   description+"description"+   address+"address"+ "heritageGroup"+heritageGroup);
                long result = pushToDB(  title,   description,   address,   latitude,
                        longitude,   consolidatedTags,   urlOrfileLink,
                        heritageCategory,   heritageLanguage,  heritageGroup,  mediaType, 100);
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

    }


    private void initSpinnerAndMap(Bundle savedInstanceState) {











        locationService = new LocationService();
        locationService.getLocation(getApplicationContext(), this);


        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);

        // Spinner element
        Spinner spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);

        // Spinner click listener
        //spinner.setOnItemSelectedListener(getApplicationContext());
        HeritageAppDTO d = null;


        // Spinner Drop down elements
        try {
            d = SettingsActivity.getCurrentAppInfo();
        }
        catch(Exception e)
        {

            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);

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
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
    }



    public long pushToDB(String title, String description, String address, Double latitude,
                         Double longitude, String consolidatedTags, String urlOrfileLink,
                         String heritageCategory, String heritageLanguage,  String heritageGroup, Integer mediaType, long fileSize)

    {

        long result = geoTagMediaDBHelper.insertWaypoint(title, description, address, latitude, longitude, consolidatedTags, urlOrfileLink, heritageCategory, heritageLanguage,heritageGroup, mediaType, fileSize);
        Log.d("Inserted", result + "Insert Way Point" + fileSize + urlOrfileLink);


        return result;
    }


    @Override
    public void onLocationResultAvailable(Location location) {

        Log.d("location", "location" + location.toString());
        currentLocation = location;

        //   mapView.setCenterCoordinate(new LatLng(, ));
        //   mapView.setZoomLevel(15);



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
    public Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory()
                + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        this.imgPath = file.getAbsolutePath();
        return imgUri;
    }

    public String getImagePath() {
        return imgPath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE) {
                selectedImagePath = getAbsolutePath(data.getData());
                imgView.setImageBitmap(decodeFile(selectedImagePath));
            } else if (requestCode == CAPTURE_IMAGE) {
                selectedImagePath = getImagePath();
                imgView.setImageBitmap(decodeFile(selectedImagePath));
            } else {
                super.onActivityResult(requestCode, resultCode,
                        data);
            }
        }

        filePath.setText(selectedImagePath);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is
        // present.
        // getMenuInflater().inflate(R.menu.main, menu);
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

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of
            // 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}
