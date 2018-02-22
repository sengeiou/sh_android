package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.FlagsType;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.CustomBaseMessageTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.ui.widgets.ProportionalImageView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;

public class ShotViewHolder  extends BaseViewHolder<ShotModel> {

  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final AndroidTimeUtils timeUtils;
  private final ImageLoader imageLoader;
  private final NumberFormatUtil numberFormatUtil;

  @BindView(R.id.shot_avatar) AvatarView avatar;
  @BindView(R.id.shot_user_name) TextView name;
  @BindView(R.id.verified_user) ImageView verifiedUser;
  @BindView(R.id.holder_or_contributor_user) ImageView holderOrContributor;
  @Nullable @BindView(R.id.shot_timestamp) TextView timestamp;
  @BindView(R.id.shot_text) CustomBaseMessageTextView text;
  @BindView(R.id.shot_image_landscape) ProportionalImageView proportionalImageView;
  @BindView(R.id.shot_video_frame) View videoFrame;
  @BindView(R.id.shot_video_title) TextView videoTitle;
  @BindView(R.id.shot_video_duration) TextView videoDuration;
  @BindView(R.id.shot_nice_button) NiceButtonView niceButton;
  @BindView(R.id.shot_nice_count) TextView niceCount;
  @BindView(R.id.shot_reply_count) TextView replyCount;
  @BindView(R.id.shot_container) View shotContainer;
  @BindView(R.id.shot_media_content) FrameLayout shotMediaContent;
  @BindView(R.id.open_menu) ImageView openImageMenu;
  @BindView(R.id.open_menu_container) FrameLayout openMenuContainer;
  @BindView(R.id.swipe) SwipeLayout swipeLayout;
  @BindView(R.id.actions) LinearLayout actionsContainer;
  @BindView(R.id.reshoot_action) LinearLayout reshootAction;
  @BindView(R.id.reshoot_action_text) TextView reshootActionText;
  @Nullable @BindView(R.id.reshoot_container) FrameLayout reshootContainer;
  @Nullable @BindView(R.id.reshoot) TextView reshootText;

  @BindString(R.string.menu_share_shot_via_shootr) String reshoootResource;
  @BindString(R.string.undo_reshoot) String undoReshootResource;
  @BindString(R.string.reshoot_action_text) String reshootActionTextResource;

  public int position;
  private View view;
  private ShotModel item;

  public ShotViewHolder(View itemView, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      AndroidTimeUtils timeUtils, ImageLoader imageLoader, NumberFormatUtil numberFormatUtil) {
    super(itemView);
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.timeUtils = timeUtils;
    this.imageLoader = imageLoader;
    this.numberFormatUtil = numberFormatUtil;
    this.view = itemView;
    ButterKnife.bind(this, view);
  }

  public void render(final ShotModel shot, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick, OnImageLongClickListener onLongClickListener,
      View.OnTouchListener onTouchListener, OnImageClickListener onImageClickListener,
      OnReshootClickListener onReshootClickListener, OnUrlClickListener onUrlClickListener,
      OnOpenShotMenuListener onOpenShotMenuListener) {
    bindUsername(shot);
    setupVerified(shot);
    setupIsHolderOrContributor(shot);
    bindComment(shot, onUrlClickListener);
    bindElapsedTime(shot);
    bindUserPhoto(shot);
    setupShotMediaContentVisibility(shot);
    bindImageInfo(shot, onLongClickListener, onTouchListener, onImageClickListener);
    bindVideoInfo(shot);
    bindNiceInfo(shot);
    bindReplyCount(shot);
    setupListeners(shot, shotClickListener, onShotLongClick, onReshootClickListener,
        onOpenShotMenuListener);
    setupSwipeLayout();
    setupShotActions(shot, onReshootClickListener);
    item = shot;
  }

