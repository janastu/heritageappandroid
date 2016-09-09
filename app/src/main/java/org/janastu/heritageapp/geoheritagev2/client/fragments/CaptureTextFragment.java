package org.janastu.heritageapp.geoheritagev2.client.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.janastu.heritageapp.geoheritagev2.client.R;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.PlusOneButton;
import com.iceteck.silicompressorr.SiliCompressor;

import org.janastu.heritageapp.geoheritagev2.client.LoginResponse;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.activity.SettingsActivity;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.LoginService;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.LoginServiceImpl;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.MapAppsServiceImpl;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageCategory;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageGroup;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageLanguage;
import org.janastu.heritageapp.geoheritagev2.client.pojo.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.janastu.heritageapp.geoheritagev2.client.pojo.AppConstants;
import org.janastu.heritageapp.geoheritagev2.client.services.AddressResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationService;
import org.janastu.heritageapp.geoheritagev2.client.services.ReverseGeocodingService;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.MediaColumns;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link CaptureImageFragment.OnCaptureImageFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CaptureImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CaptureTextFragment extends Fragment implements View.OnClickListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    // TODO: Rename and change types of parameters
    private Double mParam1;
    private Double mParam2;
    private PlusOneButton mPlusOneButton;
    private static final String TAG = "CaptureImageFragment";
    String LOGGER ="CaptureImageFragment" ;
    EditText usernameTxtView, passwordTxtView;
    String username, password;
    TextView error;

    private EditText editTextTitle, editTextDescription;


    LocationService locationService;

    Button browseButton,storeButton;

    private static Location currentLocation;
    LoginService loginService;
    String heritageCategory  ;
    String heritageLanguage  ;
    String heritageGroup  ;
    private String selectedImagePath = "";
    final private int PICK_IMAGE = 1;
    final private int CAPTURE_IMAGE = 2;

    private String imgPath;
    GeoTagMediaDBHelper geoTagMediaDBHelper ;

    private OnCaptureTextFragmentInteractionListener mListener;
    Context             context;
    private TextView filePath;

    public CaptureTextFragment(  ) {
        // Required empty public constructor

    }


    // TODO: Rename and change types and number of parameters
    public static CaptureTextFragment newInstance(Location curLocation) {
        CaptureTextFragment fragment = new CaptureTextFragment(  );
        Bundle args = new Bundle();
        currentLocation = curLocation;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_capture_text, container, false);


