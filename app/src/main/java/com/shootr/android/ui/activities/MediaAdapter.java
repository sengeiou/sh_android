package com.shootr.android.ui.activities;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.shootr.android.R;
import com.shootr.android.ui.model.ShotModel;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ShotModel> shotsWithMedia;

    public MediaAdapter(Context context, List<ShotModel> shotsWithMedia) {
        this.context = context;
        this.shotsWithMedia = shotsWithMedia;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View layoutView = LayoutInflater
          .from(viewGroup.getContext())
          .inflate(R.layout.event_media_layout, null);
        return new MyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ShotModel shotModel = shotsWithMedia.get(position);
        // Casting the viewHolder to MyViewHolder so I could interact with the views
        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        Picasso.with(context).load(shotModel.getImage()).into(myViewHolder.colorBlock);
    }

    @Override
    public int getItemCount() {
        return shotsWithMedia.size();
    }

    /** This is our ViewHolder class */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView colorBlock;

        public MyViewHolder(View itemView) {
            super(itemView); // Must call super() first
            //TODO aqui picasso
            colorBlock = (ImageView) itemView.findViewById(R.id.useLogo);

        }
    }
}