  private void setupShotActions(final ShotModel shot, final OnReshootClickListener onReshootClickListener) {
    if (shot.getTimelineFlags().contains(FlagsType.RESHOT)) {
      actionsContainer.setVisibility(View.VISIBLE);
      setupReshootState(shot);
      reshootAction.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (shot.isReshooted()) {
            item.setReshooted(false);
            onReshootClickListener.onUndoReshootClick(shot);
          } else {
            item.setReshooted(true);
            onReshootClickListener.onReshootClick(shot);
          }
          setupReshootState(item);
          setupReshootSwipe(item);
        }
      });
    } else {
      actionsContainer.setVisibility(View.GONE);
    }
  }

  private void setupReshootState(ShotModel shot) {
    if (shot.isReshooted()) {
      reshootAction.setBackgroundDrawable(reshootAction.getResources().getDrawable(R.drawable.blue_round_layout));
      reshootActionText.setText(reshootActionTextResource);
      reshootActionText.setTextColor(reshootActionText.getResources().getColor(R.color.white));
    } else {
      reshootAction.setBackgroundDrawable(reshootAction.getResources().getDrawable(R.drawable.round_layout));
      reshootActionText.setText(reshoootResource);
      reshootActionText.setTextColor(reshootActionText.getResources().getColor(R.color.gray_50));
    }
  }

  private void setupSwipeLayout() {
    swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
    swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
  }

  private void setupListeners(final ShotModel shot, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick,
      @Nullable final OnReshootClickListener reshootClickListener,
      final OnOpenShotMenuListener onOpenShotMenuListener) {
    shotContainer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        shotClickListener.onClick(shot);
      }
    });
    shotContainer.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        onShotLongClick.onShotLongClick(shot);
        return false;
      }
    });
    setupSwipe(shot, reshootClickListener);
    if (openImageMenu != null) {
      openImageMenu.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (onOpenShotMenuListener != null) {
            onOpenShotMenuListener.openMenu(shot);
          }
        }
      });
    }
  }

  private void setupSwipe(final ShotModel shot,
      @Nullable final OnReshootClickListener reshootClickListener) {
    if (reshootContainer != null) {
      reshootContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          swipeLayout.close(true);
          if (reshootClickListener != null) {
            if (!shot.isReshooted()) {
              reshootClickListener.onReshootClick(shot);
            } else {
              reshootClickListener.onUndoReshootClick(shot);
            }
          }
        }
      });

      setupReshootSwipe(shot);
    }
  }

  private void setupReshootSwipe(ShotModel shot) {
    if (reshootContainer != null) {
      if (shot.isReshooted()) {
        reshootContainer.setBackgroundColor(
            reshootContainer.getResources().getColor(R.color.gray_material));
        if (reshootText != null) {
          reshootText.setText(undoReshootResource);
        }
      } else {
        reshootContainer.setBackgroundColor(
            reshootContainer.getResources().getColor(R.color.reshoot));
        if (reshootText != null) {
          reshootText.setText(reshoootResource);
        }
      }
    }
  }

  private void bindReplyCount(final ShotModel shot) {
    Long replies = shot.getReplyCount();
    if (replies > 0L) {
      replyCount.setVisibility(View.VISIBLE);
      replyCount.setText(replyCount.getResources()
          .getQuantityString(R.plurals.shot_replies_count_pattern, replies.intValue(),
              numberFormatUtil.formatNumbers(replies)));
    } else {
      replyCount.setVisibility(View.GONE);
    }
  }

  protected void bindComment(ShotModel item, OnUrlClickListener onUrlClickListener) {
    String comment = item.getComment();
    if (comment != null) {
      addShotComment(this, comment, onUrlClickListener, item);
      text.setVisibility(View.VISIBLE);
    } else {
      text.setVisibility(View.GONE);
    }
  }

  private void addShotComment(ShotViewHolder vh, CharSequence comment,
      OnUrlClickListener onUrlClickListener, ShotModel shotModel) {
    vh.text.setBaseMessageModel(shotModel);
    vh.text.setOnUrlClickListener(onUrlClickListener);
    vh.text.setText(comment);
    vh.text.addLinks();
  }

  private void bindUsername(ShotModel shot) {
    String usernameTitle = shot.getUsername();
    if (shot.isReply()) {
      name.setText(getReplyName(shot));
    } else {
      name.setText(usernameTitle);
    }
  }

  private void setupVerified(ShotModel shot) {
    if (shot.getTimelineFlags().contains(FlagsType.VERIFIED)) {
      verifiedUser.setVisibility(View.VISIBLE);
    } else {
      verifiedUser.setVisibility(View.GONE);
    }
  }

  private void setupIsHolderOrContributor(ShotModel shot) {
    if (shot.getTimelineFlags().contains(FlagsType.IMPORTANT)) {
      holderOrContributor.setVisibility(View.VISIBLE);
    } else {
      holderOrContributor.setVisibility(View.GONE);
    }
  }

  private String getReplyName(ShotModel shot) {
    return view.getContext()
        .getString(R.string.reply_name_pattern, shot.getUsername(), shot.getReplyUsername());
  }

  private void bindElapsedTime(ShotModel shot) {
    long shotTimestamp = shot.getBirth().getTime();
    if (timestamp != null) {
      this.timestamp.setText(timeUtils.getElapsedTime(view.getContext(), shotTimestamp));
    }
  }

  private void bindUserPhoto(final ShotModel shot) {
    imageLoader.loadProfilePhoto(shot.getAvatar(), avatar, shot.getUsername());
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        avatarClickListener.onAvatarClick(shot.getIdUser(), v);
      }
    });
  }

  private void setupShotMediaContentVisibility(ShotModel shotModel) {
    if (shotModel.getTimelineFlags().contains(FlagsType.OPEN_MENU)) {
      openMenuContainer.setVisibility(View.VISIBLE);
      openImageMenu.setVisibility(View.VISIBLE);
    } else {
      openMenuContainer.setVisibility(View.GONE);
      openImageMenu.setVisibility(View.GONE);
    }

    if (shotModel.hasVideo() || shotModel.hasMedia()) {
      shotMediaContent.setVisibility(View.VISIBLE);
    } else {
      shotMediaContent.setVisibility(View.GONE);
    }
  }

  private void bindImageInfo(final ShotModel shot,
      final OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener) {
    String url = getImageUrl(shot);
    if (!url.isEmpty()) {
      setupProportionalImage(shot, onLongClickListener, onTouchListener, onImageClickListener,
          getImageUrl(shot));
      shotMediaContent.setVisibility(View.VISIBLE);

    } else {
      proportionalImageView.setVisibility(View.GONE);
      shotMediaContent.setVisibility(View.GONE);
    }
  }

  private String getImageUrl(ShotModel shot) {
    String url = "";

    if (!shot.getEntitiesModel().getImages().isEmpty()) {
      url = shot.getEntitiesModel().getImages().get(0).getSizes().getMedium().getUrl();
    }

    return url;
  }

  private Long[] getImagesSizes(ShotModel shot) {
    Long[] sizes = new Long[2];

    if (!shot.getEntitiesModel().getImages().isEmpty()) {
      sizes[0] = Long.valueOf(shot.getEntitiesModel().getImages().get(0).getSizes().getMedium().getWidth());
      sizes[1] = Long.valueOf(shot.getEntitiesModel().getImages().get(0).getSizes().getMedium().getHeight());
    }
    return sizes;
  }

  private void setupProportionalImage(ShotModel shot, OnImageLongClickListener onLongClickListener,
      View.OnTouchListener onTouchListener, OnImageClickListener onImageClickListener,
      String imageUrl) {
    Long[] sizes = getImagesSizes(shot);
    proportionalImageView.setInitialHeight(sizes[1].intValue());
    proportionalImageView.setInitialWidth(sizes[0].intValue());
    setupImage(proportionalImageView, shot, onLongClickListener, onTouchListener,
        onImageClickListener, imageUrl);
  }

  private void setupImage(ImageView imageView, final ShotModel shot,
      final OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      final OnImageClickListener onImageClickListener, String imageUrl) {
    imageView.setVisibility(View.VISIBLE);
    imageLoader.loadTimelineImage(imageUrl, imageView);
    imageView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View view) {
        onLongClickListener.onImageLongClick(shot);
        return true;
      }
    });
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onImageClickListener.onImageClick(view, shot);
      }
    });
    imageView.setOnTouchListener(onTouchListener);
  }

  private void bindVideoInfo(final ShotModel shot) {
    if (shot.hasVideo()) {
      videoFrame.setVisibility(View.VISIBLE);
      videoTitle.setText(shot.getVideoTitle());
      videoDuration.setText(shot.getVideoDuration());
      videoFrame.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          videoClickListener.onVideoClick(shot.getVideoUrl());
        }
      });
    } else {
      videoFrame.setVisibility(View.GONE);
      videoFrame.setOnClickListener(null);
    }
  }

  private void bindNiceInfo(final ShotModel shot) {
    niceButton.setVisibility(
        shot.getTimelineFlags().contains(FlagsType.NICE) ? View.VISIBLE : View.GONE);
    Integer nicesCount = shot.getNiceCount();
    if (nicesCount > 0) {
      setNiceCount(nicesCount);
    } else {
      this.niceCount.setVisibility(View.INVISIBLE);
    }

    niceButton.setChecked(shot.isNiced());
    niceButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (shot.isNiced()) {
          onNiceShotListener.unmarkNice(shot.getIdShot());
        } else {
          onNiceShotListener.markNice(shot);
        }
      }
    });
  }

  public void setNiceCount(Integer niceCount) {
    this.niceCount.setVisibility(niceCount > 0 ? View.VISIBLE : View.GONE);
    this.niceCount.setText(String.valueOf(niceCount));
  }
}
