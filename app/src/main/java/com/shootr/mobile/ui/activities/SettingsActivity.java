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
  @BindString(R.string.analytics_screen_push_settings) String analytics_screen_push_settings;
  @BindString(R.string.analytics_notification_disabled_nice) String analyticsNiceDisabled;
  @BindString(R.string.analytics_notification_disabled_reshoot) String analyticsReshootDisabled;
  @BindString(R.string.analytics_notification_disabled_started_shooting) String analyticsStartedShootingDisabled;
  @BindString(R.string.analytics_action_notification_disabled) String analyticsAction;
  @BindString(R.string.analytics_label_notification_disabled) String analyticsLabel;

  private CharSequence[] itemsStartedShooting = new CharSequence[3];
  private CharSequence[] itemsNiceShot = new CharSequence[3];
  private CharSequence[] itemsReShot = new CharSequence[3];

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

  @OnClick(R.id.layout_push_settings) public void onSettingsClick() {
    showStartedShootingDialog();
  }

  @OnClick(R.id.layout_nice_push_settings) public void onNiceSettingsClick() {
    showNiceShotDialog();
  }

  @OnClick(R.id.layout_reshot_push_settings) public void onReshotSettingsClick() {
    showReShotDialog();
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

  @Override public void sendNotificationAnalytics(String type) {
    if (type.equals(PushSettingType.STARTED_SHOOTING)) {
      sendAnalytics(analyticsStartedShootingDisabled);
    } else if (type.equals(PushSettingType.NICE_SHOT)) {
      sendAnalytics(analyticsNiceDisabled);
    } else if (type.equals(PushSettingType.SHARED_SHOT)) {
      sendAnalytics(analyticsReshootDisabled);
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
