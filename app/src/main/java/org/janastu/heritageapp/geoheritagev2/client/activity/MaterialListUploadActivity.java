package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.materiallist.Data;
import org.janastu.heritageapp.geoheritagev2.client.materiallist.Recycler_View_Adapter;
import org.janastu.heritageapp.geoheritagev2.client.pojo.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;


import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.widget.ListView;
import android.widget.Toast;

import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.pojo.DownloadInfo;


public class MaterialListUploadActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private Recycler_View_Adapter adapter;
    private static final String TAG = "MaterialListActivity";
    GeoTagMediaDBHelper geoTagMediaDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_list_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(getApplicationContext(), "Click on Upload to upload files to the server ", Toast.LENGTH_SHORT).show();

            }

            });


        geoTagMediaDBHelper = new GeoTagMediaDBHelper(getApplicationContext());

        List<DownloadInfo> data = getAllDownloadInfo();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
       // adapter = new Recycler_View_Adapter(data, this);
        //recyclerView.setAdapter(adapter);

        //recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
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



}

