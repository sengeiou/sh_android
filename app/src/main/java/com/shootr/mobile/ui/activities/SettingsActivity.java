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
  @BindString(R.string.analytics_screen_push_settings) String analytics_screen_push_settings;
  @BindString(R.string.analytics_notification_disabled_nice) String analyticsNiceDisabled;
  @BindString(R.string.analytics_notification_disabled_reshoot) String analyticsReshootDisabled;
  @BindString(R.string.analytics_notification_disabled_started_shooting) String analyticsStartedShootingDisabled;
  @BindString(R.string.analytics_notification_disabled_poll) String analyticsPollDisabled;
  @BindString(R.string.analytics_notification_disabled_newFollowers) String analyticsnewFollowersDisabled;
  @BindString(R.string.analytics_notification_disabled_checkin) String analyticsCheckinDisabled;
  @BindString(R.string.analytics_action_notification_disabled) String analyticsAction;
  @BindString(R.string.analytics_label_notification_disabled) String analyticsLabel;

  private CharSequence[] itemsStartedShooting = new CharSequence[3];
  private CharSequence[] itemsNiceShot = new CharSequence[3];
  private CharSequence[] itemsReShot = new CharSequence[3];
  private CharSequence[] itemsNewFollowers = new CharSequence[3];
  private CharSequence[] itemsPoll = new CharSequence[3];
  private CharSequence[] itemsCheckin = new CharSequence[3];

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    /* no-op */
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_settings;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    itemsStartedShooting[0] = getResources().getString(R.string.off_push_settings);
    itemsStartedShooting[1] = getResources().getString(R.string.favorite_streams_push_settings);
    itemsStartedShooting[2] = getResources().getString(R.string.all_push_settings);

    itemsNiceShot[0] = getResources().getString(R.string.nices_push_settings_off);
    itemsNiceShot[1] = getResources().getString(R.string.push_settings_people_follow);
    itemsNiceShot[2] = getResources().getString(R.string.push_settings_everyone);

    itemsReShot[0] = getResources().getString(R.string.nices_push_settings_off);
    itemsReShot[1] = getResources().getString(R.string.push_settings_people_follow);
    itemsReShot[2] = getResources().getString(R.string.push_settings_everyone);

    itemsNewFollowers[0] = getResources().getString(R.string.nices_push_settings_off);
    itemsNewFollowers[1] = getResources().getString(R.string.push_settings_people_follow);
    itemsNewFollowers[2] = getResources().getString(R.string.push_settings_everyone);

    itemsPoll[0] = getResources().getString(R.string.nices_push_settings_off);
    itemsPoll[1] = getResources().getString(R.string.favorite_streams_push_settings);
    itemsPoll[2] = getResources().getString(R.string.all_push_settings);

    itemsCheckin[0] = getResources().getString(R.string.nices_push_settings_off);
    itemsCheckin[1] = getResources().getString(R.string.favorite_streams_push_settings);
    itemsCheckin[2] = getResources().getString(R.string.all_push_settings);
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

  private void setSelectedSettingsStartedShooting(String selectedOption, int which) {
    selectedPushStartedShootingSetting.setVisibility(View.VISIBLE);
    selectedPushStartedShootingSetting.setText(itemsStartedShooting[which]);
    presenter.startedShootingSettingChanged(selectedOption);
  }

  private void setSelectedSettingsReshot(String selectedOption, int which) {
    selectedPushReShotSetting.setVisibility(View.VISIBLE);
    selectedPushReShotSetting.setText(itemsReShot[which]);
    presenter.reShotSettingChanged(selectedOption);
  }

  private void setSelectedSettingsNiceShot(String selectedOption, int which) {
    selectedPushNiceShotSetting.setVisibility(View.VISIBLE);
    selectedPushNiceShotSetting.setText(itemsNiceShot[which]);
    presenter.niceShotSettingChanged(selectedOption);
  }

  private void setSelectedSettingsNewFollowers(String selectedOption, int which) {
    selectedPushNewFollowersSetting.setVisibility(View.VISIBLE);
    selectedPushNewFollowersSetting.setText(itemsNiceShot[which]);
    presenter.newFollowersSettingChanged(selectedOption);
  }

  private void setSelectedSettingsPoll(String selectedOption, int which) {
    selectedPushPollSetting.setVisibility(View.VISIBLE);
    selectedPushPollSetting.setText(itemsNiceShot[which]);
    presenter.pollSettingChanged(selectedOption);
  }

  private void setSelectedSettingsCheckin(String selectedOption, int which) {
    selectedPushCheckinSetting.setVisibility(View.VISIBLE);
    selectedPushCheckinSetting.setText(itemsNiceShot[which]);
    presenter.checkinSettingChanged(selectedOption);
  }

  @OnClick(R.id.layout_push_settings) public void onSettingsClick() {
    showStartedShootingDialog();
  }

  @OnClick(R.id.layout_nice_push_settings) public void onNiceSettingsClick() {
    showNiceShotDialog();
  }

  @OnClick(R.id.layout_reshot_push_settings) public void onReshotSettingsClick() {
    showReShotDialog();
  }

  @OnClick(R.id.layout_new_followers_push_settings) public void onNewFollowersSettingsClick() {
    showNewFollowersDialog();
  }

  @OnClick(R.id.layout_poll_push_settings) public void onPollSettingsClick() {
    showPollDialog();
  }

  @OnClick(R.id.layout_checkin_push_settings) public void onCheckinSettingsClick() {
    showCheckinDialog();
  }

  public void showStartedShootingDialog() {
    new AlertDialog.Builder(this).setTitle(
        getResources().getString(R.string.started_shooting_push_settings))
        .setNegativeButton(getResources().getString(R.string.cancel), null)
        .setItems(itemsStartedShooting, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            setSelectedSettingsStartedShooting(PushSettingType.TYPES_STARTED_SHOOTING[which], which);
          }
        })
        .create()
        .show();
  }

  public void showNiceShotDialog() {
    new AlertDialog.Builder(this).setTitle(
        getResources().getString(R.string.nices_push_settings))
        .setNegativeButton(getResources().getString(R.string.cancel), null)
        .setItems(itemsNiceShot, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            setSelectedSettingsNiceShot(PushSettingType.TYPES_NICE_SHOT[which], which);
          }
        })
        .create()
        .show();
  }

  public void showReShotDialog() {
    new AlertDialog.Builder(this).setTitle(
        getResources().getString(R.string.reshot_push_settings))
        .setNegativeButton(getResources().getString(R.string.cancel), null)
        .setItems(itemsReShot, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            setSelectedSettingsReshot(PushSettingType.TYPES_RESHOT[which], which);
          }
        })
        .create()
        .show();
  }

  public void showNewFollowersDialog() {
    new AlertDialog.Builder(this).setTitle(
        getResources().getString(R.string.new_followers_push_settings))
        .setNegativeButton(getResources().getString(R.string.cancel), null)
        .setItems(itemsNewFollowers, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            setSelectedSettingsNewFollowers(PushSettingType.TYPES_NEW_FOLLOWERS[which], which);
          }
        })
        .create()
        .show();
  }

  public void showCheckinDialog() {
    new AlertDialog.Builder(this).setTitle(
        getResources().getString(R.string.checkin_push_settings))
        .setNegativeButton(getResources().getString(R.string.cancel), null)
        .setItems(itemsCheckin, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            setSelectedSettingsCheckin(PushSettingType.TYPES_CHECKIN[which], which);
          }
        })
        .create()
        .show();
  }

  public void showPollDialog() {
    new AlertDialog.Builder(this).setTitle(
        getResources().getString(R.string.poll_push_settings))
        .setNegativeButton(getResources().getString(R.string.cancel), null)
        .setItems(itemsPoll, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            setSelectedSettingsPoll(PushSettingType.TYPES_POLL[which], which);
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
    selectedPushStartedShootingSetting.setText(itemsStartedShooting[startedShootingPushSettings]);
  }

  @Override public void setNiceShotSettings(Integer niceShotPushSettings) {
    selectedPushNiceShotSetting.setVisibility(View.VISIBLE);
    selectedPushNiceShotSetting.setText(itemsNiceShot[niceShotPushSettings]);
  }

  @Override public void setReShotSettings(Integer reShotPushSettings) {
    selectedPushReShotSetting.setVisibility(View.VISIBLE);
    selectedPushReShotSetting.setText(itemsReShot[reShotPushSettings]);
  }

  @Override public void setNewFollowersSettings(Integer newFollowersPushSettings) {
    selectedPushNewFollowersSetting.setVisibility(View.VISIBLE);
    selectedPushNewFollowersSetting.setText(itemsNewFollowers[newFollowersPushSettings]);
  }

  @Override public void setPollSettings(Integer pollPushSettings) {
    selectedPushPollSetting.setVisibility(View.VISIBLE);
    selectedPushPollSetting.setText(itemsPoll[pollPushSettings]);
  }

  @Override public void setCheckinSettings(Integer checkinPushSettings) {
    selectedPushCheckinSetting.setVisibility(View.VISIBLE);
    selectedPushCheckinSetting.setText(itemsCheckin[checkinPushSettings]);
  }

  @Override public void sendNotificationAnalytics(String type) {
    if (type.equals(PushSettingType.STARTED_SHOOTING)) {
      sendAnalytics(analyticsStartedShootingDisabled);
    } else if (type.equals(PushSettingType.NICE_SHOT)) {
      sendAnalytics(analyticsNiceDisabled);
    } else if (type.equals(PushSettingType.SHARED_SHOT)) {
      sendAnalytics(analyticsReshootDisabled);
    } else if (type.equals(PushSettingType.NEW_FOLLOWERS)) {
      sendAnalytics(analyticsnewFollowersDisabled);
    } else if (type.equals(PushSettingType.POLL)) {
      sendAnalytics(analyticsPollDisabled);
    } else if (type.equals(PushSettingType.CHECK_IN)) {
      sendAnalytics(analyticsCheckinDisabled);
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
