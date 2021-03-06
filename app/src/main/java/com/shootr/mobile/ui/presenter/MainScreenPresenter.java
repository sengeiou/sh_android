package com.shootr.mobile.ui.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shootr.mobile.BuildConfig;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.data.repository.remote.cache.LogsCache;
import com.shootr.mobile.domain.bus.BadgeChanged;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.data.dagger.ApplicationContext;
import com.shootr.mobile.domain.bus.ChannelsBadgeChanged;
import com.shootr.mobile.domain.bus.UnwatchDone;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.GetPromotedTiersInteractor;
import com.shootr.mobile.domain.interactor.GetShootrEventsInteractor;
import com.shootr.mobile.domain.interactor.GetSocketInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.SendCacheQueueInteractor;
import com.shootr.mobile.domain.interactor.SubscribePromotedTiersInteractor;
import com.shootr.mobile.domain.interactor.device.SendDeviceInfoInteractor;
import com.shootr.mobile.domain.interactor.device.ShouldUpdateDeviceInfoInteractor;
import com.shootr.mobile.domain.interactor.shot.SendShootrEventStatsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetLocalStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.mobile.domain.interactor.timeline.privateMessage.GetPrivateMessagesChannelsInteractor;
import com.shootr.mobile.domain.interactor.user.GetCurrentUserInteractor;
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
import com.shootr.mobile.util.AnalyticsTool;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;

