package org.janastu.heritageapp.geoheritagev2.client;



import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.Point;
import org.janastu.heritageapp.geoheritagev2.client.db.DbTool;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
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
import org.janastu.heritageapp.geoheritagev2.client.activity.*;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.*;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MaterialMainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {



    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String PREFS_USERNAME = "username";
    public static final String PREFS_PASSWORD = "password";
    public static final String LOGIN_DATE = "logindate";

    public static final String PREFS_ACCESS_TOKEN = "access_token";
    public static final String PREFS_JSON_FEATURECOLLECTION = "geojsonstring";
    public static final String PREFS_JSON_APPINFO = "appinfo";
    private static final String TAG = "MaterialMainActivity";

    HeritageCategory[] heritageCategories;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     * Fragment managing the behaviors, iteractions and presentation of the navigation drawer.
     */
   private NavigationDrawerFragment mNavigationDrawerFragment;
    //  private MapView mapView = null;

    private MapView mMapView;
    private IMapController mMapController;

    private CharSequence mTitle;
    View rootView;
    private String titlePopup, descPopup, urlPopup;
    private List<String> heritageCategoriesStringList;
    private String heritageRegion ="";
    GeoTagMediaDBHelper geoTagMediaDBHelper = new GeoTagMediaDBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);
        setTitleColor(0X00000);


        ///diaplya map


        mMapView = (MapView) findViewById(R.id.mapview2);
        // mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);

        //   mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapController = mMapView.getController();
        mMapController.setZoom(13);



        HeritageAppDTO d = null;
        // Spinner Drop down elements
        try {
            d = SettingsActivity.getCurrentAppInfo();
            Set<HeritageCategory> categSet  = d.getCategorys();
            List<String> heritageCategoriesStringList = new ArrayList<String>();
            for(HeritageCategory c:categSet)
            {
                heritageCategoriesStringList.add(c.getCategoryName());

            }

            Set<HeritageRegionNameDTO>  rSet = d.getRegions();
           final List<HeritageRegionNameDTO> rlist = new ArrayList<HeritageRegionNameDTO>(rSet);
            HeritageRegionNameDTO firstRegion = rlist.get(0);
            GeoPoint gPt = new GeoPoint(firstRegion.getCenterLatitude(), firstRegion.getCenterLongitude());
            mMapController.setCenter(gPt);
            new DefaultResourceProxyImpl(this);
            new DrawGeoJSONOSM().execute();


            Set<HeritageRegionNameDTO> regionSet  = d.getRegions();
            List<String> regions = new ArrayList<String>();
            for(HeritageRegionNameDTO g:regionSet)
            {
                regions.add(g.getName() );

            }
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapterGroups = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, regions);
            // Drop down layout style - list view with radio button
            dataAdapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            Spinner spinnerRegion = (Spinner) findViewById(R.id.spinnerRegion);
            spinnerRegion.setAdapter(dataAdapterGroups);
            spinnerRegion.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                            heritageRegion = parent.getItemAtPosition(pos).toString();
                            Log.d(TAG, "selected heritageRegion "+heritageRegion.toString());     //prints the text in spinner item.

                            HeritageRegionNameDTO newRegion = null;

                            for(HeritageRegionNameDTO r :rlist)
                            {

                                if(r.getName().compareTo(heritageRegion) == 0 )
                                {
                                    newRegion = r;
                                    GeoPoint gPt = new GeoPoint(newRegion.getCenterLatitude(), newRegion.getCenterLongitude());
                                    mMapController.setCenter(gPt);
                                    //new DefaultResourceProxyImpl(this);
                                    new DrawGeoJSONOSM().execute();
                                }
                            }



                        }
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
        }
        catch(Exception e)
        {

        }





    }


    @Override
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
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }
        if(id == R.id.action_search){
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_capture_image) {
            Intent i = new Intent(getApplicationContext(), CaptureImageActivity2.class);
            startActivity(i);
        }
        if (id == R.id.action_capture_audio) {
            Intent i = new Intent(getApplicationContext(), CaptureAudioActivity.class);
            startActivity(i);
        }


        if (id == R.id.action_capture_video) {
            Intent i = new Intent(getApplicationContext(), CaptureVideoActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_capture_text) {
            Intent i = new Intent(getApplicationContext(), CaptureTextActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_upload) {
            Intent i = new Intent(getApplicationContext(), MaterialListUploadActivity.class);
            startActivity(i);
        }
        //on json and zip file;
        if (id == R.id.action_zip_json)  {

            String exportDir =  (Environment.getExternalStorageDirectory()                    + "/DCIM/" );
            File jsonFile = new File(exportDir,"mapheritage.json");
         //   File xmlFile = new File(exportDir,"mapheritage.xml");
            DbTool.db2gson(geoTagMediaDBHelper.getHandleDB(), jsonFile);
          //  DbTool.db2xml(geoTagMediaDBHelper.getHandleDB(), xmlFile);


            ArrayList<String> strList = geoTagMediaDBHelper.getAllFiles();

            strList.add(jsonFile.getAbsolutePath());
       //     strList.add(xmlFile.getAbsolutePath());
            String [] arrayFiles = new String[strList.size()];


            //arrayFiles
            Object[] objectList = strList.toArray();
            String[] stringArray =  Arrays.copyOf(objectList, objectList.length, String[].class);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            zip(stringArray, timeStamp+"_mapheritage.zip" );


        }


        if (id == R.id.action_logout)
        {
            SharedPreferences settings = getSharedPreferences(MaterialMainActivity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(MaterialMainActivity.PREFS_USERNAME, "");
            editor.putString(MaterialMainActivity.PREFS_ACCESS_TOKEN, "");
            editor.putString(MaterialMainActivity.PREFS_PASSWORD, "");
            editor.putString(MaterialMainActivity.LOGIN_DATE, "");
            editor.commit();


            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }

    /*    if(id == R.id.action_search){
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        if(heritageCategoriesStringList != null) {
            String selectedCategory = heritageCategoriesStringList.get(position);
            Log.d(TAG, "selectedCategory" + selectedCategory);
        }
        switch (position) {

            case 0:
             //   fragment = new HomeFragment();

                title = getString(R.string.title_home);
                break;
            case 1:
               // fragment = new FriendsFragment();
                title = getString(R.string.title_friends);
                break;
            case 2:
                //fragment = new MessagesFragment();
                title = getString(R.string.title_messages);
                break;
            default:
                break;
        }
    }
//refresh

    public void OnLogOut(View v) {
        Log.d(TAG, "clicked Refresh");
        SharedPreferences settings = getSharedPreferences(MaterialMainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(MaterialMainActivity.PREFS_JSON_FEATURECOLLECTION, "");
        editor.commit();
        new DrawGeoJSONOSM().execute();
    }

    ///
    int BUFFER = 1024;
    public void zip(String[] files, String zipFile) {


        String[] _files = files;
        String _zipFile = zipFile;

        try {
            BufferedInputStream origin = null;

            String exportDir =  (Environment.getExternalStorageDirectory()                    + "/DCIM/" );
            File f = new File(exportDir,_zipFile);
            FileOutputStream dest = new FileOutputStream(f);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.d("add:", _files[i]);
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));

                try {
                    out.putNextEntry(entry);
                }
                catch (ZipException e) {
                     //  ("Entry already exists");
                    //out.close();
                   // origin.close();
                    continue;
                }

                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        ////TASK

    ///end inner class
    private class GetCategoryTask extends AsyncTask < String, Void, HeritageCategory[] > {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MaterialMainActivity.this);
            mProgressDialog.setMessage(getResources()
                    .getString(R.string.login_progress_signing_in));
            mProgressDialog.show();
        }

        @Override
        protected HeritageCategory[] doInBackground(String...params) {
            User registered = null;
            HeritageCategory[] r = null;
            try {

                RestServerComunication.setContext(getApplicationContext());

                r = RestServerComunication.getAllCategories(getApplicationContext());

            } catch (Exception e) {
                Log.e("Get Category Task", "Error Retreiving  in: " + e.getMessage());
            }
            return r;
        }

        @Override
        protected void onPostExecute(HeritageCategory[] result) {
            mProgressDialog.dismiss();
            if (result != null) {
                heritageCategories = result;

                Toast.makeText(getApplicationContext(),
                        "Category Retreival Successful" + " ID: ", Toast.LENGTH_LONG).show();


            } else {
                heritageCategories = new HeritageCategory[5];
                heritageCategories[0] = new HeritageCategory();
                Toast.makeText(getApplicationContext(), "Category Retreival Failure ", Toast.LENGTH_LONG).show();
            }

    /*  textviewContent = new String[heritageCategories.length];
      for (int i = 0 ; i <heritageCategories.length;++i) {
          Log.d("ARRAY", "" + i + heritageCategories[i].toString());
          textviewContent[i] =  heritageCategories[i].getCategoryName();
      }*/
        }
    }
    //


    // OSM Droid
    List < Feature > fList;
    ArrayList < OSMMarkerInfo > points;

    private class DrawGeoJSONOSM extends AsyncTask < Void, Void, List < OSMMarkerInfo >> {
        private ProgressDialog pdia;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = new ProgressDialog(MaterialMainActivity.this);
            pdia.setMessage("Fetching map info...");
            pdia.show();
        }


        @Override
        protected List < OSMMarkerInfo > doInBackground(Void...voids) {

            points  = new ArrayList < OSMMarkerInfo > ();

            try {
                // Load GeoJSON file
                //

                SharedPreferences settings = getSharedPreferences(MaterialMainActivity.PREFS_NAME, 0);

                ObjectMapper mapper = new ObjectMapper();
                String featureJson = settings.getString(MaterialMainActivity.PREFS_JSON_FEATURECOLLECTION, "");
                FeatureCollection fc = new FeatureCollection();
                if(featureJson  == null || featureJson.isEmpty())
                {

                    RestServerComunication.setContext(getApplicationContext());
                    fc = RestServerComunication.getAllFeatures(getApplicationContext());
                    //write to string ;



                    String jsonInString = mapper.writeValueAsString(fc);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(MaterialMainActivity.PREFS_JSON_FEATURECOLLECTION, jsonInString);

                    Log.d(TAG, " writing jsonInString " + jsonInString);
                    editor.commit();


                }
                else
                {
                    String jsonString = settings.getString(MaterialMainActivity.PREFS_JSON_FEATURECOLLECTION, "");
                    fc = mapper.readValue(jsonString, FeatureCollection.class);

                    Log.d(TAG, " reading  jsonInString " + jsonString);
                }


                fList = fc.getFeatures();


//Object to JSON in file



                for (Feature temp: fList) {
                    Log.d(TAG, " properties " + temp.getProperties().toString() );

                    Point p = (Point) temp.getGeometry();
                    LngLatAlt coordinatesFromRest = p.getCoordinates();
                    OSMMarkerInfo m = new OSMMarkerInfo();
                    m.setLatLngOSM(coordinatesFromRest);
                    Map < String, Object > properties = temp.getProperties();
                    Object p1 = properties.get("description");
                    String p11 = (String) p1;
                    m.setDescription(p11);

                    Object p2 = properties.get("title");
                    String p22 = (String) p2;
                    m.setTitle(p22);

                    Object markerColorJson = properties.get("marker-color");
                    String markerColorJsonStr = (String) markerColorJson;
                    m.setMarkerColor(markerColorJsonStr);

                    Object urlJson = properties.get("url");
                    String urlJsonStr = (String) urlJson;
                    m.setUrl(urlJsonStr);

                    Object type = properties.get("mediatype");//mediatype
                    String typeStr = (String) type;
                    Log.d(TAG, "setting mediatype typeStr "+typeStr);
                    m.setMediaType(typeStr);


                    Object category = properties.get("category");
                    String categoryStr = (String) category;
                    Log.d(TAG, "setting category "+categoryStr);
                    m.setCategory(categoryStr);

                    Object language = properties.get("language");
                    String languageStr = (String) language;
                    m.setLanguauge(languageStr);


                    points.add(m);
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception GeoJSON: " + e);
            }
            return points;
        }

        @Override
        protected void onPostExecute(List < OSMMarkerInfo > points) {
            super.onPostExecute(points);
            pdia.dismiss();
            redrawPoints();
        }
    }



    ////
    public List<OSMMarkerInfo> listContainsString(List<OSMMarkerInfo> list, List<String> categories)
    {
        List<OSMMarkerInfo> filteredList = new ArrayList<OSMMarkerInfo>();

        Iterator<OSMMarkerInfo> iter = list.iterator();
        while(iter.hasNext())
        {
            OSMMarkerInfo s = iter.next();
            Log.d(TAG,"OSMMarkerInfo"+s.toString());

            if(s!= null ) {
                Log.d(TAG, "seacrhing for "+s.getCategory() +categories.toString());

                if (categories.contains(s.getCategory())) {
                    //return true;
                    Log.d(TAG, "adding for "+s.getCategory());
                    filteredList.add(s);
                }
                else
                {
                    Log.d(TAG, "adding unsuccesful category not found for  "+s.getCategory() +"OSM marker" +  s.getTitle());
                }
            }
            else
            {

                Log.d(TAG, "S is null");
            }
        }

        return filteredList;

    }

    public void redrawPoints()
    {
        List<OSMMarkerInfo> result2 = points;
        Log.d(TAG, "result2 points" +result2.size());
        List<String> categories = CheckedHeritageCategoryMap.getFilteredCategories();
        List<OSMMarkerInfo> result = listContainsString(result2, categories);
        //list contains the current app that is configured ;
        Log.d(TAG, "result points" +result.size());
        mMapView.invalidate();

        mMapView.getOverlays().clear();
        //getSelectedCategories();
        if (result.size() > 0) {
            Log.d(TAG, "Redrawing points");

            for (OSMMarkerInfo p: result) {

                showHeritagePoint(p);

                ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mMapView);
                mMapView.getOverlays().add(myScaleBarOverlay);
                mMapView.invalidate();
            }
        }
    }

    public static String replaceSpa(String str) {
        if(str == null )
        {
            return "";
        }
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ') {
                strBuffer.append("%20");
            } else {
                strBuffer.append(str.charAt(i));
            }
        }
        return strBuffer.toString();
    }

    public void showHeritagePoint(OSMMarkerInfo m) {

        Drawable heritageLocationDrawable = null;
        String mediaType = m.getMediaType();
        heritageLocationDrawable = this.getResources().getDrawable(
                R.drawable.mapicon);
        if(mediaType != null) {


            if (mediaType.contains("AUDIO")) {

                heritageLocationDrawable = this.getResources().getDrawable(
                        R.drawable.mapicon);
            } else if (mediaType.contains("VIDEO")) {
                heritageLocationDrawable = this.getResources().getDrawable(
                        R.drawable.mapvideoicon);
            } else if (mediaType.contains("IMAGE")) {
                heritageLocationDrawable = this.getResources().getDrawable(
                        R.drawable.mapimageicon);
            } else if (mediaType.contains("TEXT")) {
                heritageLocationDrawable = this.getResources().getDrawable(
                        R.drawable.mapicontext);
            }
        }
        else
        {
            heritageLocationDrawable = this.getResources().getDrawable(
                    R.drawable.mapicon);
        }


        //based on the marker-color

        MapItemizedOverlay itemizedoverlayForRestaurant = new MapItemizedOverlay(
                heritageLocationDrawable, this);

        m.getLatLngOSM().getLatitude();;
        GeoPoint myPoint1 = new GeoPoint(m.getLatLngOSM().getLatitude(), m.getLatLngOSM().getLongitude());

        // OverlayItem overlayitem2 = new OverlayItem(m.getTitle(),m.getDescription(),m.getUrl() , myPoint1);

        String url = m.getUrl();
        String encodedUrl = null;

        encodedUrl = replaceSpa(url);

        OverlayItem overlayitem2 = new OverlayItem("Title :- " + m.getTitle(), "Description :- " + m.getDescription(), "Url :- " + encodedUrl, myPoint1);

        itemizedoverlayForRestaurant.addOverlay(overlayitem2);

        mMapView.getOverlays().add(itemizedoverlayForRestaurant);
    }

    public class MapItemizedOverlay extends ItemizedOverlay < OverlayItem > {
        private ArrayList < OverlayItem > mOverlays = new ArrayList < OverlayItem > ();
        private Context mContext;

        public MapItemizedOverlay(Drawable defaultMarker, Context context) {
            // super(boundCenterBottom(defaultMarker));
            super(defaultMarker, new DefaultResourceProxyImpl(context));
            mContext = context;
        }

        public void addOverlay(OverlayItem overlay) {
            mOverlays.add(overlay);
            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return mOverlays.get(i);
        }

        @Override
        public int size() {
            return mOverlays.size();
        }

        @Override
        protected boolean onTap(int index) {
            OverlayItem item = mOverlays.get(index);

            Log.d("Title", item.getTitle());
            Log.d("Snippet", item.getSnippet());
            Log.d("Id", item.getUid());

            titlePopup = item.getTitle();
            descPopup = item.getSnippet();
            urlPopup = item.getUid();
            //set up dialog
            Dialog dialog = new Dialog(mContext);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.popupmap);
            //dialog.setTitle("This is my custom dialog box");

            dialog.setCancelable(true);
            //there are a lot of settings, for dialog, check them all out!
            item.getUid(); //textViewTitle

            TextView map_popup_body_cycle = (TextView) dialog.findViewById(R.id.textViewTitle);
            map_popup_body_cycle.setText(item.getUid());

            //map_popup_body_cycle.setText(Html.fromHtml("<a href="+item.getSnippet()+">"+item.getUid()+"</a>"));

            //set up text
            TextView map_popup_header = (TextView) dialog.findViewById(R.id.textViewDesc); //textViewDesc
            map_popup_header.setText(item.getTitle());

            TextView map_popup_body = (TextView) dialog.findViewById(R.id.textViewUrl);
            map_popup_body.setText(item.getSnippet());

            map_popup_body.setText(Html.fromHtml(item.getSnippet()));
            Linkify.addLinks(map_popup_body, Linkify.ALL);
            map_popup_body.setMovementMethod(LinkMovementMethod.getInstance());

            //now that the dialog is set up, it's time to show it
            dialog.show();

            return true;
        }



        @Override
        public boolean onSnapToItem(int i, int i1, android.graphics.Point point, IMapView iMapView) {
            return false;
        }
    }







}
