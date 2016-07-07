package com.shootr.mobile.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import javax.inject.Inject;

public class ShareManagerUtil implements ShareManager {

  private final LocaleProvider localeProvider;
  private final IntentFactory intentFactory;

  @Inject public ShareManagerUtil(LocaleProvider localeProvider, IntentFactory intentFactory) {
    this.localeProvider = localeProvider;
    this.intentFactory = intentFactory;
  }

  @Override public Intent shareShotIntent(Activity activity, ShotModel shotModel) {
    return intentFactory.shareShotIntent(activity, shotModel, localeProvider.getLocale());
  }

  @Override public Intent shareStreamIntent(FragmentActivity activity, StreamModel streamModel) {
    return intentFactory.shareStreamIntent(activity, streamModel, localeProvider.getLocale());
  }
}
