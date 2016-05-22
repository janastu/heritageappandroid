package org.janastu.heritageapp.geoheritagev2.client.pojo;

/**
 * Created by Graphics-User on 1/29/2016.
 */

public class MediaResponse {

    //REGISTER_SUCCESS
    //USER_ID_EXISTS
    ///Once registered the
    String status;
    int code;
    String message;
    long mediaCreatedId;

    public long getMediaCreatedId() {
        return mediaCreatedId;
    }

    public void setMediaCreatedId(long mediaCreatedId) {
        this.mediaCreatedId = mediaCreatedId;
    }

    public MediaResponse() {
        super();
        // TODO Auto-generated constructor stub
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MResponseToken{" +
                "status='" + status + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                ",   mediaCreatedId;=" +   mediaCreatedId +
                '}';
    }
}
