package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.PushSettingType;
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
  @Bind(R.id.started_shooting_push_option) TextView selectedPushSetting;
  @BindString(R.string.analytics_screen_push_settings) String analytics_screen_push_settings;

  private CharSequence[] items = new CharSequence[3];

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    /* no-op */
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_settings;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    items[0] = getResources().getString(R.string.off_push_settings);
    items[1] = getResources().getString(R.string.favorite_streams_push_settings);
    items[2] = getResources().getString(R.string.all_push_settings);
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

  private void setSelectedSettings(String selectedOption, int which) {
    selectedPushSetting.setVisibility(View.VISIBLE);
    selectedPushSetting.setText(items[which]);
    presenter.startedShootingSettingChanged(selectedOption);
  }

  @OnClick(R.id.layout_push_settings) public void onSettingsClick() {
    showStartedShootingDialog();
  }

  public void showStartedShootingDialog() {
    new AlertDialog.Builder(this).setTitle(
        getResources().getString(R.string.profile_push_notifications))
        .setNegativeButton(getResources().getString(R.string.cancel), null)
        .setItems(items, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            setSelectedSettings(PushSettingType.TYPES_STARTED_SHOOTING[which], which);
          }
        })
        .create()
        .show();
  }

  @Override public void showError(String messageForError) {
    feedbackMessage.show(getView(), messageForError);
  }

  @Override public void setStartedShootingSettings(Integer startedShootingPushSettings) {
    selectedPushSetting.setVisibility(View.VISIBLE);
    selectedPushSetting.setText(items[startedShootingPushSettings]);
  }
}
