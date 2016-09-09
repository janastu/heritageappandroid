package org.janastu.heritageapp.geoheritagev2.client.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.materiallist.Recycler_View_Adapter;
import org.janastu.heritageapp.geoheritagev2.client.pojo.DownloadInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import org.janastu.heritageapp.geoheritagev2.client.R;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.plus.PlusOneButton;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadFragment.OnUploadFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerView;
    private Recycler_View_Adapter adapter;
    private static final String TAG = "UploadFragment";
    GeoTagMediaDBHelper geoTagMediaDBHelper;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LinearLayout progressLayout;
    private OnUploadFragmentInteractionListener mListener;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_upload, container, false);




        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        geoTagMediaDBHelper = new GeoTagMediaDBHelper(getActivity().getApplicationContext());
        List<DownloadInfo> data = getAllDownloadInfo();
        Context context = getActivity().getApplicationContext();

        adapter = new Recycler_View_Adapter(data, context, this.mListener);
        recyclerView.setAdapter(adapter);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onUploadFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUploadFragmentInteractionListener) {
            mListener = (OnUploadFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUploadFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public List<DownloadInfo> getAllDownloadInfo() {

        List<DownloadInfo> downloadInfo = new ArrayList<DownloadInfo>();

        String name;
        Cursor cursor = geoTagMediaDBHelper.getAllWaypoint();

        int count = geoTagMediaDBHelper.numberOfRows();

        for (int i = 0 ; i <= count ; i++)
        {

            Cursor c = geoTagMediaDBHelper.getWaypoint(i);

            if(cursor.getCount() > 0) {

                cursor.moveToFirst();
                String filename = cursor.getString(cursor.getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_FILE_NAME));
                Log.d(TAG, "file name" + filename);
            }
        }

        if (cursor .moveToFirst()) {

            while (cursor.isAfterLast() == false) {


                Integer id = cursor.getInt(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_ID));

                String title = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_TITLE));

                String latitude =  cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_LATITUDE));
                String longitude =  cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_LONGITUDE));
                String urlOrfileLink = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_FILE_NAME));

                String desc = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_DESCRPITION));
                String heritageCategory = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_CATEGORY));
                String heritageLanguage = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_LANGUAGE));
                String heritageGroup = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_GROUP));
                Log.d(TAG, "heritageGroup retreived"+heritageGroup);
                String  media_type = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_MEDIA_TYPE));
                Log.d(TAG, "media_type retreived"+media_type);
                Integer fileSize = cursor.getInt(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_FILE_SIZE));

                boolean fileUploadstatus = (cursor.getInt(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_UPLOAD_STATUS) )== 1);


                DownloadInfo d =  new DownloadInfo( id, title,   desc,   latitude,   longitude ,  urlOrfileLink ,   media_type,fileUploadstatus );
                d.setHeritageCategory(heritageCategory);
                d.setHeritageLanguage(heritageLanguage);
                d.setmFileSize(fileSize);
                d.setHeritageGroup(heritageGroup);

                downloadInfo.add(d);
                cursor.moveToNext();
            }
        }
        return downloadInfo;
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
    ProgressDialog myPd_bar;
    public void startProgress()
    {

//        this.progressLayout.setVisibility(View.VISIBLE);

        myPd_bar=new ProgressDialog(getActivity());
        myPd_bar.setMessage("Uploading....");
        myPd_bar.setTitle("Please Wait..");
        myPd_bar.setProgressStyle(myPd_bar.STYLE_HORIZONTAL);
        myPd_bar.setProgress(0);
        myPd_bar.setMax(100);
        myPd_bar.show();
    }


    public void stopProgress()
    {
        myPd_bar.setProgress(100);
        myPd_bar.dismiss();
  //      this.progressLayout.setVisibility(View.GONE);
    }
    public interface OnUploadFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onUploadFragmentInteraction(Uri uri);
        public void onUploadGeoMediaToServer(DownloadInfo info);
        public Location getCurrentLocation();
    }
}
