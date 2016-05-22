package org.janastu.heritageapp.geoheritagev2.client.services;

import android.content.SharedPreferences;
import android.util.Log;

import org.janastu.heritageapp.geoheritagev2.client.LoginResponse;

import org.janastu.heritageapp.geoheritagev2.client.pojo.UserDetails;

/**
 * Created by DESKTOP on 3/11/2016.
 */
public class TokenValidator {

    private static final String TAG = "TokenValidator";

    //returns true if authenticated
    public static boolean   checkAuthenticationStatus(     String storedToken) {
        boolean result = false;

        if(storedToken == null || storedToken.isEmpty())
        {return false;
        }
        Log.d(TAG, "storedToken :: "+storedToken);
        String[] parts = storedToken.split(":");
        long expires = Long.parseLong(parts[1]);
        result = (  expires >= System.currentTimeMillis() );
        return result;
    }
}
