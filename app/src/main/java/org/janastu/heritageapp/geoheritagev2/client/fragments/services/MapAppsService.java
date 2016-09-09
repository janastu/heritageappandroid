package org.janastu.heritageapp.geoheritagev2.client.fragments.services;

import android.content.Context;

import org.geojson.FeatureCollection;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageCategory;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageGroup;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageLanguage;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
    Set<HeritageCategory> getAllCategoriesForApp(String currApp) throws IOException;
    Set<HeritageGroup>    getAllGroupsForAApp(String currApp) throws IOException;
    Set<HeritageLanguage>    getAllLanguagesForAApp(String currApp) throws IOException;
    FeatureCollection getAllFeatures() throws IOException;
}
