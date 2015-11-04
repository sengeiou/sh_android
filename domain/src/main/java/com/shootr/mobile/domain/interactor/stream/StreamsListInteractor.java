package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.utils.TimeUtils;
import java.util.List;
import javax.inject.Inject;

public class StreamsListInteractor implements Interactor {

    private static final Long REFRESH_THRESHOLD_30_SECONDS_IN_MILLIS = 30L * 1000L;

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.StreamSearchRepository remoteStreamSearchRepository;
    private final com.shootr.mobile.domain.repository.StreamSearchRepository localStreamSearchRepository;
    private final com.shootr.mobile.domain.repository.StreamListSynchronizationRepository
      streamListSynchronizationRepository;
    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final TimeUtils timeUtils;
    private final com.shootr.mobile.domain.utils.LocaleProvider localeProvider;
    private final com.shootr.mobile.domain.repository.WatchersRepository watchersRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;

    private Callback<com.shootr.mobile.domain.StreamSearchResultList> callback;
    private ErrorCallback errorCallback;

    @Inject
    public StreamsListInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.StreamSearchRepository remoteStreamSearchRepository,
      @Local com.shootr.mobile.domain.repository.StreamSearchRepository localStreamSearchRepository,
      com.shootr.mobile.domain.repository.StreamListSynchronizationRepository streamListSynchronizationRepository,
      @Local com.shootr.mobile.domain.repository.StreamRepository localStreamRepository, @Local
    com.shootr.mobile.domain.repository.WatchersRepository watchersRepository,
      com.shootr.mobile.domain.repository.SessionRepository sessionRepository, @Local UserRepository localUserRepository, TimeUtils timeUtils,
      com.shootr.mobile.domain.utils.LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteStreamSearchRepository = remoteStreamSearchRepository;
        this.localStreamSearchRepository = localStreamSearchRepository;
        this.streamListSynchronizationRepository = streamListSynchronizationRepository;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.timeUtils = timeUtils;
        this.localeProvider = localeProvider;
        this.watchersRepository = watchersRepository;
        this.localStreamRepository = localStreamRepository;
    }

    public void loadStreams(Callback<com.shootr.mobile.domain.StreamSearchResultList> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        List<StreamSearchResult> localStreams = localStreamSearchRepository.getDefaultStreams(localeProvider.getLocale());
        notifyLoaded(localStreams);

        Long currentTime = timeUtils.getCurrentTime();
        if (localStreams.isEmpty() || minimumRefreshTimePassed(currentTime)) {
            try {
                refreshStreams();
                streamListSynchronizationRepository.setStreamsRefreshDate(currentTime);
            } catch (com.shootr.mobile.domain.exception.ShootrException error) {
                notifyError(error);
            }
        }
    }

    protected void refreshStreams() {
        List<StreamSearchResult> remoteStreams = remoteStreamSearchRepository.getDefaultStreams(localeProvider.getLocale());
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
        final com.shootr.mobile.domain.StreamSearchResultList searchResultList =
          new com.shootr.mobile.domain.StreamSearchResultList(results, getWatchingStreamsWithWatchNumber());
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(searchResultList);
            }
        });
    }

    private StreamSearchResult getWatchingStreamsWithWatchNumber() {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        String idWatchingStream = currentUser.getIdWatchingStream();
        if (idWatchingStream != null) {
            com.shootr.mobile.domain.Stream stream = localStreamRepository.getStreamById(idWatchingStream);
            Integer watchers = watchersRepository.getWatchers(idWatchingStream);
            StreamSearchResult streamSearchResult = new StreamSearchResult();
            streamSearchResult.setStream(stream);
            streamSearchResult.setWatchersNumber(watchers);
            return streamSearchResult;
        } else {
            return null;
        }
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }
    //endregion
}
