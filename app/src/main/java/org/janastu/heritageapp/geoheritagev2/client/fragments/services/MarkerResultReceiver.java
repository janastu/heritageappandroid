package org.janastu.heritageapp.geoheritagev2.client.fragments.services;

/**
 * Created by DESKTOP on 6/14/2016.
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class MarkerResultReceiver extends ResultReceiver {
    private MarkerReceiver mReceiver;

    public MarkerResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(MarkerReceiver receiver) {
        mReceiver = receiver;
    }

    public interface MarkerReceiver {
        public void onMarkerReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onMarkerReceiveResult(resultCode, resultData);
        }
    }
}

