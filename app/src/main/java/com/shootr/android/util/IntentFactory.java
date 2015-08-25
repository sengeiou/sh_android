package com.shootr.android.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import com.shootr.android.R;
import com.shootr.android.ui.model.ShotModel;

/** Creates {@link Intent}s for launching into external applications. */
public interface IntentFactory {
  Intent createUrlIntent(String url);

  Intent shareShotIntent(Activity launchActivity, ShotModel shotModel);

  IntentFactory REAL = new IntentFactory() {
    @Override public Intent createUrlIntent(String url) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(url));
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      return intent;
    }

    @Override
    public Intent shareShotIntent(Activity launchActivity, ShotModel shotModel) {
      String subjectPattern = launchActivity.getString(R.string.share_shot_subject);
      String messagePattern = launchActivity.getString(R.string.share_shot_message);

      String subject = String.format(subjectPattern, shotModel.getUsername(), shotModel.getStreamTitle());
      String sharedText =
        String.format(messagePattern, shotModel.getUsername(), shotModel.getStreamTitle(), shotModel.getIdShot());

      return ShareCompat.IntentBuilder.from(launchActivity)
        .setType("text/plain")
        .setSubject(subject)
        .setText(sharedText)
        .setChooserTitle(R.string.share_shot_chooser_title)
        .createChooserIntent();
    }
  };
}
