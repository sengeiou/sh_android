package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.facebook.ads.NativeAd;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.widgets.AvatarView;
import java.util.ArrayList;
import java.util.List;

public class NativeContentAdViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.contentad_headline) TextView headline;
  @BindView(R.id.contentad_image) RoundedImageView contentImage;
  @BindView(R.id.contentad_body) TextView body;
  @BindView(R.id.contentad_call_to_action) TextView callToAction;
  @BindView(R.id.contentad_logo) AvatarView logo;
  @BindView(R.id.contentad_advertiser) TextView advertirser;
  @BindView(R.id.ad_container) RelativeLayout container;

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
    NativeAd.downloadAndDisplayImage(adImage, contentImage);

    List<View> clickableViews = new ArrayList<>();

    clickableViews.add(headline);
    clickableViews.add(logo);
    clickableViews.add(contentImage);
    clickableViews.add(callToAction);

    nativeContentAd.registerViewForInteraction(container, clickableViews);
  }
}
