package org.janastu.heritageapp.geoheritagev2.client.services;

/**
 * Created by DESKTOP on 3/9/2016.
 */
public interface FileUploadStatusListener {


    public void uploadStatus(int status);//status - 0 - started //status - 1 - complete / -1 - failed
}
