package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.content.Context;
import android.util.Log;

import java.util.List;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.pojo.DownloadInfo;
import org.janastu.heritageapp.geoheritagev2.client.services.FileUploadStatusListener;


public class DownloadInfoArrayAdapter extends ArrayAdapter<DownloadInfo> implements FileUploadStatusListener {

    DownloadInfoArrayAdapter pointer;
    // Simple class to make it so that we don't have to call findViewById frequently
    private static class ViewHolder {
        TextView textView;
        TextView titletextView;
        TextView desctextView;
        ProgressBar progressBar;
        Button button;
        int status;
        Button clearButton;
        DownloadInfo info;
    }

    Context context;
    private static final String TAG = DownloadInfoArrayAdapter.class.getSimpleName();
    GeoTagMediaDBHelper geoTagMediaDBHelper;
    public DownloadInfoArrayAdapter(Context c, int textViewResourceId,
                                    List<DownloadInfo> objects) {


        super(c, textViewResourceId, objects);
        context = c;
        pointer  = this;
    }

    @Override
    public void uploadStatus(int status) {

        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final DownloadInfo info = getItem(position);
        // We need to set the convertView's progressBar to null.

        ViewHolder holder = null;

        if(null == row) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.file_download_row, parent, false);

            holder = new ViewHolder();
            holder.textView = (TextView) row.findViewById(R.id.downloadFileName);
            holder.titletextView = (TextView)row.findViewById(R.id.title);;
            holder.desctextView = (TextView)row.findViewById(R.id.desc);;
            holder.progressBar = (ProgressBar) row.findViewById(R.id.downloadProgressBar);
            holder.button = (Button)row.findViewById(R.id.downloadButton);
            holder.clearButton = (Button)row.findViewById(R.id.clearButton);


            holder.info = info;

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();

          //  holder.info.setProgressBar(null);
            holder.info = info;
            holder.info.setProgressBar(holder.progressBar);
        }
        holder.titletextView.setText(info.getTitle());
        holder.desctextView.setText(info.getDescription());
        holder.textView.setText(info.getUrlOrfileLink());
        boolean uploadStatus = info.getFileUploadstatus();
        Log.d(TAG, "uploadStatus of the file"+ uploadStatus);
        if(uploadStatus == false) {
            holder.button.setText("Upload");


             holder.button.setEnabled(true);
             holder.button.setClickable(true);
             holder.button.setVisibility(View.VISIBLE);


            holder.clearButton.setText("  X");
            holder.clearButton.setEnabled(false);
            holder.clearButton.setClickable(false);
            holder.clearButton.setVisibility(View.INVISIBLE);
            holder.clearButton.invalidate();
        }
        else {
             holder.button.setText("  X");
             holder.button.setEnabled(false);
             holder.button.setClickable(false);
             holder.button.setVisibility(View.INVISIBLE);
             holder.button.invalidate();

            holder.clearButton.setEnabled(true);
            holder.clearButton.setClickable(true);
            holder.clearButton.setVisibility(View.VISIBLE);



        }

        if(info.getProgress() != null)
        holder.progressBar.setProgress(info.getProgress());
        else
            holder.progressBar.setProgress(0);

        if(info.getFileSize() != null) {
            Log.d(TAG,"getFileSize not null"  + info.getFileSize());
            holder.progressBar.setMax(info.getFileSize());
        }
        else {
            holder.progressBar.setMax(150);
        }


        info.setProgressBar(holder.progressBar);

        holder.button.setEnabled(info.getDownloadState() == DownloadInfo.DownloadState.NOT_STARTED);
        final Button button = holder.button;
        holder.button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setDownloadState(DownloadInfo.DownloadState.QUEUED);
                button.setEnabled(false);
                button.invalidate();
              //  FileDownloadTask task = new FileDownloadTask(context,info, pointer);
               // task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                //show spinner to display upload
            }
        });


        holder.button.setEnabled(info.getDownloadState() == DownloadInfo.DownloadState.NOT_STARTED);
        final Button cButton = holder.clearButton;
        holder.clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(info != null) {
                    Log.d(TAG, "deleting ID" + info.getId());
                    geoTagMediaDBHelper = new GeoTagMediaDBHelper(context);
                    geoTagMediaDBHelper.deleteWaypoint( info.getId());
                    cButton.setEnabled(false);
                    cButton.invalidate();
                }
                else {
                    Log.d(TAG, "info is null ID");
                }


            }
        });

        //TODO: When reusing a view, invalidate the current progressBar.

        return row;
    }

}