public class MainScreenPresenter implements Presenter, BadgeChanged.Receiver, UnwatchDone.Receiver,
    ChannelsBadgeChanged.Receiver {

  private final GetCurrentUserInteractor getCurrentUserInteractor;
  private final SendDeviceInfoInteractor sendDeviceInfoInteractor;
  private final SendShootrEventStatsInteractor sendShootrEventStatsInteractor;
  private final SendCacheQueueInteractor sendEventsOnQueueCacheInteractor;
  private final GetUserForAnalythicsByIdInteractor getUserForAnalythicsByIdInteractor;
  private final ShouldUpdateDeviceInfoInteractor shouldUpdateDeviceInfoInteractor;
  private final GetSocketInteractor getSocketInteractor;
  private final UnwatchStreamInteractor unwatchStreamInteractor;
  private final SessionRepository sessionRepository;
  private final UserModelMapper userModelMapper;
  private final IntPreference badgeCount;
  private final GetPrivateMessagesChannelsInteractor getPrivateMessagesChannelsInteractor;
  private final GetLocalStreamInteractor getStreamInteractor;
  private final GetShootrEventsInteractor getShootrEventsInteractor;
  private final GetPromotedTiersInteractor getPromotedTiersInteractor;
  private final StreamModelMapper streamModelMapper;
  private final Bus bus;
  private final BusPublisher busPublisher;
  private final Context context;
  private final LogsCache logsCache;
  private AnalyticsTool analyticsTool;

  private MainScreenView mainScreenView;
  private boolean hasBeenPaused = false;
  private UserModel userModel;
  private int unreadChannels = 0;
  private int unreadFollowChannels = 0;
  private StreamModel streamModel;

  private GoogleApiClient googleApiClient;

  @Inject public MainScreenPresenter(GetCurrentUserInteractor getCurrentUserInteractor,
      SendDeviceInfoInteractor sendDeviceInfoInteractor,
      SendShootrEventStatsInteractor sendShootrEventStatsInteractor,
      SendCacheQueueInteractor sendEventsOnQueueCacheInteractor,
      GetUserForAnalythicsByIdInteractor getUserForAnalythicsByIdInteractor,
      ShouldUpdateDeviceInfoInteractor getDeviceInfoInteractor,
      GetSocketInteractor getSocketInteractor, UnwatchStreamInteractor unwatchStreamInteractor,
      SessionRepository sessionRepository, UserModelMapper userModelMapper,
      @ActivityBadgeCount IntPreference badgeCount,
      GetPrivateMessagesChannelsInteractor getPrivateMessagesChannelsInteractor,
      GetLocalStreamInteractor getStreamInteractor,
      GetShootrEventsInteractor getShootrEventsInteractor,
      GetPromotedTiersInteractor getPromotedTiersInteractor,
      SubscribePromotedTiersInteractor subscribePromotedTiersInteractor,
      StreamModelMapper streamModelMapper, @Main Bus bus, BusPublisher busPublisher,
      @ApplicationContext Context context, LogsCache logsCache, AnalyticsTool analyticsTool) {
    this.getCurrentUserInteractor = getCurrentUserInteractor;
    this.sendDeviceInfoInteractor = sendDeviceInfoInteractor;
    this.sendShootrEventStatsInteractor = sendShootrEventStatsInteractor;
    this.sendEventsOnQueueCacheInteractor = sendEventsOnQueueCacheInteractor;
    this.getUserForAnalythicsByIdInteractor = getUserForAnalythicsByIdInteractor;
    this.shouldUpdateDeviceInfoInteractor = getDeviceInfoInteractor;
    this.getSocketInteractor = getSocketInteractor;
    this.unwatchStreamInteractor = unwatchStreamInteractor;
    this.sessionRepository = sessionRepository;
    this.userModelMapper = userModelMapper;
    this.badgeCount = badgeCount;
    this.getPrivateMessagesChannelsInteractor = getPrivateMessagesChannelsInteractor;
    this.getStreamInteractor = getStreamInteractor;
    this.getShootrEventsInteractor = getShootrEventsInteractor;
    this.getPromotedTiersInteractor = getPromotedTiersInteractor;
    this.streamModelMapper = streamModelMapper;
    this.bus = bus;
    this.busPublisher = busPublisher;
    this.context = context;
    this.logsCache = logsCache;
    this.analyticsTool = analyticsTool;
  }

  protected void setView(MainScreenView mainScreenView) {
    this.mainScreenView = mainScreenView;
  }

  public void initialize(MainScreenView mainScreenView) {
    setView(mainScreenView);
    this.loadCurrentUser();
    this.getRecentSearch();
    this.setupDeviceInfo();
    this.sendShotEventStats();
    this.sendEventsOnQueueCache();
    this.updateActivityBadge();
    this.loadConnectedStream();
    this.getChannels(false);
    this.getPromotedTiers();
  }

  private void setupDeviceInfo() {

    shouldUpdateDeviceInfoInteractor.getDeviceInfo(new Interactor.Callback<Boolean>() {
      @Override public void onLoaded(Boolean needsUpdate) {
        if (needsUpdate) {
          buildGoogleApiClient();
          startVerification();
        }
      }
    });
  }

  private void getPromotedTiers() {
    getPromotedTiersInteractor.getPromotedTiers(new Interactor.Callback<Boolean>() {
      @Override public void onLoaded(Boolean aBoolean) {
        /* no-op */
      }
    });
  }

  private void getRecentSearch() {
    getShootrEventsInteractor.synchronizeShootrEvents();
  }

  private void sendShotEventStats() {
    sendShootrEventStatsInteractor.sendShootrEvents();
  }

  private void sendEventsOnQueueCache() {
    sendEventsOnQueueCacheInteractor.sendCacheQueue();
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
                analyticsTool.setUser(sessionRepository.getCurrentUser());
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

  public void loadChannelsForBadge(final boolean localOnly) {
    //TODO
  }

  private void getChannels(boolean localOnly) {
    getPrivateMessagesChannelsInteractor.loadChannels(localOnly,
        new Interactor.Callback<List<PrivateMessageChannel>>() {
          @Override public void onLoaded(List<PrivateMessageChannel> privateMessageChannels) {
            countUnreadsChannels(privateMessageChannels);
            publishChannelBadge();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
        /* no-op */
          }
        });
  }

  private void countUnreadsChannels(List<PrivateMessageChannel> privateMessageChannels) {
    unreadChannels = 0;
    unreadFollowChannels = 0;
    for (PrivateMessageChannel privateMessageChannel : privateMessageChannels) {
      if (!privateMessageChannel.isRead()) {
        unreadChannels++;
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
    loadChannelsForBadge(false);
    loadConnectedStream();
    bus.register(this);
    if (hasBeenPaused) {
      loadCurrentUser();
      sendShotEventStats();
      sendEventsOnQueueCache();
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

  @Subscribe @Override public void onBadgeChanged(ChannelsBadgeChanged.Event event) {
    if (event.getUnreadFollowChannels() > 0) {
      mainScreenView.updateChannelBadge(event.getUnreadChannels(), true);
    } else {
      mainScreenView.updateChannelBadge(event.getUnreadChannels(), false);
    }
  }

  private synchronized void buildGoogleApiClient() {
    googleApiClient = new GoogleApiClient.Builder(context).addApi(SafetyNet.API).build();
    googleApiClient.connect();
  }

  private byte[] getRequestNonce() {

    String data = String.valueOf(System.currentTimeMillis());

    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    byte[] bytes = new byte[24];
    Random random = new SecureRandom();
    random.nextBytes(bytes);
    try {
      byteStream.write(bytes);
      byteStream.write(data.getBytes());
      byteStream.write(sessionRepository.getCurrentUserId().getBytes());
    } catch (IOException e) {
      return null;
    }

    return byteStream.toByteArray();
  }

  private void startVerification() {
    final byte[] nonce = getRequestNonce();
    SafetyNet.getClient(context)
        .attest(nonce, BuildConfig.SafetyNetApiKey)
        .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.AttestationResponse>() {

          @Override public void onSuccess(SafetyNetApi.AttestationResponse attestationResponse) {
            String jwsResult = attestationResponse.getJwsResult();
            sendDeviceInfoInteractor.sendDeviceInfo(jwsResult);
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            sendDeviceInfoInteractor.sendDeviceInfo(null);
          }
        });
  }
}
