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
public class CaptureImageActivity extends Activity implements LocationResultListener, AddressResultListener {

    private static final int REQUEST_PICK_FILE = 1;
    private File selectedFile;
    private TextView filePath;

    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
    private static final String TAG = "TakePhotoActivity";

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


    File myFilesDir;

    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;


    Uri imageUri = null;
    Uri imageUri2 = null;
    static TextView imageDetails = null;
    static EditText phoneNum = null;
    static EditText name = null;
    public static ImageView showImg = null;
    CaptureImageActivity CameraActivity = null;

    static TextView imageDetailsAddress = null;
    public static ImageView showImgAddress = null;

    boolean photoClicked = false;
    boolean photoAddressClicked = false;

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

    private MapView mMapView;
    private IMapController mMapController;

    private String mNumber;
    private int cameraId;
    GeoTagMediaDBHelper geoTagMediaDBHelper = new GeoTagMediaDBHelper(this);
    private EditText editTextTitle, editTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_design_capture_image_screen);

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);

        browse = (Button)findViewById(R.id.btnBrowsePhoto);


        filePath = (TextView)findViewById(R.id.file_path);

        //m = new SignInMemberMobile(name.getText().toString(), mNumber);

        //Set up photo capture
        CameraActivity = this;

        cameraId = findFrontFacingCamera();
        if (cameraId < 0) {
            Toast.makeText(this, "No front facing camera found.",
                    Toast.LENGTH_LONG).show();
        } else {
            camera = Camera.open(cameraId);
            configure(camera);
            camera.release();
        }



        showImg = (ImageView) findViewById(R.id.showImg);

        final Button photo = (Button) findViewById(R.id.btnCapturePhoto);


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
                String urlOrfileLink;
                if(mpictureFile != null) {
                    urlOrfileLink = mpictureFile.getAbsolutePath();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), " Picture was not taken ", Toast.LENGTH_SHORT).show();
                    return;
                }
                long fileSize = mpictureFile.length();
                String consolidatedTags ="dd";


                Log.d(TAG, "storing into DB" +title+"title"+   description+"description"+   address+"address"+ "heritageGroup"+heritageGroup);
                long result = pushToDB(  title,   description,   address,   latitude,
                              longitude,   consolidatedTags,   urlOrfileLink,
                              heritageCategory,   heritageLanguage,  heritageGroup,  mediaType, fileSize);
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

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoClicked = true;
                photo.setText("Try Again");
                //
                String address ="Address not found";
                if(currentAddress != null)
                    address = currentAddress.toString();


                if (currentLocation != null) {

                   /* mapView.addMarker(new MarkerOptions()
                                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                    .title(editTextTitle.getText().toString())
                                    .snippet( address.toString() )
                    );*/


                }


                //get location details


                /*************************** Camera Intent Start ************************/

                // Define the file-name to save photo taken by Camera activity

                String title = editTextTitle.getText().toString();

                Log.d(TAG, "stoting into file name "+ title);
                String fileName;
                if(title == null || title.length() == 0 )
                {

                    fileName = "Title"+ new Date().toString();
                }
                else
                {

                    fileName = title;
                }
                Log.d(TAG, "stoting into file name "+ fileName);


                // Create parameters for Intent with filename

                ContentValues values = new ContentValues();

                values.put(MediaColumns.DISPLAY_NAME, fileName);

                values.put(MediaColumns.TITLE, fileName);

                values.put(ImageColumns.DESCRIPTION,editTextDescription.getText().toString());
                ////addeed to get the file name

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

