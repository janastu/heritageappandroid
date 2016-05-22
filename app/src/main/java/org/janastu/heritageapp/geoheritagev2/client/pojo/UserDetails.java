package org.janastu.heritageapp.geoheritagev2.client.pojo;

/**
 * Created by Graphics-User on 2/9/2016.
 */
public class UserDetails {

    String userName;
    String password;

    public UserDetails(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getPassword(){return password;}

    /**
     * Returns the username used to authenticate the user. Cannot return <code>null</code>
     * .
     *
     * @return the username (never <code>null</code>)
     */
    public String getUsername(){return userName;}


}
