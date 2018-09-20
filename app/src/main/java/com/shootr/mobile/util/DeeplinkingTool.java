package com.shootr.mobile.util;

import android.content.Context;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

public class DeeplinkingTool implements DeeplinkingNavigator {

  public static final String SHARE_STREAM_PATTERN_HTTPS = "https://web.shootr.com/st/";
  public static final String SHARE_STREAM_PATTERN_HTTP = "http://web.shootr.com/st/";
  public static final String SHARE_POLL_PATTERN_HTTPS = "https://web.shootr.com/poll/";
  public static final String SHARE_POLL_PATTERN_HTTP = "http://web.shootr.com/poll/";
  public static final String SHARE_SHOT_PATTERN_HTTPS = "https://web.shootr.com/s/";
  public static final String SHARE_SHOT_PATTERN_HTTP = "http://web.shootr.com/s/";
  public static final String SHARE_USER_PATTERN_HTTP = "http://web.shootr.com/user/";
  public static final String SHARE_USER_PATTERN_HTTPS = "https://web.shootr.com/user/";
  public static final String SHARE_SHOT_PATTERN_SHOOTR = "shootr://s/";
  public static final String SHARE_STREAM_PATTERN_SHOOTR = "shootr://st/";

  @BindString(R.string.analytics_action_open_deep_linking) String analyticsActionOpenLink;
  @BindString(R.string.analytics_label_open_deep_linking) String analyticsLabelOpenLink;

  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  @Inject public DeeplinkingTool() {
  }

  @Override public void navigate(Context context, String address) {
    sendAnalytics(context);

    Pattern shareStreamPatternWithHttps = Pattern.compile(SHARE_STREAM_PATTERN_HTTPS);
    Matcher matcherShareStreamHttps = shareStreamPatternWithHttps.matcher(address);

    Pattern shareStreamPatternWithHttp = Pattern.compile(SHARE_STREAM_PATTERN_HTTP);
    Matcher matcherShateStreamHttp = shareStreamPatternWithHttp.matcher(address);

    Pattern shareShotPatternWithHttp = Pattern.compile(SHARE_SHOT_PATTERN_HTTP);
    Matcher matcherShareShotHttp = shareShotPatternWithHttp.matcher(address);

    Pattern shareUserPatternWithHttp = Pattern.compile(SHARE_USER_PATTERN_HTTP);
    Matcher matcherShareUserHttp = shareUserPatternWithHttp.matcher(address);

    Pattern shareUserPatternWithHttps = Pattern.compile(SHARE_USER_PATTERN_HTTPS);
    Matcher matcherShareUserHttps = shareUserPatternWithHttps.matcher(address);

    Pattern shareShotPatternWithHttps = Pattern.compile(SHARE_SHOT_PATTERN_HTTPS);
    Matcher matcherShareShotHttps = shareShotPatternWithHttps.matcher(address);

    Pattern shareShotPatternWithShootr = Pattern.compile(SHARE_SHOT_PATTERN_SHOOTR);
    Matcher matcherShareShotShootr = shareShotPatternWithShootr.matcher(address);

    Pattern shareStreamPatternWithShootr = Pattern.compile(SHARE_STREAM_PATTERN_SHOOTR);
    Matcher matcherShateStreamShootr = shareStreamPatternWithShootr.matcher(address);

    Pattern sharePollPatternWithHttps = Pattern.compile(SHARE_POLL_PATTERN_HTTPS);
    Matcher matcherSharePollHttps = sharePollPatternWithHttps.matcher(address);

    Pattern sharePollPatternWithHttp = Pattern.compile(SHARE_POLL_PATTERN_HTTP);
    Matcher matcherSharePollHttp = sharePollPatternWithHttp.matcher(address);

    if (matcherShareStreamHttps.find()) {
      String idStream = address.substring(matcherShareStreamHttps.end());
      context.startActivity(StreamTimelineActivity.newIntent(context, removeLocale(idStream)));
    } else if (matcherShateStreamHttp.find()) {
      String idStream = address.substring(matcherShateStreamHttp.end());
      context.startActivity(StreamTimelineActivity.newIntent(context, removeLocale(idStream)));
    } else if (matcherShateStreamShootr.find()) {
      String idStream = address.substring(matcherShateStreamShootr.end());
      context.startActivity(StreamTimelineActivity.newIntent(context, removeLocale(idStream)));
    } else if (matcherShareShotHttp.find()) {
      String idShot = address.substring(matcherShareShotHttp.end());
      context.startActivity(ShotDetailActivity.getIntentForActivity(context, removeLocale(idShot)));
    } else if (matcherShareShotHttps.find()) {
      String idShot = address.substring(matcherShareShotHttps.end());
      context.startActivity(ShotDetailActivity.getIntentForActivity(context, removeLocale(idShot)));
    } else if (matcherShareShotShootr.find()) {
      String idShot = address.substring(matcherShareShotShootr.end());
      context.startActivity(ShotDetailActivity.getIntentForActivity(context, removeLocale(idShot)));
    } else if (matcherSharePollHttps.find()) {
      String idPoll = address.substring(matcherSharePollHttps.end());
      context.startActivity(
          PollVoteActivity.newIntentWithIdPoll(context, removeLocale(idPoll), null));
    } else if (matcherSharePollHttp.find()) {
      String idPoll = address.substring(matcherSharePollHttp.end());
      context.startActivity(
          PollVoteActivity.newIntentWithIdPoll(context, removeLocale(idPoll), null));
    } else if (matcherShareUserHttp.find()) {
      String idUser = address.substring(matcherShareUserHttp.end());
      context.startActivity(
          ProfileActivity.getIntent(context, removeLocale(idUser)));
    } else if (matcherShareUserHttps.find()) {
      String idUser = address.substring(matcherShareUserHttps.end());
      context.startActivity(
          ProfileActivity.getIntent(context, removeLocale(idUser)));
    }
  }

  private void sendAnalytics(Context context) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(context);
    builder.setActionId(analyticsActionOpenLink);
    builder.setLabelId(analyticsLabelOpenLink);
    if (sessionRepository.getCurrentUser() != null) {
      builder.setUser(sessionRepository.getCurrentUser());
    }
    analyticsTool.analyticsSendAction(builder);
  }

  private String removeLocale(String anyIdPlusLocale) {
    Pattern localePattern = Pattern.compile("\\?");
    Matcher matcherLocale = localePattern.matcher(anyIdPlusLocale);
    if (matcherLocale.find()) {
      return anyIdPlusLocale.substring(0, matcherLocale.start());
    } else {
      return anyIdPlusLocale;
    }
  }
}
