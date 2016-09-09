package org.janastu.heritageapp.geoheritagev2.client.fragments.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.geojson.FeatureCollection;
import org.janastu.heritageapp.geoheritagev2.client.RestServerComunication;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;
import org.janastu.heritageapp.geoheritagev2.client.rest.RestGroupComunication;

import java.io.FileOutputStream;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UpdateAppInfoService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "org.janastu.heritageapp.geoheritagev2.client.fragments.services.action.aupdateapp";
    private static final String ACTION_BAZ = "org.janastu.heritageapp.geoheritagev2.client.fragments.services.action.updatemarker";

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "org.janastu.heritageapp.geoheritagev2.client.fragments.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "org.janastu.heritageapp.geoheritagev2.client.fragments.services.extra.PARAM2";
    private static final String TAG = "APPSERV" ;
    public  static final  String APP_FILENAME = "heritageappJson";
    public  static final String MARKER_FILENAME = "heritagemarkerJson";
    public UpdateAppInfoService() {
        super("UpdateAppInfoService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, UpdateAppInfoService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, UpdateAppInfoService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);



        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle bundle = new Bundle();
        if (intent != null) {
            Log.d(TAG, "Starting service" );
            FeatureCollection collection =null ;
            final  ResultReceiver receiver = intent.getParcelableExtra("markreceiver");
            //
            try {
            HeritageAppDTO[]   appDTOs = RestGroupComunication.getAllApps( );
            String appJson = new Gson().toJson(appDTOs);
            //write json;
            Log.d(TAG, "all apps" + appJson);
            //write colllection

            FileOutputStream fosapp = openFileOutput(MARKER_FILENAME, Context.MODE_PRIVATE);
            fosapp.write(appJson.getBytes());
            fosapp.close();

            collection = RestServerComunication.getAllFeatures();
            String jsonInString="";

            ObjectMapper mapper = new ObjectMapper();
            jsonInString = mapper.writeValueAsString(collection);
            FileOutputStream fos = openFileOutput(MARKER_FILENAME, Context.MODE_PRIVATE);
            fos.write(jsonInString.getBytes());
            fos.close();
            Log.d(TAG, "all appMarkerJson" + jsonInString);

                receiver.send(STATUS_FINISHED, bundle);

                Log.d(TAG, "Service Stopping!");

            }catch(Exception e)
            {
                Log.e(TAG, "fetching error" + e);
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }


            if(collection != null) {
                bundle.putInt("size", collection.getFeatures().size());
            }
            else
            {
                bundle.putInt("size", 0);
            }

            this.stopSelf();

        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
