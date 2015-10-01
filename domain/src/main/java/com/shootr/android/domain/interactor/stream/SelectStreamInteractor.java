package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import com.shootr.android.domain.utils.TimeUtils;
import javax.inject.Inject;

public class SelectStreamInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamRepository localStreamRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final WatchersRepository localWatchersRepository;
    private final SessionRepository sessionRepository;
    private final TimeUtils timeUtils;

    private String idSelectedStream;
    private Callback<StreamSearchResult> callback;

    @Inject public SelectStreamInteractor(final InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository localStreamRepository,
      @Local UserRepository localUserRepository, @Remote UserRepository remoteUserRepository,
      @Local WatchersRepository localWatchersRepository, SessionRepository sessionRepository, TimeUtils timeUtils) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.localWatchersRepository = localWatchersRepository;
        this.sessionRepository = sessionRepository;
        this.timeUtils = timeUtils;
    }
    //endregion

    public void selectStream(String idStream, Callback<StreamSearchResult> callback) {
        this.idSelectedStream = idStream;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        Stream selectedStream = getSelectedStream();
        if (isSelectingCurrentWatchingStream(currentUser)) {
            notifyLoaded(selectedStream);
        } else {
            User updatedUser = updateUserWithStreamInfo(currentUser, selectedStream);

            sessionRepository.setCurrentUser(updatedUser);
            localUserRepository.updateWatch(updatedUser);
            notifyLoaded(selectedStream);
            remoteUserRepository.updateWatch(updatedUser);
        }
    }

    private Stream getSelectedStream() {
        Stream selectedStream = localStreamRepository.getStreamById(idSelectedStream);
        if (selectedStream == null) {
            throw new RuntimeException(String.format("Stream with id %s not found in local repository", idSelectedStream));
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
        streamSearchResult.setWatchersNumber(localWatchersRepository.getWatchers(stream.getId()));
        return streamSearchResult;
    }
    private void notifyLoaded(final Stream selectedStream) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(attachWatchNumber(selectedStream));
            }
        });
    }
}
