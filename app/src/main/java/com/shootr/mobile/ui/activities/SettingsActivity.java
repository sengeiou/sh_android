package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.PushSettingType;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.presenter.SettingsPresenter;
import com.shootr.mobile.ui.views.SettingsView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import javax.inject.Inject;

public class SettingsActivity extends BaseToolbarDecoratedActivity implements SettingsView {

  @Inject SettingsPresenter presenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  @BindView(R.id.started_shooting_push_option) TextView selectedPushStartedShootingSetting;
  @BindView(R.id.nice_shot_push_option) TextView selectedPushNiceShotSetting;
  @BindView(R.id.reshot_push_option) TextView selectedPushReShotSetting;
  @BindView(R.id.new_followers_push_option) TextView selectedPushNewFollowersSetting;
  @BindView(R.id.poll_push_option) TextView selectedPushPollSetting;
  @BindView(R.id.checkin_push_option) TextView selectedPushCheckinSetting;
  @BindView(R.id.private_message_push_option) TextView selectedPushPrivateMessageSetting;
  @BindString(R.string.analytics_screen_push_settings) String analytics_screen_push_settings;
  @BindString(R.string.analytics_notification_disabled_nice) String analyticsNiceDisabled;
  @BindString(R.string.analytics_notification_disabled_reshoot) String analyticsReshootDisabled;
  @BindString(R.string.analytics_notification_disabled_started_shooting) String
      analyticsStartedShootingDisabled;
  @BindString(R.string.analytics_notification_disabled_poll) String analyticsPollDisabled;
  @BindString(R.string.analytics_notification_disabled_newFollowers) String
      analyticsnewFollowersDisabled;
  @BindString(R.string.analytics_notification_disabled_checkin) String analyticsCheckinDisabled;
  @BindString(R.string.analytics_notification_disabled_private_message) String
      analyticsPrivateMessageDisabled;
  @BindString(R.string.analytics_action_notification_disabled) String analyticsAction;
  @BindString(R.string.analytics_label_notification_disabled) String analyticsLabel;

  private CharSequence[] itemsOffFavoriteAll = new CharSequence[3];
  private CharSequence[] itemsOffFollowEveryone = new CharSequence[3];

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    /* no-op */
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_settings;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    itemsOffFavoriteAll[0] = getResources().getString(R.string.off_push_settings);
    itemsOffFavoriteAll[1] = getResources().getString(R.string.favorite_streams_push_settings);
    itemsOffFavoriteAll[2] = getResources().getString(R.string.all_push_settings);

