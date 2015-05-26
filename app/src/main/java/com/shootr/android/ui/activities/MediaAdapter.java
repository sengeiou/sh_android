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
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.TimelineAdapter;
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
        return new MediaItemHolder(layoutView, new TimelineAdapter.VideoClickListener() {
            @Override
            public void onClick(String url) {
                onVideoClick(url);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ShotModel shotModel = shotsWithMedia.get(position);
        MediaItemHolder mediaItemHolder = (MediaItemHolder) viewHolder;
        mediaItemHolder.shotModel = shotModel;
        Picasso.with(context).load(shotModel.getImage()).into(mediaItemHolder.mediaImage);
        mediaItemHolder.bindMedia(shotModel);
    }

    private void onVideoClick(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return shotsWithMedia.size();
    }

    public static class MediaItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ShotModel shotModel;
        public ImageView mediaImage;

        @InjectView(R.id.shot_video_frame) View videoFrame;
        @InjectView(R.id.shot_video_duration) TextView videoDuration;
        @InjectView(R.id.event_media_item_video) ImageView eventMediaItemVideo;

        private TimelineAdapter.VideoClickListener videoClickListener;

        public MediaItemHolder(View itemView, final TimelineAdapter.VideoClickListener videoClickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.videoClickListener = videoClickListener;
            mediaImage = (ImageView) itemView.findViewById(R.id.event_media_item);
        }

        public void bindMedia(final ShotModel shotModel){
            this.shotModel = shotModel;
            if(shotModel.hasVideo()){
                this.videoFrame.setVisibility(View.VISIBLE);
                this.videoDuration.setText(shotModel.getVideoDuration());
                this.videoFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.with(v.getContext()).load(shotModel.getImage()).into(eventMediaItemVideo);
                        videoClickListener.onClick(shotModel.getVideoUrl());
                    }
                });
            }else{
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
