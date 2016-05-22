package org.janastu.heritageapp.geoheritagev2.client.pojo;

/**
 * Created by Graphics-User on 2/9/2016.
 */
public class Token {



    String token;
    long expires;

    public Token(String token, long expires){
        this.token = token;
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public long getExpires() {
        return expires;
    }
}
