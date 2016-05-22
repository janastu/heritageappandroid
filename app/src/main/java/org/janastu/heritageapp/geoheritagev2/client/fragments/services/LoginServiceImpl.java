package org.janastu.heritageapp.geoheritagev2.client.fragments.services;

import org.janastu.heritageapp.geoheritagev2.client.LoginResponse;
import org.janastu.heritageapp.geoheritagev2.client.RestServerComunication;

/**
 * Created by DESKTOP on 5/21/2016.
 */
public class LoginServiceImpl implements  LoginService
{


    @Override
    public LoginResponse login(String username, String password) {


        LoginResponse r;
        r = RestServerComunication.authenticate(username, password);
        return r;
    }
}
