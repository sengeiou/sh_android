package com.shootr.mobile.ui.presenter;

import android.support.annotation.NonNull;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.ChangeUserSettingsInteractor;
import com.shootr.mobile.domain.interactor.user.GetSettingsByIdUserInteractor;
import com.shootr.mobile.domain.model.PushSettingType;
import com.shootr.mobile.domain.model.user.UserSettings;
import com.shootr.mobile.ui.model.UserSettingsModel;
import com.shootr.mobile.ui.model.mappers.UserSettingsModelMapper;
import com.shootr.mobile.ui.views.SettingsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import javax.inject.Inject;

public class SettingsPresenter implements Presenter {

  private final GetSettingsByIdUserInteractor getSettingsByIdUserInteractor;
  private final ChangeUserSettingsInteractor changeUserSettingsInteractor;
  private final UserSettingsModelMapper userSettingsModelMapper;
  private final ErrorMessageFactory errorMessageFactory;
  private SettingsView settingsView;
  private UserSettingsModel userSettingsModel;

  @Inject public SettingsPresenter(GetSettingsByIdUserInteractor getSettingsByIdUserInteractor,
      ChangeUserSettingsInteractor changeUserSettingsInteractor,
      UserSettingsModelMapper userSettingsModelMapper, ErrorMessageFactory errorMessageFactory) {
    this.getSettingsByIdUserInteractor = getSettingsByIdUserInteractor;
    this.changeUserSettingsInteractor = changeUserSettingsInteractor;
    this.userSettingsModelMapper = userSettingsModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void initialize(SettingsView settingsView) {
    this.setView(settingsView);
    loadUserSettings();
  }

  protected void setView(SettingsView settingsView) {
    this.settingsView = settingsView;
  }

  private void loadUserSettings() {
    getSettingsByIdUserInteractor.loadSettings(new Interactor.Callback<UserSettings>() {
      @Override public void onLoaded(UserSettings userSettings) {
        userSettingsModel = userSettingsModelMapper.map(userSettings);
        String startedShootingPushSettings = userSettingsModel.getStartedShootingPushSettings();
        Integer option = mapStartedShootingOption(startedShootingPushSettings);
        settingsView.setStartedShootingSettings(option);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        settingsView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  @NonNull private Integer mapStartedShootingOption(String startedShootingPushSettings) {
    Integer option = 2;
    switch (startedShootingPushSettings) {
      case PushSettingType.STARTED_SHOOTING_ALL:
        option = 2;
        break;
      case PushSettingType.STARTED_SHOOTING_FAVORITE_STREAMS:
        option = 1;
        break;
      case PushSettingType.STARTED_SHOOTING_OFF:
        option = 0;
        break;
      default:
        break;
    }
    return option;
  }

  @Override public void resume() {

  }

  @Override public void pause() {
  }

  public void startedShootingSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    changedSettings.setStartedShootingPushSettings(selectedOption);
    changeUserSettingsInteractor.changeSettings(userSettingsModelMapper.reverseMap(changedSettings),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            userSettingsModel = changedSettings;
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            Integer option =
                mapStartedShootingOption(userSettingsModel.getStartedShootingPushSettings());
            settingsView.setStartedShootingSettings(option);
          }
        });
  }
}
