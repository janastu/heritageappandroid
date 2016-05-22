package org.janastu.heritageapp.geoheritagev2.client.fragments.services;

import org.janastu.heritageapp.geoheritagev2.client.LoginResponse;

/**
 * Created by DESKTOP on 5/21/2016.
 */
public interface LoginService {

    public LoginResponse login(String email, String password);
}
