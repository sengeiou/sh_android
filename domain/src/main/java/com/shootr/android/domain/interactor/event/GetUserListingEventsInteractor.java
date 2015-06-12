package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.LocaleProvider;
import java.util.List;
import javax.inject.Inject;

public class GetUserListingEventsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final EventSearchRepository localEventSearchRepository;
    private final EventSearchRepository remoteEventSearchRepository;
    private final SessionRepository sessionRepository;
    private final LocaleProvider localeProvider;

    private String idUser;
    private Callback<List<EventSearchResult>> callback;

    @Inject public GetUserListingEventsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local EventSearchRepository localEventRepositoty,
      @Remote EventSearchRepository remoteEventRepositoty, SessionRepository sessionRepository,
      LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localEventSearchRepository = localEventRepositoty;
        this.remoteEventSearchRepository = remoteEventRepositoty;
        this.sessionRepository = sessionRepository;
        this.localeProvider = localeProvider;
    }

    public void getUserListingEvents(Callback<List<EventSearchResult>> callback, String idUser){
        this.callback = callback;
        this.idUser = idUser;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        getUserListingEventsFromLocal();
        getUserListingEventsFromRemote();
    }

    private void getUserListingEventsFromRemote() {
        getUserListingEventsFromRepository(remoteEventSearchRepository);
    }

    public void getUserListingEventsFromLocal() {
        getUserListingEventsFromRepository(localEventSearchRepository);
    }

    public void getUserListingEventsFromRepository(EventSearchRepository eventRepository){
        String locale = localeProvider.getLocale();
        List<EventSearchResult> listingEvents = eventRepository.getEventsListing(idUser, locale);
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
