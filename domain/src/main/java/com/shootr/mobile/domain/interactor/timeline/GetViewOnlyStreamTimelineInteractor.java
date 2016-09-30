package com.shootr.mobile.domain.interactor.timeline;

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
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import javax.inject.Inject;

public class GetViewOnlyStreamTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalShotRepository localShotRepository;
  private final UserRepository localUserRepository;
  private final ContributorRepository localContributorRepository;
  private final StreamRepository localStreamRepository;
  private final SessionRepository sessionRepository;
  private String idStream;
  private Callback callback;
  private Boolean goneBackground;

  @Inject public GetViewOnlyStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalShotRepository localShotRepository,
      @Local UserRepository localUserRepository,
      @Local ContributorRepository localContributorRepository,
      @Local StreamRepository localStreamRepository, SessionRepository sessionRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localShotRepository = localShotRepository;
    this.localUserRepository = localUserRepository;
    this.localContributorRepository = localContributorRepository;
    this.localStreamRepository = localStreamRepository;
    this.sessionRepository = sessionRepository;
  }

  public void loadStreamTimeline(String idStream, Boolean goneBackground,
      Callback<Timeline> callback) {
    this.idStream = idStream;
    this.goneBackground = goneBackground;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    loadLocalTimeline();
  }

  private void loadLocalTimeline() {
    List<Shot> shotsForStreamTimeline = getShots();
    TreeSet<String> visibleIdUsers = getVisibleIdUsers();
    List<Shot> filteredShots = filterShotsByRelevantUsers(shotsForStreamTimeline, visibleIdUsers);
    List<Shot> shots = sortShotsByPublishDate(filteredShots);
    notifyTimelineFromShots(shots);
  }

  private List<Shot> getShots() {
    StreamTimelineParameters streamTimelineParameters = buildParameters();
    return localShotRepository.getShotsForStreamTimeline(streamTimelineParameters);
  }

  private List<Shot> filterShotsByRelevantUsers(List<Shot> shotsForStreamTimeline,
      TreeSet<String> visibleIdUsers) {
    List<Shot> filteredShots = new ArrayList<>();
    for (Shot shot : shotsForStreamTimeline) {
      if (visibleIdUsers.contains(shot.getUserInfo().getIdUser())) {
        filteredShots.add(shot);
      }
    }
    return filteredShots;
  }

  private TreeSet<String> getVisibleIdUsers() {
    List<User> people = localUserRepository.getPeople();
    List<Contributor> contributors = localContributorRepository.getContributors(idStream);
    Stream stream = localStreamRepository.getStreamById(idStream, StreamMode.TYPES_STREAM);
    TreeSet<String> visibleIdUsers = new TreeSet<>();
    visibleIdUsers.add(stream.getAuthorId());
    visibleIdUsers.add(sessionRepository.getCurrentUserId());
    for (User user : people) {
      visibleIdUsers.add(user.getIdUser());
    }
    for (Contributor contributor : contributors) {
      visibleIdUsers.add(contributor.getIdUser());
    }
    return visibleIdUsers;
  }

  private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
    Collections.sort(remoteShots, new Shot.NewerAboveComparator());
    return remoteShots;
  }

  private StreamTimelineParameters buildParameters() {
    return StreamTimelineParameters.builder().forStream(idStream).realTime(goneBackground).build();
  }

  private void notifyTimelineFromShots(List<Shot> shots) {
    Timeline timeline = buildTimeline(shots);
    notifyLoaded(timeline);
  }

  private Timeline buildTimeline(List<Shot> shots) {
    Timeline timeline = new Timeline();
    timeline.setShots(shots);
    return timeline;
  }

  private void notifyLoaded(final Timeline timeline) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(timeline);
      }
    });
  }
}
