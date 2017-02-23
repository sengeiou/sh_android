package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.bus.BadgeChanged;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.ChannelsBadgeChanged;
import com.shootr.mobile.domain.bus.UnwatchDone;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.discover.SendDeviceInfoInteractor;
import com.shootr.mobile.domain.interactor.shot.SendShotEventStatsIneteractor;
import com.shootr.mobile.domain.interactor.stream.GetLocalStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.GetMutedStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.mobile.domain.interactor.timeline.privateMessage.GetPrivateMessagesChannelsInteractor;
import com.shootr.mobile.domain.interactor.user.GetCurrentUserInteractor;
import com.shootr.mobile.domain.interactor.user.GetFollowingIdsInteractor;
import com.shootr.mobile.domain.interactor.user.GetFollowingInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserForAnalythicsByIdInteractor;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.MainScreenView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class MainScreenPresenter implements Presenter, BadgeChanged.Receiver, UnwatchDone.Receiver {

  private final GetCurrentUserInteractor getCurrentUserInteractor;
  private final SendDeviceInfoInteractor sendDeviceInfoInteractor;
  private final SendShotEventStatsIneteractor sendShotEventStatsIneteractor;
  private final GetUserForAnalythicsByIdInteractor getUserForAnalythicsByIdInteractor;
  private final GetMutedStreamsInteractor getMutedStreamsInteractor;
  private final UnwatchStreamInteractor unwatchStreamInteractor;
  private final SessionRepository sessionRepository;
  private final UserModelMapper userModelMapper;
  private final IntPreference badgeCount;
  private final GetFollowingInteractor followingInteractor;
  private final GetPrivateMessagesChannelsInteractor getPrivateMessagesChannelsInteractor;
  private final GetFollowingIdsInteractor getFollowingIdsInteractor;
  private final GetLocalStreamInteractor getStreamInteractor;
  private final StreamModelMapper streamModelMapper;
  private final Bus bus;
  private final BusPublisher busPublisher;

  private MainScreenView mainScreenView;
  private boolean hasBeenPaused = false;
  private UserModel userModel;
  private int unreadChannels = 0;
  private int unreadFollowChannels = 0;
  private StreamModel streamModel;

  @Inject public MainScreenPresenter(GetCurrentUserInteractor getCurrentUserInteractor,
      SendDeviceInfoInteractor sendDeviceInfoInteractor,
      SendShotEventStatsIneteractor sendShotEventStatsIneteractor,
      GetUserForAnalythicsByIdInteractor getUserForAnalythicsByIdInteractor,
      GetMutedStreamsInteractor getMutedStreamsInteractor,
      UnwatchStreamInteractor unwatchStreamInteractor, SessionRepository sessionRepository,
      UserModelMapper userModelMapper, @ActivityBadgeCount IntPreference badgeCount,
      GetFollowingInteractor followingInteractor,
      GetPrivateMessagesChannelsInteractor getPrivateMessagesChannelsInteractor,
      GetFollowingIdsInteractor getFollowingIdsInteractor,
      GetLocalStreamInteractor getStreamInteractor, StreamModelMapper streamModelMapper,
      @Main Bus bus, BusPublisher busPublisher) {
    this.getCurrentUserInteractor = getCurrentUserInteractor;
    this.sendDeviceInfoInteractor = sendDeviceInfoInteractor;
    this.sendShotEventStatsIneteractor = sendShotEventStatsIneteractor;
    this.getUserForAnalythicsByIdInteractor = getUserForAnalythicsByIdInteractor;
    this.getMutedStreamsInteractor = getMutedStreamsInteractor;
    this.unwatchStreamInteractor = unwatchStreamInteractor;
    this.sessionRepository = sessionRepository;
    this.userModelMapper = userModelMapper;
    this.badgeCount = badgeCount;
    this.followingInteractor = followingInteractor;
    this.getPrivateMessagesChannelsInteractor = getPrivateMessagesChannelsInteractor;
    this.getFollowingIdsInteractor = getFollowingIdsInteractor;
    this.getStreamInteractor = getStreamInteractor;
    this.streamModelMapper = streamModelMapper;
    this.bus = bus;
    this.busPublisher = busPublisher;
  }

  protected void setView(MainScreenView mainScreenView) {
    this.mainScreenView = mainScreenView;
  }

  public void initialize(MainScreenView mainScreenView) {
    setView(mainScreenView);
    this.loadCurrentUser();
    this.getFollows();
    this.getMuted();
    this.sendDeviceInfo();
    this.sendShotEventStats();
    this.updateActivityBadge();
    this.loadConnectedStream();
  }

  private void sendDeviceInfo() {
    sendDeviceInfoInteractor.sendDeviceInfo();
  }

  private void getFollows() {
    followingInteractor.synchronizeFollow();
  }

  private void getMuted() {
    getMutedStreamsInteractor.loadMutedStreamIds();
  }

  private void sendShotEventStats() {
    sendShotEventStatsIneteractor.sendShotsStats();
  }

  private void loadCurrentUser() {
    getCurrentUserInteractor.getCurrentUser(new Interactor.Callback<User>() {
      @Override public void onLoaded(User user) {
        userModel = userModelMapper.transform(user);
        mainScreenView.setUserData(userModel);
      }
    });
    loadRemoteCurrentUser();
  }

  private void loadRemoteCurrentUser() {
    if (userModel != null) {
      getUserForAnalythicsByIdInteractor.getCurrentUserForAnalythics(userModel.getIdUser(),
          new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
              if (sessionRepository.getCurrentUser() != null) {
                sessionRepository.getCurrentUser()
                    .setAnalyticsUserType(user.getAnalyticsUserType());
                sessionRepository.getCurrentUser()
                    .setReceivedReactions(user.getReceivedReactions());
              }
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                    /* no-op */
            }
          });
    }
  }

  private void loadConnectedStream() {
    if (sessionRepository.getCurrentUser().getIdWatchingStream() != null) {
      getStreamInteractor.loadStream(sessionRepository.getCurrentUser().getIdWatchingStream(),
          new GetLocalStreamInteractor.Callback() {
            @Override public void onLoaded(Stream stream) {
              streamModel = streamModelMapper.transform(stream);
              mainScreenView.showConnectController(streamModel);
            }
          });
    } else {
      mainScreenView.hideConnectController();
    }
  }

  private void loadChannelsForBadge() {
    getFollowingIdsInteractor.loadFollowingsIds(sessionRepository.getCurrentUserId(),
        new Interactor.Callback<List<String>>() {
          @Override public void onLoaded(final List<String> results) {
            getChannels(results);
          }
        });
  }

  private void getChannels(final List<String> results) {
    getPrivateMessagesChannelsInteractor.loadChannels(
        new Interactor.Callback<List<PrivateMessageChannel>>() {
          @Override public void onLoaded(List<PrivateMessageChannel> privateMessageChannels) {
            countUnreadsChannels(privateMessageChannels, results);
            publishChannelBadge();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
        /* no-op */
          }
        });
  }

  private void countUnreadsChannels(List<PrivateMessageChannel> privateMessageChannels,
      List<String> results) {
    unreadChannels = 0;
    unreadFollowChannels = 0;
    for (PrivateMessageChannel privateMessageChannel : privateMessageChannels) {
      if (!privateMessageChannel.isRead()) {
        unreadChannels++;
        if (results.contains(privateMessageChannel.getIdTargetUser())) {
          unreadFollowChannels++;
        }
      }
    }
  }

  public void publishChannelBadge() {
    busPublisher.post(new ChannelsBadgeChanged.Event(unreadChannels, unreadFollowChannels));
  }

  private void updateActivityBadge() {
    int activities = badgeCount.get();
    if (activities > 0) {
      mainScreenView.showActivityBadge(activities);
    }
  }

  @Override public void resume() {
    updateActivityBadge();
    loadChannelsForBadge();
    loadConnectedStream();
    bus.register(this);
    if (hasBeenPaused) {
      loadCurrentUser();
      sendShotEventStats();
    }
  }

  @Override public void pause() {
    bus.unregister(this);
    hasBeenPaused = true;
  }

  public void unwatchStream() {
    unwatchStreamInteractor.unwatchStream(new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        /* no-op */
      }
    });
  }

  @Override @Subscribe public void onBadgeChanged(BadgeChanged.Event event) {
    updateActivityBadge();
  }

  @Override @Subscribe public void onUnwatchDone(UnwatchDone.Event event) {
    mainScreenView.hideConnectController();
  }

  public void onControllerClick() {
    mainScreenView.goToTimeline(streamModel);
  }
}
