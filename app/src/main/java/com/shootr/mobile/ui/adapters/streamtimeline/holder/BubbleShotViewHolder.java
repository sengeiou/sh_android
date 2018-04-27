package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
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
import com.shootr.mobile.ui.model.CardModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.CustomBubbleBaseMessageTextView;
import com.shootr.mobile.ui.widgets.ScalingImageView;
import com.shootr.mobile.ui.widgets.VideoFrameView;
import com.shootr.mobile.ui.widgets.VideoImageView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;

public class BubbleShotViewHolder extends BaseViewHolder<ShotModel> {

  private static final String EMPTY = "";

  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final AndroidTimeUtils timeUtils;
  private final ImageLoader imageLoader;
  private final NumberFormatUtil numberFormatUtil;

  @BindView(R.id.shot_avatar) AvatarView avatar;
  @BindView(R.id.shot_user_name) TextView name;
  @BindView(R.id.shot_image) ScalingImageView proportionalImageView;
  @BindView(R.id.shot_text) CustomBubbleBaseMessageTextView text;
  @BindView(R.id.shot_reply_count) TextView replyCount;
  @BindView(R.id.reshoot_action_text) TextView reshootActionText;
  @BindView(R.id.shot_video_frame) VideoFrameView videoFrame;
  @BindView(R.id.shot_video_title) TextView videoTitle;
  @BindView(R.id.shot_video_duration) TextView videoDuration;
  @BindView(R.id.video_image) VideoImageView videoImage;
  @BindView(R.id.shot_nice_button) ImageView niceButton;
  @BindView(R.id.shot_nice_count) TextView niceCount;
  @BindView(R.id.verified_user) ImageView verifiedUser;
  @BindView(R.id.holder_or_contributor_user) ImageView holderOrContributor;
  @BindView(R.id.open_menu) ImageView openImageMenu;
  @BindView(R.id.shot_timestamp) TextView timestamp;
  private View view;
  private ShotModel item;

  @BindString(R.string.menu_share_shot_via_shootr) String reshoootResource;
  @BindString(R.string.undo_reshoot) String undoReshootResource;
  @BindString(R.string.reshoot_action_text) String reshootActionTextResource;

  public BubbleShotViewHolder(View itemView, OnAvatarClickListener avatarClickListener,
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
    bindUserPhoto(shot);
    setupShotMediaContentVisibility(shot);
    bindImageInfo(shot, onLongClickListener, onTouchListener, onImageClickListener);
    bindVideoInfo(shot);
    bindNiceInfo(shot);
    bindReplyCount(shot);
    setupListeners(shot, shotClickListener, onShotLongClick, onReshootClickListener,
        onOpenShotMenuListener);
    setupShotActions(shot, onReshootClickListener);
    bindElapsedTime(shot);
    item = shot;
  }

  private void bindUsername(ShotModel shot) {
    String usernameTitle = shot.getUsername();
    if (shot.isReply()) {
      name.setText(getReplyName(shot));
    } else {
      name.setText(usernameTitle);
    }
  }

  private String getReplyName(ShotModel shot) {
    return view.getContext()
        .getString(R.string.reply_name_pattern, shot.getUsername(), shot.getReplyUsername());
  }

  protected void bindComment(ShotModel item, OnUrlClickListener onUrlClickListener) {
    String comment = item.getComment();
    if (comment != null && !comment.equals(EMPTY)) {
      addShotComment(this, comment, onUrlClickListener, item);
    } else {
      text.setVisibility(View.GONE);
    }
  }

  private void addShotComment(BubbleShotViewHolder vh, CharSequence comment,
      OnUrlClickListener onUrlClickListener, ShotModel shotModel) {
    vh.text.setMine(shotModel.isMine());
    vh.text.setBaseMessageModel(shotModel);
    vh.text.setOnUrlClickListener(onUrlClickListener);
    //vh.text.setText(comment);
    vh.text.addLinks(comment, shotModel.isMine());
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
      openImageMenu.setVisibility(View.VISIBLE);
    } else {
      openImageMenu.setVisibility(View.GONE);
    }

