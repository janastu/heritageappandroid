package org.janastu.heritageapp.geoheritagev2.client.fragments.services;

import android.content.Context;

import org.geojson.FeatureCollection;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;

import java.io.IOException;
import java.util.List;

/**
 * Created by DESKTOP on 5/21/2016.
 */
public interface MapAppsService
{

   /* public String getApps();
    public String getAppinfo(String appName);
    public List<String> getAppCategories(String appName);
    public List<String> getAppMarkers(String appName);
    public List<String> getAllAppMarkers( );*/
    public void setCurrentApp(String currApp);
    //public List<String> getAppMarkers(String appName);
    HeritageAppDTO[]   getAllApps() throws IOException;
    FeatureCollection getAllFeatures() throws IOException;
}
