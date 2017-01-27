package com.shootr.mobile.ui.adapters.holders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.AvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShareClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NicerTextSpannableBuilder;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import com.shootr.mobile.util.TimeFormatter;
import java.util.Date;

public class ShotDetailMainViewHolder extends RecyclerView.ViewHolder {

  public static final int NICES_TRESHOLD = 2;
  private Context context;

  @BindView(R.id.shot_detail_avatar) ImageView avatar;
  @BindView(R.id.shot_detail_user_name) TextView username;
  @BindView(R.id.verified_user) ImageView verifiedUser;
  @BindView(R.id.holder_or_contributor_user) ImageView holderOrContributor;
  @BindView(R.id.shot_detail_timestamp) TextView timestamp;
  @BindView(R.id.shot_detail_text) ClickableTextView shotText;
  @BindView(R.id.shot_detail_image) ImageView shotImage;
  @BindView(R.id.shot_detail_stream_title) TextView streamTitle;
  @BindView(R.id.shot_detail_parent_toggle) ImageView parentToggleButton;
  @BindView(R.id.shot_video_frame) View videoFrame;
  @BindView(R.id.shot_video_title) TextView videoTitle;
  @BindView(R.id.shot_video_duration) TextView videoDuration;
  @BindView(R.id.shot_nice_button) NiceButtonView niceButton;
  @BindView(R.id.shot_nice_count) TextView niceCount;
  @BindView(R.id.shot_nicers) ClickableTextView nicers;
  @BindView(R.id.shot_detail_pin_to_profile_container) LinearLayout pinToProfileContainer;
  @BindView(R.id.shot_view_count) TextView viewsCount;
  @BindView(R.id.shot_link_clicks_count) TextView linkClicksCount;
  @BindView(R.id.counts_dot) TextView countsDot;
  @BindView(R.id.reshoot_count) TextView reshotCount;
  @BindView(R.id.reshoot_container) LinearLayout reshootContainer;
  @BindView(R.id.external_share_container) LinearLayout externalShare;

  private final ImageLoader imageLoader;
  private final AvatarClickListener avatarClickListener;
  private final ShotClickListener streamClickListener;
  private final ShotClickListener imageClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnUsernameClickListener onUsernameClickListener;
  private final TimeFormatter timeFormatter;
  private final NumberFormatUtil numberFormatUtil;
  private final Resources resources;
  private final OnNiceShotListener onNiceShotListener;
  private final ShotClickListener onClickListenerPinToProfile;
  private final ShotClickListener nicesClickListener;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final NicerTextSpannableBuilder nicerTextSpannableBuilder;
  private final OnUrlClickListener onUrlClickListener;
  private final ShareClickListener reshootClickListener;
  private final ShareClickListener shareClickListener;

  public ShotDetailMainViewHolder(View itemView, ImageLoader imageLoader,
      AvatarClickListener avatarClickListener, ShotClickListener streamClickListener,
      ShotClickListener imageClickListener, OnVideoClickListener videoClickListener,
      OnUsernameClickListener onUsernameClickListener, TimeFormatter timeFormatter,
      NumberFormatUtil followsFormatUtil, Resources resources,
      OnNiceShotListener onNiceShotListener, ShotClickListener onClickListenerPinToProfile,
      ShotClickListener nicesClickListener, ShotTextSpannableBuilder shotTextSpannableBuilder,
      NicerTextSpannableBuilder nicerTextSpannableBuilder, OnUrlClickListener onUrlClickListener,
      ShareClickListener reshootClickListener, ShareClickListener shareClickListener) {
    super(itemView);
    this.imageLoader = imageLoader;
    this.avatarClickListener = avatarClickListener;
    this.streamClickListener = streamClickListener;
    this.imageClickListener = imageClickListener;
    this.videoClickListener = videoClickListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeFormatter = timeFormatter;
    this.numberFormatUtil = followsFormatUtil;
    this.resources = resources;
    this.onNiceShotListener = onNiceShotListener;
    this.onClickListenerPinToProfile = onClickListenerPinToProfile;
    this.nicesClickListener = nicesClickListener;
    this.shotTextSpannableBuilder = shotTextSpannableBuilder;
    this.nicerTextSpannableBuilder = nicerTextSpannableBuilder;
    this.onUrlClickListener = onUrlClickListener;
    this.reshootClickListener = reshootClickListener;
    this.shareClickListener = shareClickListener;
    ButterKnife.bind(this, itemView);
    context = itemView.getContext();
  }

