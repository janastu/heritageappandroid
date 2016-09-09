package org.janastu.heritageapp.geoheritagev2.client.fragments.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.FeatureCollection;
import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageCategory;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageGroup;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageLanguage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

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
      //  InputStream is  = ctx.openFileInput(UpdateAppInfoService.APP_FILENAME);
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
    public Set<HeritageCategory> getAllCategoriesForApp(String currApp) throws IOException
    {
        Set<HeritageCategory> setCategory = new HashSet<HeritageCategory>();

        HeritageAppDTO[] appDTOs =  this.getAllApps();
        HeritageAppDTO selectedApp = null;


        for(HeritageAppDTO app: appDTOs)
        {
            int res = app.getName().compareTo(currApp);
            if(res == 0)
            {
                selectedApp = app;
            }
        }
        if(selectedApp != null)
            return selectedApp.getCategorys();
        else
            return setCategory;


    }

    public Set<HeritageGroup>    getAllGroupsForAApp(String currApp) throws IOException
    {
        Set<HeritageGroup> setGroup  = new HashSet<HeritageGroup>();


        Set<HeritageCategory> setCategory = new HashSet<HeritageCategory>();

        HeritageAppDTO[] appDTOs =  this.getAllApps();
        HeritageAppDTO selectedApp = null;
        for(HeritageAppDTO app: appDTOs)
        {
            int res = app.getName().compareTo(currApp);
            if(res == 0)
            {
                selectedApp = app;
            }
        }
        if(selectedApp != null)
            return selectedApp.getGroups();
        else
            return setGroup;
    }

    public Set<HeritageLanguage>    getAllLanguagesForAApp(String currApp) throws IOException
    {
        Set<HeritageLanguage> setLanguages  = new HashSet<HeritageLanguage>();


        Set<HeritageLanguage> setCategory = new HashSet<HeritageLanguage>();

        HeritageAppDTO[] appDTOs =  this.getAllApps();
        HeritageAppDTO selectedApp = null;
        for(HeritageAppDTO app: appDTOs)
        {
            int res = app.getName().compareTo(currApp);
            if(res == 0)
            {
                selectedApp = app;
            }
        }
        if(selectedApp != null)
            return selectedApp.getLanguages();
        else
            return setLanguages;
    }

    @Override
    public FeatureCollection getAllFeatures() throws IOException {

        FeatureCollection features;
        ObjectMapper mapper = new ObjectMapper();
        //  String jsonAppString = settings.getString(MaterialMainActivity.PREFS_JSON_APPINFO, "");
        String jsonAppString ;
 //       InputStream is = ctx.getAssets().open("allmarkersmap.json");
        InputStream is  = ctx.openFileInput(UpdateAppInfoService.MARKER_FILENAME);
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


}
