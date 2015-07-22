package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import com.shootr.android.domain.utils.TimeUtils;
import javax.inject.Inject;

public class SelectEventInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamRepository localStreamRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final WatchersRepository localWatchersRepository;
    private final SessionRepository sessionRepository;
    private final TimeUtils timeUtils;

    private String idSelectedEvent;
    private Callback<EventSearchResult> callback;

    @Inject public SelectEventInteractor(final InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository localStreamRepository,
      @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, @Local WatchersRepository localWatchersRepository,
      SessionRepository sessionRepository, TimeUtils timeUtils) {
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

    public void selectEvent(String idEvent, Callback<EventSearchResult> callback) {
        this.idSelectedEvent = idEvent;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        Stream selectedStream = getSelectedEvent();
        if (isSelectingCurrentWatchingEvent(currentUser)) {
            notifyLoaded(selectedStream);
        } else {
            User updatedUser = updateUserWithEventInfo(currentUser, selectedStream);

            sessionRepository.setCurrentUser(updatedUser);
            localUserRepository.putUser(updatedUser);
            notifyLoaded(selectedStream);
            remoteUserRepository.putUser(updatedUser);
        }
    }

    private Stream getSelectedEvent() {
        Stream selectedStream = localStreamRepository.getStreamById(idSelectedEvent);
        if (selectedStream == null) {
            throw new RuntimeException(String.format("Event with id %s not found in local repository", idSelectedEvent));
        }
        return selectedStream;
    }

    private boolean isSelectingCurrentWatchingEvent(User currentUser) {
        return idSelectedEvent.equals(currentUser.getIdWatchingEvent());
    }

    protected User updateUserWithEventInfo(User currentUser, Stream selectedStream) {
        currentUser.setIdWatchingEvent(selectedStream.getId());
        currentUser.setWatchingEventTitle(selectedStream.getTitle());
        currentUser.setJoinEventDate(getCurrentTime());
        return currentUser;
    }

    private long getCurrentTime() {
        return timeUtils.getCurrentTime();
    }

    private EventSearchResult attachWatchNumber(Stream stream) {
        EventSearchResult eventSearchResult = new EventSearchResult();
        eventSearchResult.setStream(stream);
        eventSearchResult.setWatchersNumber(localWatchersRepository.getWatchers(stream.getId()));
        return eventSearchResult;
    }
    private void notifyLoaded(final Stream selectedStream) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(attachWatchNumber(selectedStream));
            }
        });
    }
}