    if (!getImageUrl(shotModel).equals("")) {
      proportionalImageView.setVisibility(View.VISIBLE);
    } else {
      proportionalImageView.setVisibility(View.GONE);
    }
  }

  private void bindImageInfo(final ShotModel shot,
      final OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener) {
    String url = getImageUrl(shot);
    if (!url.isEmpty()) {
      setupProportionalImage(shot, onLongClickListener, onTouchListener, onImageClickListener,
          getImageUrl(shot));
    } else {
      proportionalImageView.setVisibility(View.GONE);
    }
  }

  private String getImageUrl(ShotModel shot) {
    String url = "";

    if (!shot.getEntitiesModel().getImages().isEmpty()) {
      url = shot.getEntitiesModel().getImages().get(0).getSizes().getHigh().getUrl();
    }

    return url;
  }

  private void setupProportionalImage(ShotModel shot, OnImageLongClickListener onLongClickListener,
      View.OnTouchListener onTouchListener, OnImageClickListener onImageClickListener,
      String imageUrl) {
    Long[] sizes = getImagesSizes(shot);
    proportionalImageView.setInitialHeight(sizes[1].intValue());
    proportionalImageView.setInitialWidth(sizes[0].intValue());
    proportionalImageView.setOnlyTopCorner(shot.getComment() != null);
    setupImage(proportionalImageView, shot, onLongClickListener, onTouchListener,
        onImageClickListener, imageUrl);
  }

  private Long[] getImagesSizes(ShotModel shot) {
    Long[] sizes = new Long[2];

    if (!shot.getEntitiesModel().getImages().isEmpty()) {
      sizes[0] = Long.valueOf(shot.getEntitiesModel().getImages().get(0).getSizes().getHigh().getWidth());
      sizes[1] = Long.valueOf(shot.getEntitiesModel().getImages().get(0).getSizes().getHigh().getHeight());
    }
    return sizes;
  }

  private void setupImage(ImageView imageView, final ShotModel shot,
      final OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      final OnImageClickListener onImageClickListener, String imageUrl) {
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

  public void bindReplyCount(final ShotModel shot) {
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

  private void setupShotActions(final ShotModel shot, final OnReshootClickListener onReshootClickListener) {
    if (shot.getTimelineFlags().contains(FlagsType.RESHOT)) {
      reshootActionText.setVisibility(View.VISIBLE);
      setupReshootState(shot);
      reshootActionText.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (shot.isReshooted()) {
            item.setReshooted(false);
            onReshootClickListener.onUndoReshootClick(shot);
          } else {
            item.setReshooted(true);
            onReshootClickListener.onReshootClick(shot);
          }
          setupReshootState(item);
        }
      });
    } else {
      reshootActionText.setVisibility(View.GONE);
    }
  }

  public void setupReshootState(ShotModel shot) {
    item = shot;
    if (shot.isReshooted()) {
      reshootActionText.setText(undoReshootResource);
    } else {
      reshootActionText.setText(reshoootResource);
    }
  }

  private void bindVideoInfo(final ShotModel shot) {

    if (!shot.getEntitiesModel().getCards().isEmpty()) {
      final CardModel cardModel = shot.getEntitiesModel().getCards().get(0);
      videoFrame.setVisibility(View.VISIBLE);
      videoFrame.setOnlyTopCorner(!text.getCurrenText().toString().isEmpty());
      videoImage.setOnlyTopCorner(!text.getCurrenText().toString().isEmpty());
      videoTitle.setText(cardModel.getTitle());
      videoDuration.setText(cardModel.getDuration());
      videoFrame.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          videoClickListener.onVideoClick(cardModel.getLink().getUrl());
        }
      });
      imageLoader.loadTimelineImage(cardModel.getImage().getSizes().getMedium().getUrl(), videoImage);
    } else {
      videoFrame.setVisibility(View.GONE);
      videoFrame.setOnClickListener(null);
    }
  }

  public void bindNiceInfo(final ShotModel shot) {
    niceButton.setVisibility(
        shot.getTimelineFlags().contains(FlagsType.NICE) ? View.VISIBLE : View.GONE);
    Integer nicesCount = shot.getNiceCount();
    if (nicesCount > 0) {
      setNiceCount(nicesCount);
    } else {
      this.niceCount.setVisibility(View.INVISIBLE);
    }

    niceButton.setImageDrawable(
        shot.isNiced() ? niceButton.getContext().getResources().getDrawable(R.drawable.ic_niced)
            : niceButton.getContext().getResources().getDrawable(R.drawable.ic_nice));
    niceButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (shot.isNiced()) {
          niceButton.setImageDrawable(niceButton.getContext().getResources().getDrawable(R.drawable.ic_nice));
          onNiceShotListener.unmarkNice(shot.getIdShot());
        } else {
          niceButton.setImageDrawable(niceButton.getContext().getResources().getDrawable(R.drawable.ic_niced));
          onNiceShotListener.markNice(shot);
        }
      }
    });
  }

  public void setNiceCount(Integer niceCount) {
    this.niceCount.setVisibility(niceCount > 0 ? View.VISIBLE : View.GONE);
    this.niceCount.setText(String.valueOf(niceCount));
  }

  private void setupVerified(ShotModel shot) {
    if (shot.getTimelineFlags().contains(FlagsType.VERIFIED)) {
      verifiedUser.setVisibility(View.VISIBLE);
    } else {
      verifiedUser.setVisibility(View.GONE);
    }
  }

  private void setupIsHolderOrContributor(ShotModel shot) {
    if (shot.getTimelineFlags().contains(FlagsType.IMPORTANT) &&
        !shot.getTimelineFlags().contains(FlagsType.VERIFIED)) {
      holderOrContributor.setVisibility(View.VISIBLE);
    } else {
      holderOrContributor.setVisibility(View.GONE);
    }
  }

  private void setupListeners(final ShotModel shot, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick,
      @Nullable final OnReshootClickListener reshootClickListener,
      final OnOpenShotMenuListener onOpenShotMenuListener) {
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        shotClickListener.onClick(shot);
      }
    });
    itemView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        onShotLongClick.onShotLongClick(item);
        return false;
      }
    });
    if (openImageMenu != null) {
      openImageMenu.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (onOpenShotMenuListener != null) {
            onOpenShotMenuListener.openMenu(item);
          }
        }
      });
    }
  }

  private void bindElapsedTime(ShotModel shot) {
    long shotTimestamp = shot.getBirth().getTime();
    if (timestamp != null) {
      this.timestamp.setText(timeUtils.getElapsedTime(view.getContext(), shotTimestamp));
    }
  }



}