  public void bindView(final ShotModel shotModel) {
    username.setText(getUsernameTitle(shotModel));
    timestamp.setText(getTimestampForDate(shotModel.getBirth()));
    setupVerified(shotModel);
    setupIsHolderOrContributor(shotModel);
    setupCounts(shotModel);
    setupComment(shotModel);
    showStreamTitle(shotModel);
    setupAvatar(shotModel);
    setupImage(shotModel);
    setupVideo(shotModel);
    setupReply(shotModel);
    setupNiceButton(shotModel);
    setupPinShotView(shotModel);
    setupPinToProfileContainer(shotModel);
    setupShareListener();
    setupStreamTitle(shotModel);
  }

  private void setupVerified(ShotModel shotModel) {
    if (shotModel.getParentShotId() == null && shotModel.isVerifiedUser()) {
      verifiedUser.setVisibility(View.VISIBLE);
    } else {
      verifiedUser.setVisibility(View.GONE);
    }
  }

  private void setupIsHolderOrContributor(ShotModel shotModel) {
    if (shotModel.getParentShotId() == null && shotModel.isHolderOrContributor()) {
      holderOrContributor.setVisibility(View.VISIBLE);
    } else {
      holderOrContributor.setVisibility(View.GONE);
    }
  }

  private void setupShareListener() {
    reshootContainer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        reshootClickListener.onClickListener();
      }
    });
    externalShare.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        shareClickListener.onClickListener();
      }
    });
  }

  private void setupCounts(ShotModel shotModel) {
    setupViewCount(shotModel);
    setupLinkClicksCount(shotModel);
    setupReshotCount(shotModel);
    setupCountDot(shotModel);
  }

  private void setupViewCount(ShotModel shotModel) {
    Long views = shotModel.getViews();
    if (views != 0) {
      viewsCount.setVisibility(View.VISIBLE);
      viewsCount.setText(context.getResources()
          .getQuantityString(R.plurals.view_count_pattern, views.intValue(),
              numberFormatUtil.formatNumbers(views)));
    }
  }

  private void setupLinkClicksCount(ShotModel shotModel) {
    Long clicks = shotModel.getLinkClickCount();
    if (clicks != 0) {
      linkClicksCount.setVisibility(View.VISIBLE);
      linkClicksCount.setText(context.getResources()
          .getQuantityString(R.plurals.link_click_count_pattern, clicks.intValue(),
              numberFormatUtil.formatNumbers(clicks)));
    }
  }

  private void setupReshotCount(ShotModel shotModel) {
    Long reshoots = shotModel.getReshootCount();
    if (reshoots != 0) {
      reshotCount.setVisibility(View.VISIBLE);
      reshotCount.setText(context.getResources()
          .getQuantityString(R.plurals.reshot_count_pattern, reshoots.intValue(),
              numberFormatUtil.formatNumbers(reshoots)));
    }
  }

  private void setupCountDot(ShotModel shotModel) {
    if (shotModel.getReshootCount() > 0 && shotModel.getViews() > 0) {
      countsDot.setVisibility(View.VISIBLE);
    }
  }

  private void setupReply(ShotModel shotModel) {
    if (shotModel.isReply()) {
      setReply();
    } else {
      parentToggleButton.setVisibility(View.GONE);
    }
  }

  private void setupVideo(ShotModel shotModel) {
    if (shotModel.hasVideo()) {
      setVideo(shotModel);
    } else {
      this.videoFrame.setVisibility(View.GONE);
      this.videoFrame.setOnClickListener(null);
    }
  }

  private void setupImage(ShotModel shotModel) {
    String imageUrl = shotModel.getImage().getImageUrl();
    if (imageUrl != null) {
      setImage(shotModel, imageUrl);
    } else {
      shotImage.setVisibility(View.GONE);
    }
  }

  private void setupComment(ShotModel shotModel) {
    String comment = shotModel.getComment();
    if (comment != null) {
      setComment(comment);
    } else if (shotModel.getCtaCaption() != null) {
      setComment(shotModel.getCtaCaption());
    } else {
      shotText.setVisibility(View.GONE);
    }
  }

  private void setupAvatar(final ShotModel shotModel) {
    imageLoader.loadProfilePhoto(shotModel.getAvatar(), avatar);
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        avatarClickListener.onClick(shotModel.getIdUser());
      }
    });
  }

  private void setupNiceButton(final ShotModel shotModel) {
    Integer nicesCount = shotModel.getNiceCount();
    if (nicesCount > 0) {
      setNiceCount(shotModel, nicesCount);
    } else {
      this.niceCount.setVisibility(View.GONE);
      this.nicers.setVisibility(View.GONE);
    }
    setNiceButton(shotModel);
  }

  private void setNiceButton(final ShotModel shotModel) {
    niceButton.setChecked(shotModel.isMarkedAsNice());
    niceButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (shotModel.isMarkedAsNice()) {
          onNiceShotListener.unmarkNice(shotModel.getIdShot());
        } else {
          onNiceShotListener.markNice(shotModel);
        }
      }
    });
  }

  private void setNiceCount(final ShotModel shotModel, Integer niceCount) {
    if (niceCount > NICES_TRESHOLD) {
      this.nicers.setVisibility(View.GONE);
      this.niceCount.setVisibility(View.VISIBLE);
      this.niceCount.setText(context.getResources()
          .getQuantityString(R.plurals.nice_count_pattern, niceCount, niceCount));
      this.niceCount.setTextColor(context.getResources().getColor(R.color.links));
      this.niceCount.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          nicesClickListener.onClick(shotModel);
        }
      });
    } else {
      this.nicers.setVisibility(View.VISIBLE);
      this.niceCount.setVisibility(View.GONE);
      setNicers(shotModel);
    }
  }

  private void setNicers(ShotModel shotModel) {
    if (shotModel.getNicers() != null) {
      String nicersText = context.getString(R.string.niced_by);
      for (String nicer : shotModel.getNicers()) {
        nicersText += nicer + ", ";
      }
      this.nicers.setText(
          nicerTextSpannableBuilder.formatWithUsernameSpans(nicersText, onUsernameClickListener));
    }
  }

  private void setComment(String comment) {
    CharSequence spannedComment =
        shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
    shotText.setText(spannedComment);
    shotText.addLinks(onUrlClickListener);
  }

  private void setImage(final ShotModel shotModel, String imageUrl) {
    shotImage.setVisibility(View.VISIBLE);
    imageLoader.loadTimelineImage(imageUrl, shotImage);
    shotImage.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        imageClickListener.onClick(shotModel);
      }
    });
  }

  private void setVideo(final ShotModel shotModel) {
    this.videoFrame.setVisibility(View.VISIBLE);
    this.shotImage.setVisibility(View.VISIBLE);
    this.videoTitle.setText(shotModel.getVideoTitle());
    this.videoDuration.setText(shotModel.getVideoDuration());
    this.videoFrame.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        videoClickListener.onVideoClick(shotModel.getVideoUrl());
      }
    });
  }

  private void setReply() {
    parentToggleButton.setVisibility(View.VISIBLE);
  }

  private String getUsernameTitle(ShotModel shotModel) {
    if (shotModel.isReply()) {
      return resources.getString(R.string.reply_name_pattern, shotModel.getUsername(),
          shotModel.getReplyUsername());
    } else {
      return shotModel.getUsername();
    }
  }

  private String getTimestampForDate(Date date) {
    return timeFormatter.getDateAndTimeDetailed(date.getTime());
  }

  private void showStreamTitle(final ShotModel shotModel) {
    String title = shotModel.getStreamTitle();
    if (title != null) {
      streamTitle.setText(shotModel.getStreamTitle());
      streamTitle.setVisibility(View.VISIBLE);
    } else {
      streamTitle.setVisibility(View.GONE);
    }
    streamTitle.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        streamClickListener.onClick(shotModel);
      }
    });
  }

  private void disableStreamTitle() {
    streamTitle.setTextColor(context.getResources().getColor(R.color.gray_material));
    streamTitle.setEnabled(false);
  }

  private void enableStreamTitle() {
    streamTitle.setTextColor(context.getResources().getColor(R.color.links));
    streamTitle.setEnabled(true);
  }

  private void setupStreamTitle(ShotModel shotModel) {
    if (shotModel.isTitleEnabled()) {
      enableStreamTitle();
    } else {
      disableStreamTitle();
    }
  }

  private void setupPinToProfileContainer(final ShotModel shotModel) {
    pinToProfileContainer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onClickListenerPinToProfile.onClick(shotModel);
      }
    });
  }

  private void setupPinShotView(final ShotModel shotModel) {
    if (shotModel.getCanBePinned()) {
      pinToProfileContainer.setVisibility(View.VISIBLE);
    } else {
      pinToProfileContainer.setVisibility(View.GONE);
    }
  }
}
