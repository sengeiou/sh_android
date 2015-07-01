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
import com.shootr.android.util.PicassoWrapper;
import java.util.Collections;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private Context context;
    private List<ShotModel> shotsWithMedia = Collections.emptyList();
    private final PicassoWrapper picasso;

    public MediaAdapter(Context context, PicassoWrapper picasso) {
        this.context = context;
        this.picasso = picasso;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View layoutView = LayoutInflater
          .from(viewGroup.getContext())
          .inflate(R.layout.event_media_layout, viewGroup, false);
        return new ViewHolder(layoutView, new TimelineAdapter.VideoClickListener() {
            @Override
            public void onClick(String url) {
                onVideoClick(url);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final ShotModel shotModel = shotsWithMedia.get(position);
        ViewHolder mediaItemHolder = (ViewHolder) viewHolder;
        mediaItemHolder.shotModel = shotModel;
        picasso.load(shotModel.getImage()).into(mediaItemHolder.mediaImage);
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

        @InjectView(R.id.shot_video_frame) View videoFrame;
        @InjectView(R.id.shot_video_duration) TextView videoDuration;
        @InjectView(R.id.event_media_item) ImageView mediaImage;

        private TimelineAdapter.VideoClickListener videoClickListener;

        public ViewHolder(View itemView, final TimelineAdapter.VideoClickListener videoClickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
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
                        videoClickListener.onClick(shotModel.getVideoUrl());
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
