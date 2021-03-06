package com.shootr.mobile.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
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

  @Override public Intent sharePollIntent(Activity activity, PollModel pollModel) {
    return intentFactory.sharePollIntent(activity, pollModel, localeProvider.getLocale());
  }

  @Override public Intent sharePollVotedIntent(Activity activity, PollModel pollModel,
      PollOptionModel pollOptionModel) {
    return intentFactory.sharePollVotedIntent(activity, pollModel, pollOptionModel,
        localeProvider.getLocale());
  }

  @Override public Intent shareProfileIntent(Activity activity, UserModel userModel) {
    return intentFactory.shareProfileIntent(activity, userModel);
  }
}
