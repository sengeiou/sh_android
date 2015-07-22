package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.StreamSearchResultList;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamListSynchronizationRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.StreamSearchRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import com.shootr.android.domain.utils.LocaleProvider;
import com.shootr.android.domain.utils.TimeUtils;
import java.util.List;
import javax.inject.Inject;

public class StreamsListInteractor implements Interactor {

    private static final Long REFRESH_THRESHOLD_30_SECONDS_IN_MILLIS = 30L * 1000L;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamSearchRepository remoteStreamSearchRepository;
    private final StreamSearchRepository localStreamSearchRepository;
    private final StreamListSynchronizationRepository streamListSynchronizationRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final TimeUtils timeUtils;
    private final LocaleProvider localeProvider;
    private final WatchersRepository watchersRepository;
    private final StreamRepository localStreamRepository;

    private Callback<StreamSearchResultList> callback;
    private ErrorCallback errorCallback;

    @Inject
    public StreamsListInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote StreamSearchRepository remoteStreamSearchRepository,
      @Local StreamSearchRepository localStreamSearchRepository,
      StreamListSynchronizationRepository streamListSynchronizationRepository,
      @Local StreamRepository localStreamRepository, @Local WatchersRepository watchersRepository,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository, TimeUtils timeUtils,
      LocaleProvider localeProvider) {
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

    public void loadStreams(Callback<StreamSearchResultList> callback, ErrorCallback errorCallback) {
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
            } catch (ShootrException error) {
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
        final StreamSearchResultList searchResultList =
          new StreamSearchResultList(results, getWatchingStreamsWithWatchNumber());
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
            Stream stream = localStreamRepository.getStreamById(idWatchingStream);
            Integer watchers = watchersRepository.getWatchers(idWatchingStream);
            StreamSearchResult streamSearchResult = new StreamSearchResult();
            streamSearchResult.setStream(stream);
            streamSearchResult.setWatchersNumber(watchers);
            return streamSearchResult;
        } else {
            return null;
        }
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }
    //endregion
}
