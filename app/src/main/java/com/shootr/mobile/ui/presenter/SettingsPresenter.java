package com.shootr.mobile.ui.presenter;

import android.support.annotation.NonNull;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.ChangeCheckinSettingsInteractor;
import com.shootr.mobile.domain.interactor.user.ChangeNewFollowersSettingsInteractor;
import com.shootr.mobile.domain.interactor.user.ChangeNiceShotSettingsInteractor;
import com.shootr.mobile.domain.interactor.user.ChangePollSettingsInteractor;
import com.shootr.mobile.domain.interactor.user.ChangeReShotSettingsInteractor;
import com.shootr.mobile.domain.interactor.user.ChangeStartedShootingSettingsInteractor;
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
  private final ChangeStartedShootingSettingsInteractor changeStartedShootingSettingsInteractor;
  private final ChangeNiceShotSettingsInteractor changeNiceShotSettingsInteractor;
  private final ChangeReShotSettingsInteractor changeReShotSettingsInteractor;
  private final ChangeNewFollowersSettingsInteractor changeNewFollowersSettingsInteractor;
  private final ChangePollSettingsInteractor changePollSettingsInteractor;
  private final ChangeCheckinSettingsInteractor changeCheckinSettingsInteractor;
  private final UserSettingsModelMapper userSettingsModelMapper;
  private final ErrorMessageFactory errorMessageFactory;
  private SettingsView settingsView;
  private UserSettingsModel userSettingsModel;

  @Inject public SettingsPresenter(GetSettingsByIdUserInteractor getSettingsByIdUserInteractor,
      ChangeStartedShootingSettingsInteractor changeStartedShootingSettingsInteractor,
      ChangeNiceShotSettingsInteractor changeNiceShotSettingsInteractor,
      ChangeReShotSettingsInteractor changeReShotSettingsInteractor,
      ChangeNewFollowersSettingsInteractor changeNewFollowersSettingsInteractor,
      ChangePollSettingsInteractor changePollSettingsInteractor,
      ChangeCheckinSettingsInteractor changeCheckinSettingsInteractor,
      UserSettingsModelMapper userSettingsModelMapper, ErrorMessageFactory errorMessageFactory) {
    this.getSettingsByIdUserInteractor = getSettingsByIdUserInteractor;
    this.changeStartedShootingSettingsInteractor = changeStartedShootingSettingsInteractor;
    this.changeNiceShotSettingsInteractor = changeNiceShotSettingsInteractor;
    this.changeReShotSettingsInteractor = changeReShotSettingsInteractor;
    this.changeNewFollowersSettingsInteractor = changeNewFollowersSettingsInteractor;
    this.changePollSettingsInteractor = changePollSettingsInteractor;
    this.changeCheckinSettingsInteractor = changeCheckinSettingsInteractor;
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
        Integer optionStartShooting = mapStartedShootingOption(startedShootingPushSettings);
        settingsView.setStartedShootingSettings(optionStartShooting);

        String niceShotPushSettings = userSettingsModel.getNiceShotPushSettings();
        Integer optionNiceShot = mapNiceShotOption(niceShotPushSettings);
        settingsView.setNiceShotSettings(optionNiceShot);

        String reshotPushSettings = userSettingsModel.getReShotPushSettings();
        Integer optionReShot = mapReShotOption(reshotPushSettings);
        settingsView.setReShotSettings(optionReShot);

        String newFollowersPushSettings = userSettingsModel.getNewFollowersPushSettings();
        Integer optionNewFollowers = mapNewFollowersOption(newFollowersPushSettings);
        settingsView.setNewFollowersSettings(optionNewFollowers);

        String pollPushSettings = userSettingsModel.getPollPushSettings();
        Integer optionPoll = mapPollOption(pollPushSettings);
        settingsView.setPollSettings(optionPoll);

        String checkinPushSettings = userSettingsModel.getCheckinPushSettings();
        Integer optioncheckin = mapCheckinOption(checkinPushSettings);
        settingsView.setCheckinSettings(optioncheckin);
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

  @NonNull private Integer mapReShotOption(String reShotPushSettings) {
    Integer option = 2;
    switch (reShotPushSettings) {
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

  @NonNull private Integer mapNiceShotOption(String niceShotPushSettings) {
    Integer option = 2;
    switch (niceShotPushSettings) {
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

  @NonNull private Integer mapNewFollowersOption(String newFollowersPushSettings) {
    Integer option = 2;
    switch (newFollowersPushSettings) {
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

  @NonNull private Integer mapPollOption(String pollPushSettings) {
    Integer option = 2;
    switch (pollPushSettings) {
      case PushSettingType.ALL:
        option = 2;
        break;
      case PushSettingType.OFF:
        option = 0;
        break;
      case PushSettingType.FAVORITE_STREAMS:
        option = 1;
        break;
      default:
        break;
    }
    return option;
  }

  @NonNull private Integer mapCheckinOption(String checkinPushSettings) {
    Integer option = 2;
    switch (checkinPushSettings) {
      case PushSettingType.ALL:
        option = 2;
        break;
      case PushSettingType.OFF:
        option = 0;
        break;
      case PushSettingType.FAVORITE_STREAMS:
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
        changeStartedShootingSettingsInteractor.changeStartedShotSettings(
            userSettingsModelMapper.reverseMap(changedSettings),
            new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                userSettingsModel = changedSettings;
                settingsView.sendNotificationAnalytics(PushSettingType.STARTED_SHOOTING);
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
  }

  public void reShotSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setReShotPushSettings(selectedOption);
      changeReShotSettingsInteractor.changeReShotSettings(
          userSettingsModelMapper.reverseMap(changedSettings), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.SHARED_SHOT);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option = mapReShotOption(userSettingsModel.getReShotPushSettings());
              settingsView.setReShotSettings(option);
            }
          });
    }
  }

  public void niceShotSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setNiceShotPushSettings(selectedOption);
      changeNiceShotSettingsInteractor.changeNiceShotSettings(
          userSettingsModelMapper.reverseMap(changedSettings), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.NICE_SHOT);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option = mapNiceShotOption(userSettingsModel.getNiceShotPushSettings());
              settingsView.setNiceShotSettings(option);
            }
          });
    }
  }

  public void newFollowersSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setNewFollowersPushSettings(selectedOption);
      changeNewFollowersSettingsInteractor.changeNewFollowersSettings(
          userSettingsModelMapper.reverseMap(changedSettings), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.NEW_FOLLOWERS);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option =
                  mapNewFollowersOption(userSettingsModel.getNewFollowersPushSettings());
              settingsView.setNewFollowersSettings(option);
            }
          });
    }
  }

  public void pollSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setPollPushSettings(selectedOption);
      changePollSettingsInteractor.changePollSettings(
          userSettingsModelMapper.reverseMap(changedSettings), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.POLL);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option = mapPollOption(userSettingsModel.getPollPushSettings());
              settingsView.setPollSettings(option);
            }
          });
    }
  }

  public void checkinSettingChanged(String selectedOption) {
    final UserSettingsModel changedSettings = userSettingsModel;
    if (changedSettings != null) {
      changedSettings.setCheckinPushSettings(selectedOption);
      changeCheckinSettingsInteractor.changeCheckinSettings(
          userSettingsModelMapper.reverseMap(changedSettings), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              userSettingsModel = changedSettings;
              settingsView.sendNotificationAnalytics(PushSettingType.CHECK_IN);
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              Integer option = mapCheckinOption(userSettingsModel.getCheckinPushSettings());
              settingsView.setCheckinSettings(option);
            }
          });
    }
  }
}
