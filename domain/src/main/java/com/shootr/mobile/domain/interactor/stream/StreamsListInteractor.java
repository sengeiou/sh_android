package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.stream.InternalStreamSearchRepository;
import com.shootr.mobile.domain.repository.stream.StreamListSynchronizationRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamSearchRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.domain.utils.TimeUtils;
import java.util.List;
import javax.inject.Inject;

public class StreamsListInteractor implements Interactor {

  private static final Long REFRESH_THRESHOLD_30_SECONDS_IN_MILLIS = 30L * 1000L;

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamSearchRepository remoteStreamSearchRepository;
  private final InternalStreamSearchRepository localStreamSearchRepository;
  private final StreamListSynchronizationRepository streamListSynchronizationRepository;
  private final SessionRepository sessionRepository;
  private final UserRepository localUserRepository;
  private final TimeUtils timeUtils;
  private final LocaleProvider localeProvider;
  private final StreamRepository localStreamRepository;

  private Callback<StreamSearchResultList> callback;
  private ErrorCallback errorCallback;

  @Inject public StreamsListInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote StreamSearchRepository remoteStreamSearchRepository,
      InternalStreamSearchRepository localStreamSearchRepository,
      StreamListSynchronizationRepository streamListSynchronizationRepository,
      @Local StreamRepository localStreamRepository,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository,
      TimeUtils timeUtils, LocaleProvider localeProvider) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteStreamSearchRepository = remoteStreamSearchRepository;
    this.localStreamSearchRepository = localStreamSearchRepository;
    this.streamListSynchronizationRepository = streamListSynchronizationRepository;
    this.sessionRepository = sessionRepository;
    this.localUserRepository = localUserRepository;
    this.timeUtils = timeUtils;
    this.localeProvider = localeProvider;
    this.localStreamRepository = localStreamRepository;
  }

  public void loadStreams(Callback<StreamSearchResultList> callback, ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    List<StreamSearchResult> localStreams =
        localStreamSearchRepository.getDefaultStreams(localeProvider.getLocale(),
            StreamMode.TYPES_STREAM);
    notifyLoaded(localStreams);

    Long currentTime = timeUtils.getCurrentTime();
    if (localStreams.isEmpty() || minimumRefreshTimePassed(currentTime)) {
      try {
        refreshStreams();
        streamListSynchronizationRepository.setStreamsRefreshDate(currentTime);
      } catch (ShootrException error) {
        notifyError(error);
      }
    }
  }

  protected void refreshStreams() {
    List<StreamSearchResult> remoteStreams =
        remoteStreamSearchRepository.getDefaultStreams(localeProvider.getLocale(),
            StreamMode.TYPES_STREAM);
    notifyLoaded(remoteStreams);
    localStreamSearchRepository.deleteDefaultStreams();
    localStreamSearchRepository.putDefaultStreams(remoteStreams);
  }

  private boolean minimumRefreshTimePassed(Long currentTime) {
    Long streamsLastRefreshDate = streamListSynchronizationRepository.getStreamsRefreshDate();
    Long minimumTimeToRefresh = streamsLastRefreshDate + REFRESH_THRESHOLD_30_SECONDS_IN_MILLIS;
    return minimumTimeToRefresh < currentTime;
  }

  //region Result
  private void notifyLoaded(final List<StreamSearchResult> results) {
    final StreamSearchResultList searchResultList =
        new StreamSearchResultList(results, getWatchingStreamsWithWatchNumber());
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(searchResultList);
      }
    });
  }

  private StreamSearchResult getWatchingStreamsWithWatchNumber() {
    String idUser = sessionRepository.getCurrentUserId();
    if (idUser == null) {
      idUser = sessionRepository.getCurrentUser().getIdUser();
    }
    User currentUser = localUserRepository.getUserById(idUser);
    if (currentUser != null) {
      String idWatchingStream = currentUser.getIdWatchingStream();
      if (idWatchingStream != null) {
        Stream stream =
            localStreamRepository.getStreamById(idWatchingStream, StreamMode.TYPES_STREAM);
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        streamSearchResult.setStream(stream);
        return streamSearchResult;
      } else {
        return null;
      }
    } else {
      return null;
    }
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
