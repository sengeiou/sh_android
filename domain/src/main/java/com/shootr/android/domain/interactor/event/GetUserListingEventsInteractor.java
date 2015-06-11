package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class GetUserListingEventsInteractor implements Interactor {

    public static final Integer MAX_NUMBER_OF_LISTING_EVENTS = 100;
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final EventRepository localEventRepository;
    private final EventRepository remoteEventRepository;
    private final SessionRepository sessionRepository;

    private String idUser;
    private String listingIdUser;
    private String locale;
    private Callback<List<EventSearchResult>> callback;

    @Inject public GetUserListingEventsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local EventRepository localEventRepositoty,
      @Remote EventRepository remoteEventRepositoty, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localEventRepository = localEventRepositoty;
        this.remoteEventRepository = remoteEventRepositoty;
        this.sessionRepository = sessionRepository;
    }

    public void getUserListingEvents(Callback<List<EventSearchResult>> callback, String listingIdUser, String locale){
        this.callback = callback;
        this.idUser = sessionRepository.getCurrentUserId();
        this.listingIdUser = listingIdUser;
        this.locale = locale;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        getUserListingEventsFromLocal();
        getUserListingEventsFromRemote();
    }

    private void getUserListingEventsFromRemote() {
        getUserListingEventsFromRepository(remoteEventRepository);
    }

    public void getUserListingEventsFromLocal() {
        getUserListingEventsFromRepository(localEventRepository);
    }

    public void getUserListingEventsFromRepository(EventRepository eventRepository){
        List<EventSearchResult> listingEvents = eventRepository.getEventsListing(idUser, listingIdUser, locale,
          MAX_NUMBER_OF_LISTING_EVENTS);
        notifyLoaded(listingEvents);
    }

    private void notifyLoaded(final List<EventSearchResult> listingEvents) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(listingEvents);
            }
        });
    }
}
