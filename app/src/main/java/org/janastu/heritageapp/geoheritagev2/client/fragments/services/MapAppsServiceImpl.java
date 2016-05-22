package org.janastu.heritageapp.geoheritagev2.client.fragments.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.FeatureCollection;
import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by DESKTOP on 5/21/2016.
 */
public class MapAppsServiceImpl  implements MapAppsService{

    Context ctx;
    public void setContext(Context c)
    { ctx = c; }
    @Override
    public void setCurrentApp(String currApp) {

    }

    @Override
    public HeritageAppDTO[] getAllApps() throws IOException {

        HeritageAppDTO[] heritageAppDTO;
        ObjectMapper mapper = new ObjectMapper();
      //  String jsonAppString = settings.getString(MaterialMainActivity.PREFS_JSON_APPINFO, "");
        String jsonAppString ;
        InputStream is = ctx.getAssets().open("appinfo.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String bufferString = new String(buffer);
        heritageAppDTO = mapper.readValue(bufferString, HeritageAppDTO[].class);
        Log.d("service imple", "heritageAppDTO2 reading from bufferString"+bufferString);

        Log.d("service imple", "heritageAppDTO2 reading from settnigs"+heritageAppDTO);

        return heritageAppDTO;
    }

    @Override
    public FeatureCollection getAllFeatures() throws IOException {

        FeatureCollection features;
        ObjectMapper mapper = new ObjectMapper();
        //  String jsonAppString = settings.getString(MaterialMainActivity.PREFS_JSON_APPINFO, "");
        String jsonAppString ;
        InputStream is = ctx.getAssets().open("allmarkersmap.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String bufferString = new String(buffer);
        features = (FeatureCollection) mapper.readValue(bufferString, FeatureCollection.class);
        Log.d("service imple", "features reading from bufferString"+bufferString);

        Log.d("service imple", "features reading from "+features);

        return features;
    }
/*
    if(isNetworkAvailable()) {
        heritageAppDTO = restGroupComunication.getAllApps(getActivity().getApplicationContext());
        Log.d(TAG, "RECEIVED FROM SERVER apps" + heritageAppDTO.length + heritageAppDTO.toString());


        SharedPreferences.Editor editor = settings.edit();

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(heritageAppDTO);
        editor.putString(MaterialMainActivity.PREFS_JSON_APPINFO, jsonInString);
        editor.commit();
    }
    else {
        SharedPreferences.Editor editor = settings.edit();
        ObjectMapper mapper = new ObjectMapper();
        String jsonAppString = settings.getString(MaterialMainActivity.PREFS_JSON_APPINFO, "");
        heritageAppDTO = mapper.readValue(jsonAppString, HeritageAppDTO[].class);
        Log.d(TAG, "heritageAppDTO2 reading from settnigs"+heritageAppDTO);

    }*/

}
