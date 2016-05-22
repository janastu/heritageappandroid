package org.janastu.heritageapp.geoheritagev2.client;


import java.io.Serializable;

public class LoginResponse implements Serializable {

    String token;
    String expires;

    public LoginResponse() {
    }

    public LoginResponse(String token, String expires) {
        this.token = token;
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", expires='" + expires + '\'' +
                '}';
    }
}
