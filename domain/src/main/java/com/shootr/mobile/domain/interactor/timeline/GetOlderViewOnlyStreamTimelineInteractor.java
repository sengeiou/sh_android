package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetOlderViewOnlyStreamTimelineInteractor implements Interactor {

  private final SessionRepository sessionRepository;
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalShotRepository remoteShotRepository;
  private final StreamRepository localStreamRepository;
  private final ContributorRepository contributorRepository;
  private final UserRepository localUserRepository;

  private Long currentOldestDate;
  private Interactor.Callback<Timeline> callback;
  private Interactor.ErrorCallback errorCallback;

  @Inject public GetOlderViewOnlyStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, SessionRepository sessionRepository,
      ExternalShotRepository remoteShotRepository, @Local StreamRepository localStreamRepository,
      @Local ContributorRepository contributorRepository,
      @Local UserRepository localUserRepository) {
    this.sessionRepository = sessionRepository;
    this.remoteShotRepository = remoteShotRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localStreamRepository = localStreamRepository;
    this.contributorRepository = contributorRepository;
    this.localUserRepository = localUserRepository;
  }

  public void loadOlderStreamTimeline(Long currentOldestDate,
      Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback) {
    this.currentOldestDate = currentOldestDate;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      StreamTimelineParameters timelineParameters = buildTimelineParameters();
      ArrayList<Shot> olderShots = new ArrayList<>();
      olderShots.addAll(remoteShotRepository.getShotsForStreamTimeline(timelineParameters));
      sortShotsByPublishDate(olderShots);
      filterViewOnlyTimeline(olderShots, timelineParameters.getStreamId());
      notifyTimelineFromShots(olderShots);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private StreamTimelineParameters buildTimelineParameters() {
    Stream visibleStream = getVisibleStream();
    return StreamTimelineParameters.builder() //
        .forStream(visibleStream) //
        .maxDate(currentOldestDate) //
        .realTime(true) //
        .build();
  }

  private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
    Collections.sort(remoteShots, new Shot.NewerAboveComparator());
    return remoteShots;
  }

  private void filterViewOnlyTimeline(ArrayList<Shot> olderShots, String idStream) {
    User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
    List<Contributor> contributors = contributorRepository.getContributors(idStream);
    ArrayList<Shot> shots = new ArrayList<>();
    shots.addAll(olderShots);
    olderShots.clear();
    for (Shot shot : shots) {
      if (isContributorShot(contributors, shot) || isCurrentUserAuthor(currentUser.getIdUser(),
          shot) || isHolderShot(shot, idStream) || isFollowingShotAuthor(shot)) {
        olderShots.add(shot);
      }
    }
  }

  private boolean isCurrentUserAuthor(String idUser, Shot shot) {
    return shot.getUserInfo().getIdUser().equals(idUser);
  }

  private boolean isHolderShot(Shot shot, String idStream) {
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
  private void notifyTimelineFromShots(List<Shot> shots) {
    Timeline timeline = buildTimeline(shots);
    notifyLoaded(timeline);
  }

  private Timeline buildTimeline(List<Shot> shots) {
    Timeline timeline = new Timeline();
    timeline.setShots(shots);
    return timeline;
  }

  private Stream getVisibleStream() {
    User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
    String visibleStreamId = currentUser.getIdWatchingStream();
    if (visibleStreamId != null) {
      return localStreamRepository.getStreamById(visibleStreamId, StreamMode.TYPES_STREAM);
    }
    return null;
  }

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
