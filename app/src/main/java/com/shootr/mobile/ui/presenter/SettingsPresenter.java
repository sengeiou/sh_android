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
        Integer optionStartShooting = mapOptionAllFavoriteOff(startedShootingPushSettings);
        settingsView.setStartedShootingSettings(optionStartShooting);

        String niceShotPushSettings = userSettingsModel.getNiceShotPushSettings();
        Integer optionNiceShot = mapOptionAllOffFollowings(niceShotPushSettings);
        settingsView.setNiceShotSettings(optionNiceShot);

        String reshotPushSettings = userSettingsModel.getReShotPushSettings();
        Integer optionReShot = mapOptionAllOffFollowings(reshotPushSettings);
        settingsView.setReShotSettings(optionReShot);

        String newFollowersPushSettings = userSettingsModel.getNewFollowersPushSettings();
        Integer optionNewFollowers = mapOptionAllOffFollowings(newFollowersPushSettings);
        settingsView.setNewFollowersSettings(optionNewFollowers);

        String pollPushSettings = userSettingsModel.getPollPushSettings();
        Integer optionPoll = mapOptionAllFavoriteOff(pollPushSettings);
        settingsView.setPollSettings(optionPoll);

        String checkinPushSettings = userSettingsModel.getCheckinPushSettings();
        Integer optioncheckin = mapOptionAllFavoriteOff(checkinPushSettings);
        settingsView.setCheckinSettings(optioncheckin);

        String privateMessagePushSettings = userSettingsModel.getPrivateMessagePushSettings();
        Integer optionprivatemessage = mapOptionAllOffFollowings(privateMessagePushSettings);
        settingsView.setPrivateMessageSettings(optionprivatemessage);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        settingsView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  @NonNull private Integer mapOptionAllFavoriteOff(String settings) {
    Integer option = 2;
    switch (settings) {
      case PushSettingType.ALL:
        option = 2;
        break;
      case PushSettingType.FAVORITE_STREAMS:
        option = 1;
        break;
      case PushSettingType.OFF:
        option = 0;
        break;
      default:
        break;
    }
    return option;
  }

  @NonNull private Integer mapOptionAllOffFollowings(String settings) {
    Integer option = 2;
    switch (settings) {
      case PushSettingType.ALL:
        option = 2;
        break;
      case PushSettingType.OFF:
        option = 0;
        break;
      case PushSettingType.FOLLOWING:
        option = 1;
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
    if (selectedOption != null) {
      final UserSettingsModel changedSettings = userSettingsModel;
      if (changedSettings != null) {
        changedSettings.setStartedShootingPushSettings(selectedOption);
        changeUserSettingsInteractor.changeSettings(
            userSettingsModelMapper.reverseMap(changedSettings),
            PushSettingType.STARTED_SHOOTING,
            new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                userSettingsModel = changedSettings;
                settingsView.sendNotificationAnalytics(PushSettingType.STARTED_SHOOTING);
              }
            }, new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                Integer option =
                    mapOptionAllFavoriteOff(userSettingsModel.getStartedShootingPushSettings());
                settingsView.setStartedShootingSettings(option);
              }
            });
      }
    }
  }

  public void reShotSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setReShotPushSettings(selectedOption);
      changeUserSettingsInteractor.changeSettings(
          userSettingsModelMapper.reverseMap(changedSettings),
          PushSettingType.SHARED_SHOT,
          new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.SHARED_SHOT);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option = mapOptionAllFavoriteOff(userSettingsModel.getReShotPushSettings());
              settingsView.setReShotSettings(option);
            }
          });
    }
  }

  public void niceShotSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setNiceShotPushSettings(selectedOption);
      changeUserSettingsInteractor.changeSettings(
          userSettingsModelMapper.reverseMap(changedSettings),
          PushSettingType.NICE_SHOT,
          new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.NICE_SHOT);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option = mapOptionAllOffFollowings(userSettingsModel.getNiceShotPushSettings());
              settingsView.setNiceShotSettings(option);
            }
          });
    }
  }

  public void newFollowersSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setNewFollowersPushSettings(selectedOption);
      changeUserSettingsInteractor.changeSettings(
          userSettingsModelMapper.reverseMap(changedSettings),
          PushSettingType.NEW_FOLLOWERS,
          new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.NEW_FOLLOWERS);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option = mapOptionAllOffFollowings(userSettingsModel.getNewFollowersPushSettings());
              settingsView.setNewFollowersSettings(option);
            }
          });
    }
  }

  public void pollSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setPollPushSettings(selectedOption);
      changeUserSettingsInteractor.changeSettings(
          userSettingsModelMapper.reverseMap(changedSettings),
          PushSettingType.POLL,
          new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.POLL);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option = mapOptionAllFavoriteOff(userSettingsModel.getPollPushSettings());
              settingsView.setPollSettings(option);
            }
          });
    }
  }

  public void privateMessageSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setPrivateMessagePushSettings(selectedOption);
      changeUserSettingsInteractor.changeSettings(
          userSettingsModelMapper.reverseMap(changedSettings),
          PushSettingType.PRIVATE_MESSAGE,
          new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.PRIVATE_MESSAGE);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option = mapOptionAllOffFollowings(userSettingsModel.getPrivateMessagePushSettings());
              settingsView.setPrivateMessageSettings(option);
            }
          });
    }
  }

  public void checkinSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setCheckinPushSettings(selectedOption);
      changeUserSettingsInteractor.changeSettings(
          userSettingsModelMapper.reverseMap(changedSettings),
          PushSettingType.CHECK_IN,
          new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.CHECK_IN);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option = mapOptionAllFavoriteOff(userSettingsModel.getCheckinPushSettings());
              settingsView.setCheckinSettings(option);
            }
          });
    }
  }
}
