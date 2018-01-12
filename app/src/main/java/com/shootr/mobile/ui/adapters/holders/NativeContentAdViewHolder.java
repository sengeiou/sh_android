package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.facebook.ads.NativeAd;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.ProportionalImageView;
import java.util.ArrayList;
import java.util.List;

public class NativeContentAdViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.contentad_headline) TextView headline;
  @BindView(R.id.contentad_image) ProportionalImageView contentImage;
  @BindView(R.id.contentad_body) TextView body;
  @BindView(R.id.contentad_call_to_action) TextView callToAction;
  @BindView(R.id.contentad_logo) AvatarView logo;
  @BindView(R.id.contentad_advertiser) TextView advertirser;
  @BindView(R.id.ad_container) FrameLayout container;
  @BindView(R.id.promoted_icon) ImageView adChoiceImage;

  public NativeContentAdViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  public void render(final NativeAd nativeContentAd) {

    headline.setText(nativeContentAd.getAdTitle());
    body.setText(nativeContentAd.getAdBody());
    advertirser.setText(nativeContentAd.getAdTitle());

    NativeAd.Image adIcon = nativeContentAd.getAdIcon();
    NativeAd.downloadAndDisplayImage(adIcon, logo);

    NativeAd.Image adImage = nativeContentAd.getAdCoverImage();
    contentImage.setInitialHeight(adImage.getHeight());
    contentImage.setInitialWidth(adImage.getWidth());
    NativeAd.downloadAndDisplayImage(adImage, contentImage);

    NativeAd.Image adChoiceIcon = nativeContentAd.getAdChoicesIcon();
    NativeAd.downloadAndDisplayImage(adChoiceIcon, adChoiceImage);

    List<View> clickableViews = new ArrayList<>();

    clickableViews.add(headline);
    clickableViews.add(logo);
    clickableViews.add(contentImage);
    clickableViews.add(callToAction);
    clickableViews.add(body);
    clickableViews.add(advertirser);

    nativeContentAd.registerViewForInteraction(container, clickableViews);
  }
}
