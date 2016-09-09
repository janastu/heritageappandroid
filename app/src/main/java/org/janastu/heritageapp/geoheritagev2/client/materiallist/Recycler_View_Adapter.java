package org.janastu.heritageapp.geoheritagev2.client.materiallist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.SimpleMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.activity.FileDownloadTask;
import org.janastu.heritageapp.geoheritagev2.client.activity.MaterialListUploadActivity;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.fragments.UploadFragment;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.DownloadResultReceiver;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.FileUploadService;
import org.janastu.heritageapp.geoheritagev2.client.pojo.AppConstants;
import org.janastu.heritageapp.geoheritagev2.client.pojo.DownloadInfo;
import org.janastu.heritageapp.geoheritagev2.client.services.FileUploadStatusListener;

import java.io.File;
import java.util.Collections;
import java.util.List;


public class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> implements FileUploadStatusListener {

    private static final String TAG = "Recycler_View_Adapter";
    List<DownloadInfo> list = Collections.emptyList();
    Context context;

    UploadFragment.OnUploadFragmentInteractionListener uploadListenerServicePtr;

    GeoTagMediaDBHelper geoTagMediaDBHelper;
    FileUploadStatusListener pointer;
    public Recycler_View_Adapter(List<DownloadInfo> list, Context context, UploadFragment.OnUploadFragmentInteractionListener uploadListener) {
        this.list = list;
        this.context = context;
        this.pointer = this;
        uploadListenerServicePtr = uploadListener;


    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, int position) {

        final DownloadInfo info  = new DownloadInfo();
        info.setDescription(list.get(position).getTitle());
        info.setDownloadState(list.get(position).getDownloadState());
        info.setId(list.get(position).getId());
        String  uploadFile =  (list.get(position).getUrlOrfileLink()) ;

        info.setUrlOrfileLink(uploadFile);
        String title =  list.get(position).getTitle() ;
        info.setTitle(title);
        String description = list.get(position).getDescription();
        info.setDescription(description);
        String category  = list.get(position).getHeritageCategory();
        info.setHeritageCategory(category);
        String language = list.get(position).getHeritageLanguage();
        info.setHeritageLanguage(language);

        String group  = list.get(position).getHeritageGroup();
        info.setHeritageGroup(group);

        String latitude  =  list.get(position).getLatitude();
        info.setLatitude(latitude);
        String longitude  = list.get(position).getLongitude();
        info.setLongitude(longitude);
        String mediatype = list.get(position).getMediaType();
        info.setMediaType(mediatype);

        Integer medInt = Integer.parseInt(mediatype);





        holder.title.setText("Title - " + list.get(position).getTitle());
        holder.description.setText("Desc - "+list.get(position).getDescription());
        holder.downloadFileName.setText("File Name-"+uploadFile);

        if(medInt == AppConstants.IMAGETYPE)
        {
            File imgFile = new  File(uploadFile);

            if(imgFile.exists()){
                try {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    holder.imageView.setImageBitmap(myBitmap);
                } catch(Exception e)
                {
                    Toast.makeText(context, " EXCEPTION SHOWING IMAGE - "+ e, Toast.LENGTH_SHORT).show();
                }
            }
        }
        boolean uploadStatus = list.get(position).getFileUploadstatus();
        Log.d(TAG, "uploadStatus of the file" + uploadStatus);
        if (uploadStatus == false) {
            holder.button.setText("Upload");


            holder.button.setEnabled(true);
            holder.button.setClickable(true);
            holder.button.setVisibility(View.VISIBLE);

            holder.downloadProgressBar.setProgress(0);

            holder.clearButton.setText("  X");
            holder.clearButton.setEnabled(false);
            holder.clearButton.setClickable(false);
            holder.clearButton.setVisibility(View.INVISIBLE);
            holder.clearButton.invalidate();
        } else {
            holder.button.setText("  X");
            holder.button.setEnabled(false);
            holder.button.setClickable(false);
            holder.downloadProgressBar.setProgress(100);
            holder.button.setVisibility(View.INVISIBLE);
            holder.button.invalidate();

            holder.clearButton.setEnabled(true);
            holder.clearButton.setClickable(true);
            holder.clearButton.setVisibility(View.VISIBLE);


            //holder.imageView.setImageResource(list.get(position).imageId);

            animate(holder);
        }


        ///////////////set status
        final int p = position;

        holder.button.setEnabled(list.get(position).getDownloadState() == DownloadInfo.DownloadState.NOT_STARTED);
        final Button button = holder.button;
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(p).getDownloadState();
                list.get(p).setDownloadState(DownloadInfo.DownloadState.QUEUED);

                holder.downloadProgressBar.setProgress(10);
                holder.downloadProgressBar.setMax(100);
                final DownloadInfo info2  = new DownloadInfo();

                info2.setId(list.get(p).getId());

                String title =  list.get(p).getTitle();
                info2.setTitle(title);

                String description = list.get(p).getDescription();
                info2.setDescription(description);


                String  uploadFile =  (list.get(p).getUrlOrfileLink()) ;
                info2.setUrlOrfileLink(uploadFile);

                String category  = list.get(p).getHeritageCategory();
                info2.setHeritageCategory(category);
                String language = list.get(p).getHeritageLanguage();
                info2.setHeritageLanguage(language);

                String group  = list.get(p).getHeritageGroup();
                info2.setHeritageGroup(group);



                info2.setDownloadState(list.get(p).getDownloadState());


                Log.d(TAG, "upload info" + info2);
                Log.d(TAG, "upload info file "+info2.getUrlOrfileLink());
                String latitude  =  list.get(p).getLatitude();
                info2.setLatitude(latitude);
                String longitude  = list.get(p).getLongitude();
                info2.setLongitude(longitude);
                String mediatype = list.get(p).getMediaType();
                info2.setMediaType(mediatype);


                button.setEnabled(false);
                button.invalidate();


                uploadListenerServicePtr.onUploadGeoMediaToServer(info2);
            }
        });


        holder.button.setEnabled(info.getDownloadState() == DownloadInfo.DownloadState.NOT_STARTED);
        final Button cButton = holder.clearButton;
        holder.clearButton.setOnClickListener(new View.OnClickListener() {
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

    }
    @Override
    public void uploadStatus(int status) {

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView
    public void insert(int position, DownloadInfo data) {
        list.add(position, data);
        notifyItemInserted(position);
    }
    // Remove a RecyclerView item containing the Data object
    public void remove(DownloadInfo data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
     //   final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipate_overshoot_interpolator);
       // viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    public void notifyDataStateChanged() {


    }

}
