package org.janastu.heritageapp.geoheritagev2.client.activity.fragments;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.RestServerComunication;
import org.janastu.heritageapp.geoheritagev2.client.db.DbTool;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.MapAppsServiceImpl;
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
import org.janastu.heritageapp.geoheritagev2.client.R;

/**
 * A placeholder fragment containing a simple view.


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabFragment1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapActivityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MAPFRAG";
    private int  position;
    private MapView mMapView;
    private IMapController mMapController;
    static HeritageAppDTO[] heritageAppDTO;
    // OSM Droid
    List < Feature > fList;
    ArrayList < OSMMarkerInfo > points;
    private CharSequence mTitle;
    View rootView;
    private String titlePopup, descPopup, urlPopup;
    private List<String> heritageCategoriesStringList;
    private String heritageRegion ="";
    GeoTagMediaDBHelper geoTagMediaDBHelper ;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MapAppsServiceImpl mapAppsService = new MapAppsServiceImpl();
    private OnFragmentInteractionListener mListener;

    public MapActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static MapActivityFragment newInstance(String param1, String param2) {
        MapActivityFragment fragment = new MapActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            position  = Integer.parseInt(mParam1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapview2);
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


            mapAppsService.setContext(getActivity().getApplicationContext());
            heritageAppDTO =  mapAppsService.getAllApps();
            d = heritageAppDTO[position];
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

            Set<HeritageRegionNameDTO> regionSet  = d.getRegions();
            List<String> regions = new ArrayList<String>();
            for(HeritageRegionNameDTO g:regionSet)
            {
                regions.add(g.getName() );

            }
            ArrayAdapter<String> dataAdapterGroups = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, regions);
            // Drop down layout style - list view with radio button
            dataAdapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            Spinner spinnerRegion = (Spinner) rootView.findViewById(R.id.spinnerRegion);
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
        return rootView;



    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class DrawGeoJSONOSM extends AsyncTask < Void, Void, List < OSMMarkerInfo >> {
        private ProgressDialog pdia;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity());
            pdia.setMessage("Fetching map info...");
            pdia.show();
        }


        @Override
        protected List < OSMMarkerInfo > doInBackground(Void...voids) {

            points  = new ArrayList < OSMMarkerInfo > ();

            try {
                // Load GeoJSON file
                //


                FeatureCollection fc  ;
                fc = mapAppsService.getAllFeatures();
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
        List<OSMMarkerInfo> result  = points;
        Log.d(TAG, "result2 points" +result.size());
        List<String> categories = CheckedHeritageCategoryMap.getFilteredCategories();
      //  List<OSMMarkerInfo> result = listContainsString(result2, categories);
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
                heritageLocationDrawable, getActivity() );

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
