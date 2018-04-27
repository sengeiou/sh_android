package com.shootr.mobile.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.shootr.mobile.R;

public class BottomYoutubeVideoPlayer extends BottomSheetDialogFragment {

  private static final String API = "AIzaSyAamKWr6yMmLmhSsLvWA1cKOBYXPytC6_I";

  private FrameLayout playerContainer;

  private String videoId;
  private YouTubePlayerSupportFragment youTubePlayerSupportFragment;

  private VideoPlayerCallback playerCallback;
  private YouTubePlayer youTubePlayer;

  public BottomYoutubeVideoPlayer() {
    /* no-op */
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.youtube_player_fragment, container, false);

    playerContainer = (FrameLayout) fragmentView.findViewById(R.id.player);
    return fragmentView;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    renderExternalVideo(getVideoId());
  }

  void renderExternalVideo(final String videoId) {
    if (youTubePlayerSupportFragment == null) {
      youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
      FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
      transaction.add(R.id.player_fragment, youTubePlayerSupportFragment).commit();
    }

    youTubePlayerSupportFragment.initialize(API, new YouTubePlayer.OnInitializedListener() {
      @Override public void onInitializationSuccess(YouTubePlayer.Provider provider,
          YouTubePlayer player, boolean b) {
        youTubePlayer = player;
        player.loadVideo(videoId);
      }

      @Override public void onInitializationFailure(YouTubePlayer.Provider provider,
          YouTubeInitializationResult youTubeInitializationResult) {

      }
    });
  }

  public void setVideoPlayerCallback(
      BottomYoutubeVideoPlayer.VideoPlayerCallback videoPlayerCallback) {
    this.playerCallback = videoPlayerCallback;
  }

  public void setVideoId(String videoId) {
    this.videoId = videoId;
  }

  public String getVideoId() {
    return videoId;
  }

  @Override public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (this.playerCallback != null) {
      if(youTubePlayer != null) {
        youTubePlayer.release();
      }
      this.playerCallback.onDismiss();
    }
  }

  public interface VideoPlayerCallback {
    void onDismiss();
  }
}
