package org.janastu.heritageapp.geoheritagev2.client.materiallist;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.activity.FileDownloadTask;
import org.janastu.heritageapp.geoheritagev2.client.activity.MaterialListUploadActivity;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.pojo.DownloadInfo;
import org.janastu.heritageapp.geoheritagev2.client.services.FileUploadStatusListener;

import java.io.File;
import java.util.Collections;
import java.util.List;


public class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> implements FileUploadStatusListener {

    private static final String TAG = "Recycler_View_Adapter";
    List<DownloadInfo> list = Collections.emptyList();
    MaterialListUploadActivity context;
    GeoTagMediaDBHelper geoTagMediaDBHelper;
    FileUploadStatusListener pointer;
    public Recycler_View_Adapter(List<DownloadInfo> list, MaterialListUploadActivity context) {
        this.list = list;
        this.context = context;
        this.pointer = this;

    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {

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




        holder.title.setText("Title - " + list.get(position).getTitle());
        holder.description.setText("Desc - "+list.get(position).getDescription());
        holder.downloadFileName.setText("File Name-"+uploadFile);
        boolean uploadStatus = list.get(position).getFileUploadstatus();
        Log.d(TAG, "uploadStatus of the file" + uploadStatus);
        if (uploadStatus == false) {
            holder.button.setText("Upload");


            holder.button.setEnabled(true);
            holder.button.setClickable(true);
            holder.button.setVisibility(View.VISIBLE);


            holder.clearButton.setText("  X");
            holder.clearButton.setEnabled(false);
            holder.clearButton.setClickable(false);
            holder.clearButton.setVisibility(View.INVISIBLE);
            holder.clearButton.invalidate();
        } else {
            holder.button.setText("  X");
            holder.button.setEnabled(false);
            holder.button.setClickable(false);
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

                button.setEnabled(false);
                button.invalidate();
                FileDownloadTask task = new FileDownloadTask(context,info, pointer);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                //show spinner to display upload
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

}
