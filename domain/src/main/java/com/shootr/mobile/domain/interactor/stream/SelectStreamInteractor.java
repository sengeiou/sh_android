package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.repository.WatchersRepository;
import com.shootr.mobile.domain.utils.TimeUtils;
import javax.inject.Inject;

public class SelectStreamInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamRepository localStreamRepository;
    private final StreamRepository remoteStreamRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final WatchersRepository localWatchersRepository;
    private final SessionRepository sessionRepository;
    private final TimeUtils timeUtils;

    private String idSelectedStream;
    private Callback<StreamSearchResult> callback;
    private ErrorCallback errorCallback;

    @Inject
    public SelectStreamInteractor(final InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local StreamRepository localStreamRepository, @Remote StreamRepository remoteStreamRepository, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, @Local WatchersRepository localWatchersRepository, SessionRepository sessionRepository, TimeUtils timeUtils) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
        this.remoteStreamRepository = remoteStreamRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.localWatchersRepository = localWatchersRepository;
        this.sessionRepository = sessionRepository;
        this.timeUtils = timeUtils;
    }
    //endregion

    public void selectStream(String idStream, Callback<StreamSearchResult> callback, ErrorCallback errorCallback) {
        this.idSelectedStream = idStream;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        Stream selectedStream = getSelectedStream();
        if (isSelectingCurrentWatchingStream(currentUser)) {
            notifyLoaded(selectedStream);
        } else {

            if (selectedStream != null) {
                User updatedUser = updateUserWithStreamInfo(currentUser, selectedStream);

                sessionRepository.setCurrentUser(updatedUser);
                localUserRepository.updateWatch(updatedUser);
                notifyLoaded(selectedStream);
                remoteUserRepository.updateWatch(updatedUser);
            } else {
                notifyError(new ServerCommunicationException(new Throwable()));
            }
        }
    }

    private Stream getSelectedStream() {
        Stream selectedStream = localStreamRepository.getStreamById(idSelectedStream);
        if (selectedStream == null) {
            try {
                selectedStream = remoteStreamRepository.getStreamById(idSelectedStream);
            } catch (ServerCommunicationException error) {
                notifyError(error);
            }
        }
        return selectedStream;
    }

    private boolean isSelectingCurrentWatchingStream(User currentUser) {
        return idSelectedStream.equals(currentUser.getIdWatchingStream());
    }

    protected User updateUserWithStreamInfo(User currentUser, Stream selectedStream) {
        currentUser.setIdWatchingStream(selectedStream.getId());
        currentUser.setWatchingStreamTitle(selectedStream.getTitle());
        currentUser.setJoinStreamDate(getCurrentTime());
        return currentUser;
    }

    private long getCurrentTime() {
        return timeUtils.getCurrentTime();
    }

    private StreamSearchResult attachWatchNumber(Stream stream) {
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        streamSearchResult.setStream(stream);
        streamSearchResult.setFollowingWatchersNumber(localWatchersRepository.getWatchers(stream.getId()));
        return streamSearchResult;
    }

    private void notifyLoaded(final Stream selectedStream) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(attachWatchNumber(selectedStream));
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
}
