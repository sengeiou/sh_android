package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.FlagsType;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.CardModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.CustomBubbleBaseMessageTextView;
import com.shootr.mobile.ui.widgets.ScalingImageView;
import com.shootr.mobile.ui.widgets.VideoFrameView;
import com.shootr.mobile.ui.widgets.VideoImageView;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.PromotedColorManager;
import com.shootr.mobile.util.TimeFormatter;

public class BubbleShotDetailViewHolder extends BaseViewHolder<ShotModel> {

  private static final String EMPTY = "";

  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final TimeFormatter timeFormatter;
  private final ImageLoader imageLoader;
  private final NumberFormatUtil numberFormatUtil;
  private final OnUrlClickListener onUrlClickListener;
  private final OnImageClickListener onImageClickListener;

  @BindView(R.id.shot_avatar) AvatarView avatar;
  @BindView(R.id.shot_user_name) TextView name;
  @BindView(R.id.shot_image) ScalingImageView proportionalImageView;
  @BindView(R.id.shot_text) CustomBubbleBaseMessageTextView text;
  @BindView(R.id.shot_reply_count) TextView replyCount;
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
  @BindView(R.id.parent_separator) View parentSeparator;
  @BindView(R.id.shot_container) LinearLayout container;
  @BindView(R.id.shot_price) TextView shotPrice;

  private PromotedColorManager promotedColorManager;

  public BubbleShotDetailViewHolder(View itemView, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      TimeFormatter timeFormatter, ImageLoader imageLoader, NumberFormatUtil numberFormatUtil,
      OnUrlClickListener onUrlClickListener, OnImageClickListener onImageClickListener) {
    super(itemView);
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.timeFormatter = timeFormatter;
    this.imageLoader = imageLoader;
    this.numberFormatUtil = numberFormatUtil;
    this.onUrlClickListener = onUrlClickListener;
    this.onImageClickListener = onImageClickListener;
    ButterKnife.bind(this, itemView);
    promotedColorManager = new PromotedColorManager(itemView.getContext());
  }

  public void render(ShotModel shot) {
    bindUsername(shot);
    setupVerified(shot);
    setupIsHolderOrContributor(shot);
    bindComment(shot, onUrlClickListener);
    bindUserPhoto(shot);
    setupShotMediaContentVisibility(shot);
    bindImageInfo(shot, onImageClickListener);
    bindVideoInfo(shot);
    bindNiceInfo(shot);
    bindReplyCount(shot);
    bindElapsedTime(shot);
    setupParentSeparator(shot);
    setupBubbleBackground(shot);
    setupPrice(shot);
  }

  private void setupParentSeparator(ShotModel shot) {
    if (shot.getParentShotId() != null) {
      parentSeparator.setVisibility(View.VISIBLE);
    } else {
      parentSeparator.setVisibility(View.GONE);
    }
  }

  private void bindUsername(ShotModel shot) {
    String usernameTitle = shot.getUsername();
    name.setText(usernameTitle);
  }

  private void setupVerified(ShotModel shot) {
    if (shot.getDetailFlags().contains(FlagsType.VERIFIED)) {
      verifiedUser.setVisibility(View.VISIBLE);
    } else {
      verifiedUser.setVisibility(View.GONE);
    }
  }

  private void setupIsHolderOrContributor(ShotModel shot) {
    if (shot.getDetailFlags().contains(FlagsType.IMPORTANT) && !shot.getDetailFlags()
        .contains(FlagsType.VERIFIED)) {
      holderOrContributor.setVisibility(View.VISIBLE);
    } else {
      holderOrContributor.setVisibility(View.GONE);
    }
  }

  protected void bindComment(ShotModel item, OnUrlClickListener onUrlClickListener) {
    String comment = item.getComment();
    if (comment != null && !comment.equals(EMPTY)) {
      addShotComment(this, comment, onUrlClickListener, item);
    } else {
      text.setVisibility(View.GONE);
    }
  }

