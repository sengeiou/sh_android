package com.shootr.android.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ShareCompat;
import com.shootr.android.R;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.StreamModel;

/** Creates {@link Intent}s for launching into external applications. */
public interface IntentFactory {
  Intent openUrlIntent(String url);

  Intent openEmbededUrlIntent(Activity launchActivity, String url);

  Intent shareShotIntent(Activity launchActivity, ShotModel shotModel);

  Intent shareStreamIntent(Activity launchActivity, StreamModel streamModel);

  IntentFactory REAL = new IntentFactory() {
    @Override public Intent openUrlIntent(String url) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(url));
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      return intent;
    }

    @Override
    public Intent openEmbededUrlIntent(Activity launchActivity, String url) {
      CustomTabsIntent customTabIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(launchActivity.getResources().getColor(R.color.primary))
        .setShowTitle(true)
        .build();
      customTabIntent.intent.setData(Uri.parse(url));
      return customTabIntent.intent;
    }

    @Override
    public Intent shareShotIntent(Activity launchActivity, ShotModel shotModel) {
      String subjectPattern = launchActivity.getString(R.string.share_shot_subject);
      String messagePattern = launchActivity.getString(R.string.share_shot_message);
      String urlPattern = launchActivity.getString(R.string.share_shot_base_url);

      String shotUrl = String.format(urlPattern, shotModel.getIdShot());
      String subject = String.format(subjectPattern, shotModel.getUsername(), shotModel.getStreamTitle());
      String sharedText = String.format(messagePattern, shotModel.getUsername(), shotModel.getStreamTitle(), shotUrl);

      return ShareCompat.IntentBuilder.from(launchActivity)
        .setType("text/plain")
        .setSubject(subject)
        .setText(sharedText)
        .setChooserTitle(R.string.share_shot_chooser_title)
        .createChooserIntent();
    }

    @Override
    public Intent shareStreamIntent(Activity launchActivity, StreamModel streamModel) {
      String subjectPattern = launchActivity.getString(R.string.share_stream_subject);
      String messagePattern = launchActivity.getString(R.string.share_stream_message);

      String subject = String.format(subjectPattern, streamModel.getTitle());
      String sharedText =
        String.format(messagePattern, streamModel.getTitle(), streamModel.getIdStream());

      return ShareCompat.IntentBuilder.from(launchActivity)
        .setType("text/plain")
        .setSubject(subject)
        .setText(sharedText)
        .setChooserTitle(R.string.share_via)
        .createChooserIntent();
    }

  };
}
