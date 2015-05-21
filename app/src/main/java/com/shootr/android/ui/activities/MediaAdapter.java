package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
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
        return new MediaItemHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ShotModel shotModel = shotsWithMedia.get(position);
        MediaItemHolder mediaItemHolder = (MediaItemHolder) viewHolder;
        mediaItemHolder.shotModel = shotModel;
        Picasso.with(context).load(shotModel.getImage()).into(mediaItemHolder.mediaImage);
    }

    @Override
    public int getItemCount() {
        return shotsWithMedia.size();
    }

    public static class MediaItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ShotModel shotModel;
        public ImageView mediaImage;

        public MediaItemHolder(View itemView) {
            super(itemView);
            mediaImage = (ImageView) itemView.findViewById(R.id.event_media_item);
            mediaImage.setOnClickListener(this);
        }

        @Override public void onClick(View view) {
            Intent intentForImage = PhotoViewActivity.getIntentForActivity(view.getContext(), shotModel.getImage());
            view.getContext().startActivity(intentForImage);
        }
    }
}