    itemsOffFollowEveryone[0] = getResources().getString(R.string.off_push_settings);
    itemsOffFollowEveryone[1] = getResources().getString(R.string.push_settings_people_follow);
    itemsOffFollowEveryone[2] = getResources().getString(R.string.push_settings_everyone);
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this);
    analyticsTool.analyticsStart(this, analytics_screen_push_settings);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setSelectedSettings(String selectedOption, TextView textView,
      CharSequence selectedOptionText, final String settingsType) {
    textView.setVisibility(View.VISIBLE);
    textView.setText(selectedOptionText);
    switch (settingsType) {
      case PushSettingType.STARTED_SHOOTING:
        presenter.startedShootingSettingChanged(selectedOption);
        break;
      case PushSettingType.NICE_SHOT:
        presenter.niceShotSettingChanged(selectedOption);
        break;
      case PushSettingType.SHARED_SHOT:
        presenter.reShotSettingChanged(selectedOption);
        break;
      case PushSettingType.NEW_FOLLOWERS:
        presenter.newFollowersSettingChanged(selectedOption);
        break;
      case PushSettingType.CHECK_IN:
        presenter.checkinSettingChanged(selectedOption);
        break;
      case PushSettingType.POLL:
        presenter.pollSettingChanged(selectedOption);
        break;
      case PushSettingType.PRIVATE_MESSAGE:
        presenter.privateMessageSettingChanged(selectedOption);
        break;
      default:
        break;
    }
  }

  @OnClick(R.id.layout_push_settings) public void onSettingsClick() {
    String title = getResources().getString(R.string.started_shooting_push_settings);
    showSettingsDialog(title, PushSettingType.STARTED_SHOOTING, itemsOffFavoriteAll);
  }

  @OnClick(R.id.layout_nice_push_settings) public void onNiceSettingsClick() {
    String title = getResources().getString(R.string.nices_push_settings);
    showSettingsDialog(title, PushSettingType.NICE_SHOT, itemsOffFollowEveryone);
  }

  @OnClick(R.id.layout_reshot_push_settings) public void onReshotSettingsClick() {
    String title = getResources().getString(R.string.reshot_push_settings);
    showSettingsDialog(title, PushSettingType.SHARED_SHOT, itemsOffFollowEveryone);
  }

  @OnClick(R.id.layout_new_followers_push_settings) public void onNewFollowersSettingsClick() {
    String title = getResources().getString(R.string.new_followers_push_settings);
    showSettingsDialog(title, PushSettingType.NEW_FOLLOWERS, itemsOffFollowEveryone);
  }

  @OnClick(R.id.layout_poll_push_settings) public void onPollSettingsClick() {
    String title = getResources().getString(R.string.poll_push_settings);
    showSettingsDialog(title, PushSettingType.POLL, itemsOffFavoriteAll);
  }

  @OnClick(R.id.layout_checkin_push_settings) public void onCheckinSettingsClick() {
    String title = getResources().getString(R.string.checkin_push_settings);
    showSettingsDialog(title, PushSettingType.CHECK_IN, itemsOffFavoriteAll);
  }

  @OnClick(R.id.layout_privatemessages_push_settings) public void onPrivateMessageSettingsClick() {
    String title = getResources().getString(R.string.private_message_push_settings);
    showSettingsDialog(title, PushSettingType.PRIVATE_MESSAGE, itemsOffFollowEveryone);
  }

  public void showSettingsDialog(String title, final String settingsType,
      CharSequence[] itemsSettings) {
    new AlertDialog.Builder(this).setTitle(title)
        .setNegativeButton(getResources().getString(R.string.cancel), null)
        .setItems(itemsSettings, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            switch (settingsType) {
              case PushSettingType.STARTED_SHOOTING:
                setSelectedSettings(PushSettingType.TYPES_OFF_FAVORITE_ALL[which],
                    selectedPushStartedShootingSetting, itemsOffFavoriteAll[which], settingsType);
                break;
              case PushSettingType.NICE_SHOT:
                setSelectedSettings(PushSettingType.TYPES_OFF_FOLLOWING_ALL[which],
                    selectedPushNiceShotSetting, itemsOffFollowEveryone[which], settingsType);
                break;
              case PushSettingType.SHARED_SHOT:
                setSelectedSettings(PushSettingType.TYPES_OFF_FOLLOWING_ALL[which],
                    selectedPushReShotSetting, itemsOffFollowEveryone[which], settingsType);
                break;
              case PushSettingType.NEW_FOLLOWERS:
                setSelectedSettings(PushSettingType.TYPES_OFF_FOLLOWING_ALL[which],
                    selectedPushNewFollowersSetting, itemsOffFollowEveryone[which], settingsType);
                break;
              case PushSettingType.CHECK_IN:
                setSelectedSettings(PushSettingType.TYPES_OFF_FAVORITE_ALL[which],
                    selectedPushCheckinSetting, itemsOffFavoriteAll[which], settingsType);
                break;
              case PushSettingType.POLL:
                setSelectedSettings(PushSettingType.TYPES_OFF_FAVORITE_ALL[which],
                    selectedPushPollSetting, itemsOffFavoriteAll[which], settingsType);
                break;
              case PushSettingType.PRIVATE_MESSAGE:
                setSelectedSettings(PushSettingType.TYPES_OFF_FOLLOWING_ALL[which],
                    selectedPushPrivateMessageSetting, itemsOffFollowEveryone[which], settingsType);
                break;
              default:
                break;
            }
          }
        })
        .create()
        .show();
  }

  @Override public void showError(String messageForError) {
    feedbackMessage.show(getView(), messageForError);
  }

  @Override public void setStartedShootingSettings(Integer startedShootingPushSettings) {
    selectedPushStartedShootingSetting.setVisibility(View.VISIBLE);
    selectedPushStartedShootingSetting.setText(itemsOffFavoriteAll[startedShootingPushSettings]);
  }

  @Override public void setNiceShotSettings(Integer niceShotPushSettings) {
    selectedPushNiceShotSetting.setVisibility(View.VISIBLE);
    selectedPushNiceShotSetting.setText(itemsOffFollowEveryone[niceShotPushSettings]);
  }

  @Override public void setReShotSettings(Integer reShotPushSettings) {
    selectedPushReShotSetting.setVisibility(View.VISIBLE);
    selectedPushReShotSetting.setText(itemsOffFollowEveryone[reShotPushSettings]);
  }

  @Override public void setNewFollowersSettings(Integer newFollowersPushSettings) {
    selectedPushNewFollowersSetting.setVisibility(View.VISIBLE);
    selectedPushNewFollowersSetting.setText(itemsOffFollowEveryone[newFollowersPushSettings]);
  }

  @Override public void setPollSettings(Integer pollPushSettings) {
    selectedPushPollSetting.setVisibility(View.VISIBLE);
    selectedPushPollSetting.setText(itemsOffFavoriteAll[pollPushSettings]);
  }

  @Override public void setPrivateMessageSettings(Integer privateMessagePushSettings) {
    selectedPushPrivateMessageSetting.setVisibility(View.VISIBLE);
    selectedPushPrivateMessageSetting.setText(itemsOffFollowEveryone[privateMessagePushSettings]);
  }

  @Override public void setCheckinSettings(Integer checkinPushSettings) {
    selectedPushCheckinSetting.setVisibility(View.VISIBLE);
    selectedPushCheckinSetting.setText(itemsOffFavoriteAll[checkinPushSettings]);
  }

  @Override public void sendNotificationAnalytics(String type) {
    switch (type) {
      case PushSettingType.STARTED_SHOOTING:
        sendAnalytics(analyticsStartedShootingDisabled);
        break;
      case PushSettingType.NICE_SHOT:
        sendAnalytics(analyticsNiceDisabled);
        break;
      case PushSettingType.SHARED_SHOT:
        sendAnalytics(analyticsReshootDisabled);
        break;
      case PushSettingType.NEW_FOLLOWERS:
        sendAnalytics(analyticsnewFollowersDisabled);
        break;
      case PushSettingType.POLL:
        sendAnalytics(analyticsPollDisabled);
        break;
      case PushSettingType.CHECK_IN:
        sendAnalytics(analyticsCheckinDisabled);
        break;
      case PushSettingType.PRIVATE_MESSAGE:
        sendAnalytics(analyticsPrivateMessageDisabled);
        break;
      default:
        break;
    }
  }

  private void sendAnalytics(String pushSettingType) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(this);
    builder.setActionId(analyticsAction);
    builder.setLabelId(analyticsLabel);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setNotificationName(pushSettingType);
    analyticsTool.analyticsSendAction(builder);
  }
}
