package com.shootr.mobile.ui.presenter.interactorwrapper;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.timeline.privateMessage.GetOlderPrivateMessageChannelTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.privateMessage.GetPrivateMessageTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.privateMessage.RefreshPrivateMessageTimelineInteractor;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;
import javax.inject.Inject;

public class PrivateMessageChannelTimelineInteractorWrapper {

  private final RefreshPrivateMessageTimelineInteractor refreshPrivateMessageTimelineInteractor;
  private final GetPrivateMessageTimelineInteractor getPrivateMessageTimelineInteractor;
  private final GetOlderPrivateMessageChannelTimelineInteractor
      getOlderPrivateMessageChannelTimelineInteractor;

  @Inject public PrivateMessageChannelTimelineInteractorWrapper(
      RefreshPrivateMessageTimelineInteractor refreshPrivateMessageTimelineInteractor,
      GetPrivateMessageTimelineInteractor getPrivateMessageTimelineInteractor,
      GetOlderPrivateMessageChannelTimelineInteractor getOlderPrivateMessageChannelTimelineInteractor) {
    this.refreshPrivateMessageTimelineInteractor = refreshPrivateMessageTimelineInteractor;
    this.getPrivateMessageTimelineInteractor = getPrivateMessageTimelineInteractor;
    this.getOlderPrivateMessageChannelTimelineInteractor =
        getOlderPrivateMessageChannelTimelineInteractor;
  }

  public void loadTimeline(String idChannel, String idTargetuser, Boolean hasBeenPaused,
      Boolean loadFromServer, Interactor.Callback<PrivateMessageTimeline> callback) {
    getPrivateMessageTimelineInteractor.loadStreamTimeline(idChannel, idTargetuser, hasBeenPaused,
        loadFromServer, callback);
  }

  public void refreshTimeline(String idChannel, String idTargetUser, Long lastRefreshDate,
      Boolean hasBeenPaused, Interactor.Callback<PrivateMessageTimeline> callback,
      Interactor.ErrorCallback errorCallback) {
    refreshPrivateMessageTimelineInteractor.refreshStreamTimeline(idChannel, idTargetUser,
        lastRefreshDate, hasBeenPaused, callback, errorCallback);
  }

  public void obtainOlderTimeline(String idTargetUser, Long currentOldestDate,
      Interactor.Callback<PrivateMessageTimeline> callback,
      Interactor.ErrorCallback errorCallback) {

    getOlderPrivateMessageChannelTimelineInteractor.loadOlderTimeline(idTargetUser,
        currentOldestDate, callback, errorCallback);
  }
}
