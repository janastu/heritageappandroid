package org.janastu.heritageapp.geoheritagev2.client.activity.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.MapAppsServiceImpl;
import org.janastu.heritageapp.geoheritagev2.client.pojo.CheckedHeritageCategoryMap;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageCategory;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageRegionNameDTO;
import org.janastu.heritageapp.geoheritagev2.client.pojo.OSMMarkerInfo;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationService;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class DefaultMapActivityFragment extends Fragment implements LocationResultListener,  View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MAPFRAG";
    private int  position;
    private MapView mMapView;
    private IMapController mMapController;
    private static Location currentLocation;
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
    private OnDefaultMapFragmentInteractionListener mListener;
    Button refreshButton;
    public DefaultMapActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment TabFragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static DefaultMapActivityFragment newInstance(final Location curLocation)
    {
        DefaultMapActivityFragment fragment = new DefaultMapActivityFragment();
        currentLocation = curLocation;
        return fragment;
    }

    public static void updateLocation(Location curLocation)
    {
        currentLocation = curLocation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.default_fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapview2);

        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        //MAPQUEST license no more public;
        //   mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapController = mMapView.getController();
            mMapController.setZoom(13);
        //mMapController.setCenter();
        try {


            mapAppsService.setContext(getActivity().getApplicationContext());
            GeoPoint gPt ;
            if(currentLocation != null) {
                gPt   = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                Toast.makeText(getActivity().getApplicationContext(), "GPS" + currentLocation.getLatitude() + ", " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            }
            else
            {
                gPt = new GeoPoint(12.922,77.671);//hard coding bang
            }

            mMapController.setCenter(gPt);
           // new DrawGeoJSONOSM(getActivity()).execute();
            startLoadMarkerTask();
            refreshButton = (Button) rootView.findViewById(R.id.refreshMap);
            refreshButton.setOnClickListener(this);


        }
        catch(Exception e)
        {
            Toast.makeText(getActivity().getApplicationContext(), "ERROR in setting geo center ", Toast.LENGTH_SHORT).show();

        }
        return rootView;



    }


    public void startLoadMarkerTask()
    {
        new DrawGeoJSONOSM(getActivity()).execute();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "btn clicked");
        switch (v.getId()) {
            case R.id.refreshMap:
        ///start a task to get the gro markers
                mListener.onDefaultMapFragmentInteraction(null);
                break;

        }
    }

    public void setCenterAndRedraw(final double aLatitude, final double aLongitude)
    {
        GeoPoint  gPt = new GeoPoint(aLatitude,aLongitude);
        mMapController.setCenter(gPt);
        //new DrawGeoJSONOSM(getActivity()).execute();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDefaultMapFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDefaultMapFragmentInteractionListener) {
            mListener = (OnDefaultMapFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDefaultMapFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLocationResultAvailable(Location location) {

        //need to change this ~


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
    public interface OnDefaultMapFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDefaultMapFragmentInteraction(Uri uri);
    }

    private class DrawGeoJSONOSM extends AsyncTask < Void, Void, List < OSMMarkerInfo >> {
        private final Context ctx;
        private ProgressDialog pdia;

        public DrawGeoJSONOSM(Context context)
        {
            this.ctx = context;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = new ProgressDialog(this.ctx);
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


            OSMMarkerInfo currentMarker = new OSMMarkerInfo();
            LngLatAlt currentP = new LngLatAlt();
            if(currentLocation != null) {

                Log.d(TAG, "currentlocation"+currentLocation.getLatitude() +"LONG"+currentLocation.getLongitude());

                currentP.setLatitude(currentLocation.getLatitude());
                currentP.setLongitude(currentLocation.getLongitude());
                currentMarker.setLatLngOSM(currentP);
                currentMarker.setDescription("CENTER");
                currentMarker.setMediaType("CENTER");
                points.add(currentMarker);
              }
            //AT THE END ass a marker with center variable
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

            else if (mediaType.contains("CENTER")) {
                heritageLocationDrawable = this.getResources().getDrawable(
                        R.drawable.pin);
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
        // add here I am here ;

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
