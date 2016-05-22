package org.janastu.heritageapp.geoheritagev2.client.services;

import android.location.Location;

/**
 * Created by Graphics-User on 1/14/2016.
 */

public interface LocationResultListener {
    public void onLocationResultAvailable(Location location);
}