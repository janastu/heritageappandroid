package org.janastu.heritageapp.geoheritagev2.client.services;

import android.location.Address;
import android.location.Location;

public interface AddressResultListener {
    void onLocationResultAvailable(Location location);

    public void onAddressAvailable(Address address);
}
