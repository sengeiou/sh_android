package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.ImageLoader;
import java.util.Collections;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private Context context;
    private List<ShotModel> shotsWithMedia = Collections.emptyList();
    private final ImageLoader imageLoader;

    public MediaAdapter(Context context, ImageLoader imageLoader) {
        this.context = context;
        this.imageLoader = imageLoader;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View layoutView = LayoutInflater
          .from(viewGroup.getContext())
          .inflate(R.layout.stream_media_layout, viewGroup, false);
        return new ViewHolder(layoutView, new OnVideoClickListener() {
            @Override
            public void onVideoClick(String url) {
                onVideoClick(url);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final ShotModel shotModel = shotsWithMedia.get(position);
        ViewHolder mediaItemHolder = (ViewHolder) viewHolder;
        mediaItemHolder.shotModel = shotModel;
        imageLoader.load(shotModel.getImage(), mediaItemHolder.mediaImage);
        mediaItemHolder.bindMedia(shotModel);
    }

    private void onVideoClick(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return shotsWithMedia.size();
    }

    public void setShotsWithMedia(List<ShotModel> shotsWithMedia) {
        this.shotsWithMedia = shotsWithMedia;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ShotModel shotModel;

        @Bind(R.id.shot_video_frame) View videoFrame;
        @Bind(R.id.shot_video_duration) TextView videoDuration;
        @Bind(R.id.stream_media_item) ImageView mediaImage;

        private OnVideoClickListener videoClickListener;

        public ViewHolder(View itemView, final OnVideoClickListener videoClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.videoClickListener = videoClickListener;
        }

        public void bindMedia(final ShotModel shotModel){
            this.shotModel = shotModel;
            if(shotModel.hasVideo()){
                this.videoFrame.setVisibility(View.VISIBLE);
                this.videoDuration.setText(shotModel.getVideoDuration());
                this.videoFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoClickListener.onVideoClick(shotModel.getVideoUrl());
                    }
                });
            }else{
                this.videoFrame.setVisibility(View.GONE);
                bindVideoOrPicture();
            }
        }

        public void bindVideoOrPicture(){
            mediaImage.setOnClickListener(this);
        }

        @Override public void onClick(View view) {
            Intent intentForImage = PhotoViewActivity.getIntentForActivity(view.getContext(), shotModel.getImage(),
              false);
            view.getContext().startActivity(intentForImage);
        }
    }
}
