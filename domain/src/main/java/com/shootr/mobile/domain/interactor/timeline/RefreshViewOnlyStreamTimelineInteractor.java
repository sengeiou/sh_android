package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class RefreshViewOnlyStreamTimelineInteractor implements Interactor {

  private static final Long REAL_TIME_INTERVAL = 60 * 1000L;

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShootrTimelineService shootrTimelineService;
  private final LocaleProvider localeProvider;
  private final UserRepository localUserRepository;
  private final ContributorRepository contributorRepository;
  private final SessionRepository sessionRepository;
  private final StreamRepository localStreamRepository;

  private Interactor.Callback<Timeline> callback;
  private Interactor.ErrorCallback errorCallback;
  private String idStream;
  private Long lastRefreshDate;
  private Boolean goneBackground;
  private Integer calls = 0;

  @Inject public RefreshViewOnlyStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ShootrTimelineService shootrTimelineService,
      LocaleProvider localeProvider, @Local UserRepository localUserRepository,
      @Local ContributorRepository contributorRepository, SessionRepository sessionRepository,
      @Local StreamRepository localStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.shootrTimelineService = shootrTimelineService;
    this.localeProvider = localeProvider;
    this.localUserRepository = localUserRepository;
    this.contributorRepository = contributorRepository;
    this.sessionRepository = sessionRepository;
    this.localStreamRepository = localStreamRepository;
  }

  public void refreshStreamTimeline(String streamId, Long lastRefreshDate, Boolean goneBackground,
      Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.idStream = streamId;
    this.lastRefreshDate = lastRefreshDate;
    this.goneBackground = goneBackground;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    executeSynchronized();
  }

  private synchronized void executeSynchronized() {
    try {
      long timestamp = new Date().getTime();
      Boolean isRealTime =
          calls != 0 && !(goneBackground && timestamp - lastRefreshDate >= REAL_TIME_INTERVAL);
      Timeline timeline = shootrTimelineService.refreshTimelinesForStream(idStream, false, isRealTime);
      timeline.setFirstCall(!isRealTime);
      filterViewOnlyTimeline(timeline);
      notifyLoaded(timeline);
      incrementCalls();
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  protected void incrementCalls() {
    calls++;
  }

  private void filterViewOnlyTimeline(Timeline timeline) {
    User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
    List<Contributor> contributors = contributorRepository.getContributors(idStream);
    List<Shot> shots = new ArrayList<>();
    for (Shot shot : timeline.getShots()) {
      if (isContributorShot(contributors, shot) || isCurrentUserAuthor(currentUser.getIdUser(),
          shot) || isHolderShot(shot) || isFollowingShotAuthor(shot)) {
        shots.add(shot);
      }
    }
    timeline.setShots(shots);
  }

  private boolean isCurrentUserAuthor(String idUser, Shot shot) {
    return shot.getUserInfo().getIdUser().equals(idUser);
  }

  private boolean isHolderShot(Shot shot) {
    Stream stream = localStreamRepository.getStreamById(idStream, StreamMode.TYPES_STREAM);
    return shot.getUserInfo().getIdUser().equals(stream.getAuthorId());
  }

  private boolean isContributorShot(List<Contributor> contributors, Shot shot) {
    for (Contributor contributor : contributors) {
      if (contributor.getIdUser().equals(shot.getUserInfo().getIdUser())) {
        return true;
      }
    }
    return false;
  }

  private boolean isFollowingShotAuthor(Shot shot) {
    return localUserRepository.isFollowing(shot.getUserInfo().getIdUser());
  }

  //region Result
  private void notifyLoaded(final Timeline timeline) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(timeline);
      }
    });
  }

  private void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
  //endregion
}
