package org.janastu.heritageapp.geoheritagev2.client;

/**
 * Created by Ravi on 29/07/15.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.janastu.heritageapp.geoheritagev2.client.pojo.CheckedHeritageCategoryMap;

import java.util.Collections;
import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.mCheckBox.setChecked(true);

        CheckedHeritageCategoryMap.addToMap(current.getTitle(), true);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder   implements View.OnClickListener {
        private static final String  TAG ="MyViewHolder" ;
          TextView title;
          CheckBox mCheckBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            mCheckBox = (CheckBox) itemView.findViewById(
                    R.id.checkBox);


            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick mCheckBox " + getPosition() + " " +mCheckBox.isChecked()+ title.getText().toString());

                    CheckedHeritageCategoryMap.addToMap(title.getText().toString(), mCheckBox.isChecked());
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
}
