package org.janastu.heritageapp.geoheritagev2.client.fragments.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import org.janastu.heritageapp.geoheritagev2.client.RestServerComunication;
import org.janastu.heritageapp.geoheritagev2.client.activity.SettingsActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUploadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "FileUploadService";


    String title ;
    String description  ;
    String category  ;
    String language  ;
    String group;
    String latitude  ;
    String longitude  ;
    String mediatype  ;
    Integer mediaInt ;
    String fileName;
    String userName; String appName;
    //addd 2 more fields
    //group


    public FileUploadService() {
        super(FileUploadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Service Started!");

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        Bundle bundle = new Bundle();
        int id = intent.getIntExtra("id", -1);
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        category = intent.getStringExtra("category");
        language = intent.getStringExtra("language");
        group = intent.getStringExtra("group");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        mediatype = intent.getStringExtra("mediatype");
        try {
            mediaInt = Integer.parseInt(mediatype);
        }catch(Exception e)
        {
            mediaInt = 0 ;
            bundle.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, bundle);

        }
        fileName =   intent.getStringExtra("fileName");
        appName = intent.getStringExtra("currentApp");
        userName = intent.getStringExtra("currentUser");

        ///currentUser
        ///
        Log.d(TAG, "uploading file"+ fileName);
        File uploadFile =  new File(fileName);

        //get the app
        // get the user

        RestServerComunication.setContext(FileUploadService.this);
        RestServerComunication.init();



        if (!TextUtils.isEmpty(title)) {
            /* Update UI: Download Service is Running */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {
                    boolean result = RestServerComunication.postSignInDataToserver2(FileUploadService.this, title, description, category, language, group, uploadFile, latitude, longitude, mediaInt,userName,appName);

                /* Sending result back to activity */
                if ( result == true) {
                    bundle.putInt("ID",id);
                    bundle.putBoolean("result", result);
                    receiver.send(STATUS_FINISHED, bundle);
                }

                else
                {
                    bundle.putInt("ID",id);
                    bundle.putBoolean("result", result);
                    receiver.send(STATUS_ERROR, bundle);

                }
            } catch (Exception e) {

                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    public class DownloadException extends Exception {

        public DownloadException(String message) {
            super(message);
        }

        public DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}