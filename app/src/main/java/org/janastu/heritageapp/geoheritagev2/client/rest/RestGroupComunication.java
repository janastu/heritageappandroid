package org.janastu.heritageapp.geoheritagev2.client.rest;

/**
 * Created by Graphics-User on 1/18/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Picture;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import org.geojson.FeatureCollection;
import org.janastu.heritageapp.geoheritagev2.client.ImageGeoTagHeritageEntityDTO;
import org.janastu.heritageapp.geoheritagev2.client.LoginResponse;

import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.pojo.AppConstants;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageCategory;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageCategoryListPojo;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageGroup;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageLanguage;
import org.janastu.heritageapp.geoheritagev2.client.pojo.MResponseToken;
import org.janastu.heritageapp.geoheritagev2.client.pojo.MediaResponse;
import org.janastu.heritageapp.geoheritagev2.client.pojo.RestReturnCodes;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//

public class RestGroupComunication {

    private static final String GET_ALL_LANGUAGES = "/api/heritageLanguages2";
    private static final String GET_ALL_APP_CONFIG = "/api/getAppConfigInfo/app/"; //heritageweb/api/getAppConfigInfo/app/PondyHeritageMap
    private static final String REGISTER_USER = "/api/registerForMobile";
    private static final String GET_ALL_APPS = "/api/heritageAppsMob";
    static String serverUrl;
    static Context context;
    static String userName;
    static String password;



    final static String TAG = "RestGroupComunication";

    //final static String UPLOAD_SERVER_URI = "http://10.0.2.2:8080/"; //http:///192.168.56.1/"
    // final static String UPLOAD_SERVER_URI = "http://192.168.56.1:8080"; //http:///192.168.56.1/"
    //final static String UPLOAD_SERVER_URI = "http://192.168.1.19:8082/heritageweb/"; //http:///192.168.56.1/"
   // final static String UPLOAD_SERVER_URI = "http://192.168.1.97:8080/"; //http:///192.168.56.1/"
       final static String UPLOAD_SERVER_URI = "http://pondy.openrun.com:8080/heritageweb/";
    //final static String UPLOAD_SERVER_URI = "http://pondy.openrun.com:8080/heritageweb/";
    private static String SIGN_IN = "/api/imageGeoTagHeritageFromMobile";
    private static String LOG_IN = "/api/authenticate";
    ;
    private static String GET_IMAGE_FOR_ID = "/api/imageGeoTagHeritageEntitysPost/1";

    private static final String GET_ALLCATEGORY = "/api/heritageCategorys2";



    private static final String GET_ALL_USER_GROUPS = "/api/getUserGroups/user";

    private static final String GET_ALL_TAGS = "api/allGeoTagHeritageEntitysGeoJson";//allGeoTagHeritageEntitysGeoJson

    private static final String UPLOAD_CREATE_MEDIA = "api/createAnyMediaGeoTagHeritageFromMobile";
    public RestGroupComunication(Context context) {
        this.context = context;
    }

    public static void setContext(Context c)
    {context = c; }

    public static void init()
    {

        SharedPreferences settings = context.getSharedPreferences(MaterialMainActivity.PREFS_NAME, 0);

        userName  = settings.getString(MaterialMainActivity.PREFS_USERNAME , "");
        password = settings.getString(MaterialMainActivity.PREFS_PASSWORD , "");
        String currentToken = settings.getString(MaterialMainActivity.PREFS_ACCESS_TOKEN , "");

        Log.d(TAG, "RestServerComunication SharedPreferences userName"+userName);
        Log.d(TAG, "RestServerComunication SharedPreferences password" + password);
        Log.d(TAG, "RestServerComunication SharedPreferences currentToken"+currentToken);
    }

    public static LoginResponse authenticate(String userNameVar, String passwordVar) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", userNameVar);
        map.add("password", passwordVar);
        Log.d(TAG, "logging with " + "username" + userNameVar + "password" + passwordVar);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + LOG_IN);

        URI url = builder.build().toUri();

        LoginResponse response = null;
        try {

            response = (LoginResponse) restTemplate.postForObject(url, request, LoginResponse.class);
            return response;

        } catch (Exception e) {

            response = new LoginResponse();
            response.setToken("LOGFAIL - " + e);
        }

        return response;


    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static HeritageCategory[] getAllCategories(Context c)

    {

      //  HeritageCategoryListPojo heritageCategoryListPojo;
        LoginResponse response = authenticate( userName,   password);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Set headers to include multiform


////////////////////////////////////


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("x-auth-token", response.getToken());

        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);


        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + GET_ALLCATEGORY);

        URI url = builder.build().toUri();

        HeritageCategory[] array = (HeritageCategory[]) restTemplate.postForObject(url, request, HeritageCategory[].class);


        Log.d(TAG, "From Server received category objects: - " + array.length);

        return array;

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static HeritageLanguage[] getAllLanguages(Context c)

    {

        HeritageCategoryListPojo heritageCategoryListPojo;
        LoginResponse response = authenticate( userName,   password);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Set headers to include multiform


////////////////////////////////////


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("x-auth-token", response.getToken());

        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);


        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + GET_ALL_LANGUAGES);

        URI url = builder.build().toUri();

        HeritageLanguage[] array = (HeritageLanguage[]) restTemplate.postForObject(url, request, HeritageLanguage[].class);


        Log.d(TAG, "From Server received HeritageLanguage objects: - " + array.length);

        return array;

    }
//get all apps
    //get config info for all the maps -

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static HeritageAppDTO getAllConfigInfo (String appname)

    {

        HeritageCategoryListPojo heritageCategoryListPojo;
        LoginResponse response = authenticate( userName,   password);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Set headers to include multiform


////////////////////////////////////


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("x-auth-token", response.getToken());

        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);


        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + GET_ALL_APP_CONFIG + appname);

        URI url = builder.build().toUri();

        HeritageAppDTO  appObject = (HeritageAppDTO ) restTemplate.postForObject(url, request,  HeritageAppDTO.class);


        Log.d(TAG, "From Server received HeritageAppDTO objects: - " +appObject );

        return appObject;

    }



    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static HeritageGroup[] getAllGroups(Context c)

    {

        //  HeritageGroupListPojo heritageCategoryListPojo;
        LoginResponse response = authenticate( userName,   password);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Set headers to include multiform


////////////////////////////////////


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("x-auth-token", response.getToken());

        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);


        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + GET_ALL_USER_GROUPS +"/"+userName );

        URI url = builder.build().toUri();

        HeritageGroup[] array = (HeritageGroup[]) restTemplate.postForObject(url, request, HeritageGroup[].class);


        Log.d(TAG, "From Server received group objects: - " + array.length);

        return array;

    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static   HeritageAppDTO[]   getAllApps( )
    {

      /*  Log.d(TAG, "getAllApps Server call --s:");
        LoginResponse response = authenticate( userName,   password);*/
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Set headers to include multiform


////////////////////////////////////


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

      //  headers.set("x-auth-token", response.getToken());

        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);


        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + GET_ALL_APPS);

        URI url = builder.build().toUri();

        HeritageAppDTO[] array = null;

        try {
            array = (HeritageAppDTO[]) restTemplate.postForObject(url, request, HeritageAppDTO[].class);
        }catch(Exception e)
        {
            Log.d(TAG, " Escepiont gettig HeritageAppDTO     : - "  + e);
        }



        Log.d(TAG, "From Server received group objects: - " + array.length);

        return array;

    }



}