//storeButton

        storeButton = (Button) view.findViewById(R.id.btnStoreToDB);
        storeButton.setOnClickListener(this);


        editTextTitle = (EditText) view.findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) view.findViewById(R.id.editTextDescription);
        initSpinnerAndMap(view);

        geoTagMediaDBHelper = new GeoTagMediaDBHelper(getActivity().getApplicationContext());
        return view;
    }

    private void initSpinnerAndMap(View view) {
// Spinner element
        Spinner spinnerCategory = (Spinner) view.findViewById(R.id.spinnerCategory);

        MapAppsServiceImpl mapservice = new MapAppsServiceImpl();
        mapservice.setContext(getActivity().getApplicationContext());
        Set<HeritageCategory> categSet  = null;
        Set<HeritageGroup> groupSet = null;
        Set<HeritageLanguage> langSet = null;
        try {
            categSet = mapservice.getAllCategoriesForApp(SettingsActivity.getCurrentApp());
            groupSet  = mapservice.getAllGroupsForAApp(SettingsActivity.getCurrentApp());
            langSet = mapservice.getAllLanguagesForAApp(SettingsActivity.getCurrentApp());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> categories = new ArrayList<String>();
        for(HeritageCategory c:categSet)
        {
            categories.add(c.getCategoryName());

        }

        ///

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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


        List<String> groups = new ArrayList<String>();

        for(HeritageGroup c:groupSet)
        {
            groups.add(c.getName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterGroups = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, groups);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        Spinner spinnerGroup = (Spinner) view.findViewById(R.id.spinnerGroup);
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

////

        List<String> languages = new ArrayList<String>();
        for(HeritageLanguage l : langSet)
        {
            languages.add(l.getHeritageLanguage());

        }
        Spinner spinnerLanguage = (Spinner) view.findViewById(R.id.spinnerLanguage);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterLanguages = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, languages);
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

        //
    }





    @Override
    public void onClick(View v) {
        Log.d(TAG, "btn clicked");
        switch (v.getId()) {


            case R.id.btnStoreToDB:
                Log.d(TAG, "btn btnStoreToDB");
                this.storeText();
                break;





        }
    }




    private void storeText() {

        Log.d(TAG, "storing into DB");

        String title = editTextTitle.getText().toString();

        String description =  editTextDescription.getText().toString();

        if(title == null || title.isEmpty() || description == null || description.isEmpty() )
        {
            Toast.makeText(getActivity().getApplicationContext(), " Pls fill in the Title/Description", Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentLocation == null)
        {
            Toast.makeText(getActivity().getApplicationContext(), " Unable to get the GPS Co-ordinates - Location is null - Check GPS ", Toast.LENGTH_SHORT).show();
            return;
        }
        Double latitude = (  currentLocation.getLatitude()  );
        Double longitude =  ( currentLocation.getLongitude() );
        if(latitude == null || longitude == null)
        {
            Toast.makeText(getActivity().getApplicationContext(), "Take Picture & Try - GPS Co-ordinates missing ", Toast.LENGTH_SHORT).show();
            return;
        }
        String address ="na";


        Integer mediaType = AppConstants.TEXTTYPE;
        // selectedImagePath;
       // String urlOrfileLink = compressImage();
        /*if(urlOrfileLink == null) {

            Toast.makeText(getActivity().getApplicationContext(), " Picture was not taken ", Toast.LENGTH_SHORT).show();
            return;
        }*/
        String urlOrfileLink = "";
//                long fileSize = mpictureFile.length();
        String consolidatedTags ="dd";


        Log.d(TAG, "storing into DB" +title+"title"+   description+"description"+   address+"address"+ "heritageGroup"+heritageGroup);
        long result = pushToDB(title, description, address, latitude,
                longitude, consolidatedTags, urlOrfileLink,
                heritageCategory, heritageLanguage, heritageGroup, mediaType, 100);
        if(result == -1)
        {
            Toast.makeText(getActivity().getApplicationContext(), "Storage Issue  ", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "Stored for Upload  ", Toast.LENGTH_SHORT).show();
            mListener.onCaptureTextFragmentInteraction(null);
           /*   Intent i = new Intent(getActivity().getApplicationContext(), getActivity() );
            startActivity(i);*/

        }
    }

    public long pushToDB(String title, String description, String address, Double latitude,
                         Double longitude, String consolidatedTags, String urlOrfileLink,
                         String heritageCategory, String heritageLanguage,  String heritageGroup, Integer mediaType, long fileSize)

    {

        if(longitude == null || latitude == null )
        {
            longitude = 0.0;
            latitude = 0.0;

        }
        long result = geoTagMediaDBHelper.insertWaypoint(title, description, address, latitude, longitude, consolidatedTags, urlOrfileLink, heritageCategory, heritageLanguage,heritageGroup, mediaType, fileSize);
        Log.d("Inserted", result + "Insert Way Point" + fileSize + urlOrfileLink);


        return result;
    }
    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
//        mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
       //  mListener.OnCaptureTextFragmentInteractionListener(uri);
        }
    }

    @Override
    public void onAttach(Context context) {


        super.onAttach(context);

        this.context = context;
        if (context instanceof OnCaptureTextFragmentInteractionListener) {
            mListener = (OnCaptureTextFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        this.context = null;
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
    public interface OnCaptureTextFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCaptureTextFragmentInteraction(Uri uri);
        public void onLoginSuccess(String username, String password, LoginResponse r, String d);
        public void onLoginFailure(LoginResponse result);
        public void onLoginDateStillValid();
    }

    ////Login Task


///location listener





}
