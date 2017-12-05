package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.widgets.AvatarView;
import java.util.List;

public class NativeContentAdViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.contentad_headline) TextView headline;
  @BindView(R.id.contentad_image) RoundedImageView contentImage;
  @BindView(R.id.contentad_body) TextView body;
  @BindView(R.id.contentad_call_to_action) TextView callToAction;
  @BindView(R.id.contentad_logo) AvatarView logo;
  @BindView(R.id.contentad_advertiser) TextView advertirser;
  @BindView(R.id.ad_container) NativeContentAdView adView;

  public NativeContentAdViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  public void render(final NativeContentAd nativeContentAd) {

    headline.setText(nativeContentAd.getHeadline());
    body.setText(nativeContentAd.getBody());
    advertirser.setText(nativeContentAd.getAdvertiser());

    List<NativeAd.Image> images = nativeContentAd.getImages();

    if (images.size() > 0) {
      contentImage.setImageDrawable(images.get(0).getDrawable());
    }

    NativeAd.Image logoImage = nativeContentAd.getLogo();

    if (logoImage != null) {
      logo.setImageDrawable(logoImage.getDrawable());
    }
    adView.setNativeAd(nativeContentAd);

    callToAction.setText(nativeContentAd.getCallToAction());

    adView.setCallToActionView(itemView);
  }
}
