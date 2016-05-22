package org.janastu.heritageapp.geoheritagev2.client.services;

import android.location.Address;

public interface AddressResultListener {
    public void onAddressAvailable(Address address);
}
