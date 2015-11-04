package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.ImageLoader;
import java.util.Collections;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FOOTER = -1;
    public static final int TYPE_GENERIC_MEDIA = 0;
    private Context context;
    private List<ShotModel> shotsWithMedia = Collections.emptyList();
    private final ImageLoader imageLoader;
    private boolean showFooter = false;

    public MediaAdapter(Context context, ImageLoader imageLoader) {
        this.context = context;
        this.imageLoader = imageLoader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_FOOTER:
                return onCreateFooterViewHolder(viewGroup);
            case TYPE_GENERIC_MEDIA:
                return onCreateMediaViewHolder(viewGroup);
        }
        throw new IllegalStateException("View type %d not handled");
    }

    @NonNull private RecyclerView.ViewHolder onCreateMediaViewHolder(ViewGroup viewGroup) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(com.shootr.mobile.R.layout.stream_media_layout, viewGroup, false);
        return new ViewHolder(layoutView, new OnVideoClickListener() {
            @Override
            public void onVideoClick(String url) {
                videoClicked(url);
            }
        });
    }

    private RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        View footer = LayoutInflater.from(parent.getContext()).inflate(com.shootr.mobile.R.layout.item_list_loading, parent, false);
        return new RecyclerView.ViewHolder(footer) {
            /* no-op */
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!isFooter(position)) {
            final ShotModel shotModel = shotsWithMedia.get(position);
            ViewHolder mediaItemHolder = (ViewHolder) viewHolder;
            mediaItemHolder.shotModel = shotModel;
            imageLoader.load(shotModel.getImage(), mediaItemHolder.mediaImage);
            mediaItemHolder.bindMedia(shotModel);
        }
    }

    private void videoClicked(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public ShotModel getItem(int position) {
        return shotsWithMedia.get(position);
    }

    public Integer getCount() {
        return shotsWithMedia.size();
    }

    @Override
    public int getItemCount() {
        return showFooter ? shotsWithMedia.size() + 1 : shotsWithMedia.size();
    }

    public void setShotsWithMedia(List<ShotModel> shotsWithMedia) {
        this.shotsWithMedia = shotsWithMedia;
        notifyDataSetChanged();
    }

    public ShotModel getLastMedia() {
        return shotsWithMedia.get(getCount() - 1);
    }

    public void addShotsWithMedia(List<ShotModel> shotWithMedia) {
        this.shotsWithMedia.addAll(shotWithMedia);
        notifyDataSetChanged();
    }

    @Override public int getItemViewType(int position) {
        if (isFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_GENERIC_MEDIA;
        }
    }

    private boolean isFooter(int position) {
        return position >= shotsWithMedia.size();
    }

    public void setFooterVisible(Boolean visible) {
        showFooter = visible;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ShotModel shotModel;

        @Bind(R.id.shot_video_frame) View videoFrame;
        @Bind(com.shootr.mobile.R.id.shot_video_duration) TextView videoDuration;
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
            Intent intentForImage = PhotoViewActivity.getIntentForActivity(view.getContext(), shotModel.getImage());
            view.getContext().startActivity(intentForImage);
        }
    }
}
