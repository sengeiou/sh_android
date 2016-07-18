package com.shootr.mobile.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;

public interface ShareManager {
  Intent shareShotIntent(Activity activity, ShotModel shotModel);

  Intent shareStreamIntent(FragmentActivity activity, StreamModel streamModel);

  Intent sharePollIntent(Activity activity, PollModel pollModel);
}
