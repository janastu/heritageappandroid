package org.janastu.heritageapp.geoheritagev2.client.materiallist;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.pojo.DownloadInfo;


//The adapters View Holder
public class View_Holder extends RecyclerView.ViewHolder {

    CardView cv;
    TextView title;
    TextView description;
    ImageView imageView;
    TextView downloadFileName;
    ProgressBar downloadProgressBar;
    Button downloadButton;
    Button button ,clearButton;


    View_Holder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView);
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        downloadProgressBar = (ProgressBar) itemView.findViewById(R.id.downloadProgressBar);
        button = (Button)itemView.findViewById(R.id.downloadButton);
        clearButton = (Button)itemView.findViewById(R.id.clearButton);
        downloadFileName = (TextView) itemView.findViewById(R.id.downloadFileName);
    }

}