//folder stuff
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "HeritageImages");
                imagesFolder.mkdirs();

                File image = new File(imagesFolder, "QR_" + title+"-"+timeStamp + ".png");
                Uri uriSavedImage = Uri.fromFile(image);
                ////

                // imageUri is the current activity attribute, define and save it for later usage

                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                //imageUri =  uriSavedImage;

                /**** EXTERNAL_CONTENT_URI : style URI for the "primary" external storage volume. ****/


                // Standard Intent action that can be sent to have the camera
                // application capture an image and return it.

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                /*************************** Camera Intent End ************************/
            }
        });



        // Set up the login form.



        initSpinnerAndMap(savedInstanceState);

    }

    private void initSpinnerAndMap(Bundle savedInstanceState) {


        /** Create a mapView and give it some properties */
      /*  mapView = (MapView) findViewById(R.id.mapview);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        mapView.setAccessToken("pk.eyJ1IjoibXVyYWxpaHI3NyIsImEiOiJjaWo5c2tqZjYwMDNtdXhseGFqeHlsZnQ4In0.W_DdV-qM8lNZzacVotHDEA");

        mapView.onCreate(savedInstanceState);

        SpriteFactory spriteFactory = mapView.getSpriteFactory();*/
        mMapView = (MapView) findViewById(R.id.mapview2);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
//        mMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);

        //   mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapController = mMapView.getController();
        mMapController.setZoom(13);



        GeoPoint gPt = new GeoPoint(11.857674384942547, 79.79164123535155);
        mMapController.setCenter(gPt);




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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



            if (requestCode == REQUEST_PICK_FILE)  {

                if(resultCode == RESULT_OK) {

                    if(data != null) {
                        if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {

                            selectedFile = new File
                                    (data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                            filePath.setText(selectedFile.getPath());
                            //set image view


                            showImg.setImageBitmap(BitmapFactory.decodeFile(filePath.getText().toString()));
                        }
                    }
                }

            }


        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                /*********** Load Captured Image And Data Start ****************/
                String imageId = null;
                if (this.photoClicked) {
                    imageId = getRealPathFromURI(imageUri);


                    //   mpictureFile = new File(getCapturedImageFilePath(imageUri2));
                    //  Log.d("What is photoClicked this Image ID", imageId +"file path exists "+mpictureFile.exists() +"length"+ mpictureFile.length());

                }
                if (this.photoAddressClicked) {

                    imageId = getRealPathFromURI(imageUri2);
                    // mpictureProofFile = new File (getCapturedImageFilePath(imageUri2));

                    // Log.d("What is this  photoAddressClicked Image ID", imageId +"file path exists "+mpictureProofFile.exists() +"length"+ mpictureProofFile.length() );



                }

                //  Create and excecute AsyncTask to load capture image

                new LoadImagesFromSDCard().execute("" + imageId);

                /*********** Load Captured Image And Data End ****************/


            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();

            }
        }
    }


    /************ Convert Image Uri path to physical path **************/
    public String getCapturedImageFilePath(Uri mCapturedImageURI) {
        String photoPath = "";
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {
                MediaColumns.DATA, MediaColumns.DATE_ADDED, ImageColumns.ORIENTATION
        }, MediaColumns.DATE_ADDED, null, "date_added ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaColumns.DATA)));
                photoPath = uri.toString();
            } while (cursor.moveToNext());


            cursor.close();
        }

        return photoPath;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {
                BaseColumns._ID
        };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {;
            if(cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                res = cursor.getString(column_index);
            }
        }
        cursor.close();
        return res;
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

           Cursor cursor2 = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, BaseColumns._ID + " = ? ", new String[]{
                            document_id
                    }, null);
            if(cursor2 != null) {
                cursor2.moveToFirst();
                String path = cursor2.getString(cursor.getColumnIndex(MediaColumns.DATA));
                cursor2.close();

                return path;
            }
            return "";
        }
        else
            return "";
    }
    public String convertImageUriToFile(Uri imageUri, Activity activity) {

        Cursor cursor = null;
        int imageID = 0;

        try {

            /*********** Which columns values want to get *******/
            String[] proj = {
                    MediaColumns.DATA,
                    BaseColumns._ID,
                    BaseColumns._ID,
                    ImageColumns.ORIENTATION
            };

            cursor = activity.managedQuery(

                    imageUri, //  Get data for specific image URI
                    proj, //  Which columns to return
                    null, //  WHERE clause; which rows to return (all rows)
                    null, //  WHERE clause selection arguments (none)
                    null //  Order-by clause (ascending by name)

            );


            //  Get Query Data

            int columnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(BaseColumns._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);

            //int orientation_ColumnIndex = cursor.
            //    getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

            int size = cursor.getCount();

            /*******  If size is 0, there are no images on the SD Card. *****/

            if (size == 0) {


                // imageDetails.setText("No Image");

            } else {

                int thumbID = 0;
                if (cursor.moveToFirst()) {

                    /**************** Captured image details ************/

                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    imageID = cursor.getInt(columnIndex);

                    thumbID = cursor.getInt(columnIndexThumb);

                    String Path = cursor.getString(file_ColumnIndex);

                    String CapturedImageDetails = " CapturedImageDetails : \n\n" + " ImageID :" + imageID + "\n" + " ThumbID :" + thumbID + "\n" + " Path :" + Path + "\n";


                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();

            }
            //   cursor = null;
        }

        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )

        return "" + imageID;
    }
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        // Decode image size


        try {
            if (selectedImage == null) {
                Toast.makeText(getApplicationContext(), "Try Again - Image could not be stored  ", Toast.LENGTH_SHORT).show();
                return null;

            }
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 100;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(selectedImage), null, o2);

        }catch(Exception e)
        {
//            Toast.makeText(getApplicationContext(), "Try Again - Image could not be stored  ", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        try {
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return bitmap;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }
            try {
                Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return bmRotated;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void onLocationResultAvailable(Location location) {

        Log.d("location", "location" + location.toString());
        currentLocation = location;

     //   mapView.setCenterCoordinate(new LatLng(, ));
     //   mapView.setZoomLevel(15);

        GeoPoint gPt = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMapController.setCenter(gPt);

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


    public class LoadImagesFromSDCard extends AsyncTask < String, Void, Void > {

        private ProgressDialog Dialog = new ProgressDialog(CaptureImageActivity.this);

        Bitmap mBitmap;

        @Override
        protected void onPreExecute() {
            /****** NOTE: You can call UI Element here. *****/

            // Progress Dialog
          //  Dialog.setMessage(" Loading image from Sdcard..");
           // Dialog.show();
        }


        // Call after onPreExecute method


        @Override
        protected Void doInBackground(String...urls) {

            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;


            try {

                /**  Uri.withAppendedPath Method Description
                 * Parameters
                 *    baseUri  Uri to append path segment to
                 *    pathSegment  encoded path segment to append
                 * Returns
                 *    a new Uri based on baseUri with the given segment appended to the path
                 */

                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);

                /**************  Decode an input stream into a bitmap. *********/
//                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                bitmap =                decodeUri(uri);

                if (bitmap != null) {

                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/

                    //width 800 height 600;
                    int originalHeight = bitmap.getHeight();
                    int originalWidth = bitmap.getWidth();
                  //  int targetWidth = 800; // your arbitrary fixed limit
                    //int targetHeight = (int)(originalHeight * targetWidth / (double) originalWidth); // casts to avoid truncating

                     newBitmap = Bitmap.createScaledBitmap(bitmap, originalHeight, originalWidth, true);

                //    newBitmap = getResizedBitmap(bitmap, targetHeight, targetWidth);
                    newBitmap = rotateBitmap(newBitmap, ExifInterface.ORIENTATION_ROTATE_90);

                    bitmap.recycle();

                    if (newBitmap != null) {

                        //   mBitmap = codec(newBitmap, Bitmap.CompressFormat.JPEG, 60);

                        mBitmap = newBitmap;
                    }
                }
            } catch (IOException e) {
                // Error fetching image, try to recover

                /********* Cancel execution of this task. **********/
                cancel(true);
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void unused) {

            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (mBitmap != null) {
                // Set Image to ImageView
                if (photoClicked == true) {
                    int iBytes = mBitmap.getWidth() * mBitmap.getHeight() * 4;
                    //mpicture = new byte[iBytes];
                    //   Bitmap bJPGcompress = codec(mBitmap, Bitmap.CompressFormat.JPEG, 80);
                    mpicture = bitmapToByteArray(mBitmap);

                    Matrix matrix = new Matrix();
                    matrix.preRotate(270);
                    Bitmap rotatedBitMap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(),
                            matrix, true);
                    showImg.setImageBitmap(rotatedBitMap);



                    photoClicked = false;

                    mpictureFile = new File(getCapturedImageFilePath(imageUri));

                    try {
                        saveBitmapToImageFile(mpictureFile, mBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //     Log.d("What is photoClicked this Image ID", imageUri +"file path exists "+mpictureFile.exists() +"length"+ mpictureFile.length());

                    //m.setPicture ( mpicture );

                }
                if (photoAddressClicked == true) {


                    int iBytes = mBitmap.getWidth() * mBitmap.getHeight() * 4;
                    //mpictureProof = new byte[iBytes];

                    //  Bitmap bJPGcompressProof = codec(mBitmap, Bitmap.CompressFormat.JPEG, 80);
                    mpictureProof = bitmapToByteArray(mBitmap);

                    showImgAddress.setImageBitmap(mBitmap);
                    mpictureProofFile = new File(getCapturedImageFilePath(imageUri2));




                    try {
                        saveBitmapToImageFile(mpictureProofFile, mBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    //     Log.d("What is photoClicked this Image ID", imageUri2 +"file path exists "+mpictureProofFile.exists() +"length"+ mpictureProofFile.length());
                    photoAddressClicked = false;
                    //m.setPictureIDProof( mpictureProof);

                }

            }

        }

        //Create files and check if they are getting created;





    }


    public void onBrowse( View v)
    {

        Intent intent = new Intent(this, FilePicker.class);
        startActivityForResult(intent, REQUEST_PICK_FILE);
    }



    public static byte[] bitmapToByteArray(Bitmap bm) {
        // Create the buffer with the correct size
        int iBytes = bm.getWidth() * bm.getHeight() * 4;
        ByteBuffer buffer = ByteBuffer.allocate(iBytes);

        // Log.e("DBG", buffer.remaining()+""); -- Returns a correct number based on dimensions
        // Copy to buffer and then into byte array
        bm.copyPixelsToBuffer(buffer);
        // Log.e("DBG", buffer.remaining()+""); -- Returns 0
        return buffer.array();
    }

    private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
                                int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        byte[] array = os.toByteArray();
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }


    private void configure(Camera camera) {
        Camera.Parameters params = camera.getParameters();

        // Configure image format. RGB_565 is the most common format.
        List < Integer > formats = params.getSupportedPictureFormats();
        if (formats.contains(PixelFormat.RGB_565))
            params.setPictureFormat(PixelFormat.RGB_565);
        else
            params.setPictureFormat(PixelFormat.JPEG);

        // Choose the biggest picture size supported by the hardware
        List < Camera.Size > sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        params.setPictureSize(size.width, size.height);

  /*  List<String> flashModes = params.getSupportedFlashModes();
    if (flashModes.size() > 0)
        params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);

    // Action mode take pictures of fast moving objects
    List<String> sceneModes = params.getSupportedSceneModes();
    if (sceneModes.contains(Camera.Parameters.SCENE_MODE_ACTION))
        params.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);
    else
        params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);

    // if you choose FOCUS_MODE_AUTO remember to call autoFocus() on
    // the Camera object before taking a picture
    params.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);*/

        camera.setParameters(params);
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d("Camera Sign Up ", "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }



    public static void saveBitmapToImageFile(File outputFile, Bitmap bitmap) throws FileNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        try {
            if (outputFile.getName().endsWith(".jpg") || outputFile.getName().endsWith(".jpeg") || outputFile.getName().endsWith(".JPG") || outputFile.getName().endsWith(".JPEG")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {

        }
    }

}