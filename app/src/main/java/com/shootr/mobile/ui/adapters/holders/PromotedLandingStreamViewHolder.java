package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemsClickListener;
import com.shootr.mobile.ui.model.PromotedLandingItemModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;

import static com.shootr.mobile.ShootrApplication.SCREEN_SIZE;

public class PromotedLandingStreamViewHolder extends BaseViewHolder<PromotedLandingItemModel> {

  private static final int STATE_GO = 0;
  private static final int STATE_FOLLOW = 1;

  @BindView(R.id.promoted_image) ImageView promotedImage;
  @BindView(R.id.card_subtitle) TextView cardSubtitle;
  @BindView(R.id.card_title) TextView cardTitle;
  @BindView(R.id.avatar) AvatarView avatar;
  @BindView(R.id.cta_button) TextView ctaButton;
  @BindString(R.string.promoted_card_subtitle) String promotedCardSubtitlePattern;
  @BindString(R.string.promoted_follow) String promotedFollow;
  @BindString(R.string.promoted_go) String promotedGo;

  private final PromotedItemsClickListener promotedItemsClickListener;
  private final ImageLoader imageLoader;
  private final NumberFormatUtil numberFormatUtil;
  private int currentState;

  public PromotedLandingStreamViewHolder(View itemView,
      PromotedItemsClickListener promotedItemsClickListener, ImageLoader imageLoader,
      NumberFormatUtil numberFormatUtil) {
    super(itemView);
    this.promotedItemsClickListener = promotedItemsClickListener;
    this.imageLoader = imageLoader;
    this.numberFormatUtil = numberFormatUtil;
    ButterKnife.bind(this, itemView);
  }

  public void render(PromotedLandingItemModel promotedModel) {
    int width = SCREEN_SIZE.x;
    ViewGroup.LayoutParams params = itemView.getLayoutParams();
    params.width = (int) (width * 0.94);
    itemView.setLayoutParams(params);
    cardTitle.setText(((StreamModel) promotedModel.getData()).getTitle());
    imageLoader.load(promotedModel.getImageMediaModel().getSizes().getHigh().getUrl(),
        promotedImage);
    imageLoader.loadProfilePhoto(((StreamModel) promotedModel.getData()).getPicture(), avatar,
        ((StreamModel) promotedModel.getData()).getTitle());
    setupCardSubtitle((StreamModel) promotedModel.getData());
    setupClicks(promotedModel);
    setupCtaButton(promotedModel);
  }

  private void setupCtaButton(final PromotedLandingItemModel promotedModel) {
    if (((StreamModel) promotedModel.getData()).isFollowing()) {
      ctaButton.setText(promotedGo);
      ctaButton.setVisibility(View.GONE);
      currentState = STATE_GO;
    } else {
      ctaButton.setVisibility(View.VISIBLE);
      ctaButton.setText(promotedFollow);
      currentState = STATE_FOLLOW;
    }

    ctaButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (currentState == STATE_FOLLOW) {
          promotedItemsClickListener.onFollowClick(promotedModel);
          ctaButton.setText(promotedGo);
          currentState = STATE_GO;
        } else if (currentState == STATE_GO) {
          promotedItemsClickListener.onGoClick(promotedModel);
        }
      }
    });
  }

  private void setupCardSubtitle(StreamModel stream) {
    String numViews = itemView.getContext()
        .getResources()
        .getQuantityString(R.plurals.view_count_pattern, ((int) stream.getViews()),
            numberFormatUtil.formatNumbers(stream.getViews()));

    cardSubtitle.setText(
        String.format(promotedCardSubtitlePattern, stream.getAuthorUsername(), numViews));
  }

  private void setupClicks(final PromotedLandingItemModel promotedModel) {
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        promotedItemsClickListener.onPromotedClick(promotedModel);
      }
    });
  }
}
