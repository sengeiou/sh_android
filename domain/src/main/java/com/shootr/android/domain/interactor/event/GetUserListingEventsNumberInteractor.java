package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import javax.inject.Inject;

public class GetUserListingEventsNumberInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final EventRepository localEventRepository;
    private final EventRepository remoteEventRepository;
    private final SessionRepository sessionRepository;

    private String idUser;
    private Callback<Integer> callback;

    @Inject public GetUserListingEventsNumberInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local EventRepository localEventRepositoty,
      @Remote EventRepository remoteEventRepositoty, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localEventRepository = localEventRepositoty;
        this.remoteEventRepository = remoteEventRepositoty;
        this.sessionRepository = sessionRepository;
    }

    public void getUserListingEventsNumber(String idUser, Callback<Integer> callback){
        this.callback = callback;
        this.idUser = idUser;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        getUserListingEventsNumberFromLocal();
        getUserListingEventsNumberFromRemote();
    }

    private void getUserListingEventsNumberFromRemote() {
        getUserListingEventsNumberFromRepositoty(remoteEventRepository);
    }

    public void getUserListingEventsNumberFromLocal() {
        getUserListingEventsNumberFromRepositoty(localEventRepository);
    }

    public void getUserListingEventsNumberFromRepositoty(EventRepository eventRepository){
        Integer listingEventsNumber = eventRepository.getEventsListingNumber(idUser);
        notifyLoaded(listingEventsNumber);
    }

    private void notifyLoaded(final Integer listingEventsNumber) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(listingEventsNumber);
            }
        });
    }
}