  private void addShotComment(BubbleShotDetailViewHolder vh, CharSequence comment,
      OnUrlClickListener onUrlClickListener, ShotModel shotModel) {
    vh.text.setMine(shotModel.isMine());
    vh.text.setBaseMessageModel(shotModel);
    vh.text.setOnUrlClickListener(onUrlClickListener);
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

  private void setupShotMediaContentVisibility(ShotModel shotModel) {
    if (shotModel.getDetailFlags().contains(FlagsType.OPEN_MENU)) {
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

  private void bindImageInfo(final ShotModel shot, OnImageClickListener onImageClickListener) {
    String url = getImageUrl(shot);
    if (!url.isEmpty()) {
      setupProportionalImage(shot, onImageClickListener, getImageUrl(shot));
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

  private void setupProportionalImage(ShotModel shot, OnImageClickListener onImageClickListener,
      String imageUrl) {
    Long[] sizes = getImagesSizes(shot);
    proportionalImageView.setInitialHeight(sizes[1].intValue());
    proportionalImageView.setInitialWidth(sizes[0].intValue());
    proportionalImageView.setOnlyTopCorner(shot.getComment() != null);
    setupImage(proportionalImageView, shot, onImageClickListener, imageUrl);
  }

  private Long[] getImagesSizes(ShotModel shot) {
    Long[] sizes = new Long[2];

    if (!shot.getEntitiesModel().getImages().isEmpty()) {
      sizes[0] =
          Long.valueOf(shot.getEntitiesModel().getImages().get(0).getSizes().getHigh().getWidth());
      sizes[1] =
          Long.valueOf(shot.getEntitiesModel().getImages().get(0).getSizes().getHigh().getHeight());
    }
    return sizes;
  }

  private void setupImage(ImageView imageView, final ShotModel shot,
      final OnImageClickListener onImageClickListener, String imageUrl) {
    imageLoader.loadTimelineImage(imageUrl, imageView);

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onImageClickListener.onImageClick(view, shot);
      }
    });
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
      imageLoader.loadTimelineImage(cardModel.getImage().getSizes().getMedium().getUrl(),
          videoImage);
    } else {
      videoFrame.setVisibility(View.GONE);
      videoFrame.setOnClickListener(null);
    }
  }

  public void bindNiceInfo(final ShotModel shot) {
    niceButton.setVisibility(
        shot.getDetailFlags().contains(FlagsType.NICE) ? View.VISIBLE : View.GONE);
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
          niceButton.setImageDrawable(
              niceButton.getContext().getResources().getDrawable(R.drawable.ic_nice));
          onNiceShotListener.unmarkNice(shot.getIdShot());
        } else {
          niceButton.setImageDrawable(
              niceButton.getContext().getResources().getDrawable(R.drawable.ic_niced));
          onNiceShotListener.markNice(shot);
        }
      }
    });
  }

  public void setNiceCount(Integer niceCount) {
    this.niceCount.setVisibility(niceCount > 0 ? View.VISIBLE : View.GONE);
    this.niceCount.setText(String.valueOf(niceCount));
  }

  private void bindElapsedTime(ShotModel shot) {
    long shotTimestamp = shot.getBirth().getTime();
    this.timestamp.setText(timeFormatter.getDateAndTimeDetailed(shotTimestamp));
  }

  private void setupBubbleBackground(ShotModel shot) {
    container.setBackground(promotedColorManager.getDrawableForPromoted(shot));
  }

  private void setupPrice(ShotModel shotModel) {
    String price = promotedColorManager.getPromotedPrice(shotModel);

    if (!price.isEmpty()) {
      shotPrice.setVisibility(View.VISIBLE);
      shotPrice.setText(price);
      shotPrice.setTextColor(promotedColorManager.getPriceColor(shotModel));
    } else {
      shotPrice.setVisibility(View.GONE);
    }
  }

}
