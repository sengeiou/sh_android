package com.shootr.mobile.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ShareCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;

/** Creates {@link Intent}s for launching into external applications. */
public interface IntentFactory {

  Intent openUrlIntent(String url);

  Intent openEmbededUrlIntent(Activity launchActivity, String url);

  Intent shareShotIntent(Activity activity, ShotModel shotModel, String locale);

  Intent shareStreamIntent(Activity launchActivity, StreamModel streamModel, String locale);

  Intent reportEmailIntent(Activity launchActivity, String currentUserId, String reportedUserId);

  Intent sharePollIntent(Activity activity, PollModel pollModel, String locale);

  Intent sharePollVotedIntent(Activity activity, PollModel pollModel,
      PollOptionModel pollOptionModel, String locale);

  Intent shareProfileIntent(Activity activity, UserModel userModel);

  IntentFactory REAL = new IntentFactory() {
    @Override public Intent openUrlIntent(String url) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(url));
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      return intent;
    }

    @Override public Intent openEmbededUrlIntent(Activity launchActivity, String url) {
      CustomTabsIntent customTabIntent = new CustomTabsIntent.Builder().setToolbarColor(
          launchActivity.getResources().getColor(R.color.primary)).setShowTitle(true).build();
      customTabIntent.intent.setData(Uri.parse(url));
      return customTabIntent.intent;
    }

    @Override
    public Intent shareShotIntent(Activity launchActivity, ShotModel shotModel, String locale) {
      String subjectPattern = launchActivity.getString(R.string.share_shot_subject);
      String messagePattern = launchActivity.getString(R.string.share_shot_message);

      String shotUrl = shotModel.getShareLink();
      String subject =
          String.format(subjectPattern, shotModel.getUsername(), shotModel.getStreamTitle());
      String sharedText = String.format(messagePattern, shotUrl);

      return ShareCompat.IntentBuilder.from(launchActivity)
          .setType("text/plain")
          .setSubject(subject)
          .setText(sharedText)
          .setChooserTitle(R.string.share_shot_chooser_title)
          .createChooserIntent();
    }

    @Override public Intent shareStreamIntent(Activity launchActivity, StreamModel streamModel,
        String locale) {
      String subjectPattern = launchActivity.getString(R.string.share_stream_subject);
      String messagePattern = launchActivity.getString(R.string.share_stream_message);

      String streamUrl = streamModel.getShareLink();
      String subject = String.format(subjectPattern, streamModel.getTitle());
      String sharedText = String.format(messagePattern, streamModel.getTitle(), streamUrl);

      return ShareCompat.IntentBuilder.from(launchActivity)
          .setType("text/plain")
          .setSubject(subject)
          .setText(sharedText)
          .setChooserTitle(R.string.share_via)
          .createChooserIntent();
    }

    @Override public Intent reportEmailIntent(Activity launchActivity, String currentUserId,
        String reportedUserId) {
      String adress = launchActivity.getString(R.string.feedback_email_address);
      String subjectPattern = launchActivity.getString(R.string.report_user_subject);
      String subject = String.format(subjectPattern, currentUserId, reportedUserId);
      String defaultMessage = launchActivity.getString(R.string.report_user_default_message);

      Intent intent = new Intent(Intent.ACTION_SENDTO);
      intent.setType("text/plain");
      String uriText = "mailto:"
          + Uri.encode(adress)
          + "?subject="
          + Uri.encode(subject)
          + "&body="
          + Uri.encode(defaultMessage);
      Uri uri = Uri.parse(uriText);
      intent.setData(uri);
      Intent.createChooser(intent, launchActivity.getString(R.string.report_context_menu_report));

      return intent;
    }

    @Override public Intent sharePollIntent(Activity activity, PollModel pollModel, String locale) {
      String messagePattern = activity.getString(R.string.share_poll_message);
      String subjectPattern = activity.getString(R.string.share_poll_subject);
      String pollUrl = pollModel.getShareLink();

      String subject = String.format(subjectPattern, pollModel.getQuestion());

      return ShareCompat.IntentBuilder.from(activity)
          .setType("text/plain")
          .setSubject(subject)
          .setText(pollUrl)
          .setChooserTitle(R.string.share_via)
          .createChooserIntent();
    }

    @Override public Intent sharePollVotedIntent(Activity activity, PollModel pollModel,
        PollOptionModel pollOptionModel, String locale) {
      String messagePattern = activity.getString(R.string.share_poll_message);
      String subjectPattern = activity.getString(R.string.share_poll_voted_subject);

      String sharedText = String.format(messagePattern, pollModel.getShareLink());

      String subject =
          String.format(subjectPattern, pollOptionModel.getText(), pollModel.getQuestion());

      return ShareCompat.IntentBuilder.from(activity)
          .setType("text/plain")
          .setSubject(subject)
          .setText(sharedText)
          .setChooserTitle(R.string.share_via)
          .createChooserIntent();
    }

    @Override public Intent shareProfileIntent(Activity activity, UserModel userModel) {
      String messagePattern = userModel.getShareLink();
      String subjectPattern = activity.getString(R.string.share_user_profile_subject);
      String sharedText = String.format(subjectPattern, userModel.getName());

      return ShareCompat.IntentBuilder.from(activity)
          .setType("text/plain")
          .setSubject(sharedText)
          .setText(messagePattern)
          .setChooserTitle(R.string.share_via)
          .createChooserIntent();
    }
  };
}
