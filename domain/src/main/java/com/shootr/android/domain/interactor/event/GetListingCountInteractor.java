package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import javax.inject.Inject;

public class GetListingCountInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final EventRepository localEventRepository;
    private final EventRepository remoteEventRepository;

    private String idUser;
    private Callback<Integer> callback;

    @Inject public GetListingCountInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local EventRepository localEventRepositoty,
      @Remote EventRepository remoteEventRepositoty) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localEventRepository = localEventRepositoty;
        this.remoteEventRepository = remoteEventRepositoty;
    }

    public void loadListingCount(String idUser, Callback<Integer> callback){
        this.callback = callback;
        this.idUser = idUser;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        loadListingCountFromLocal();
        loadListingCountFromRemote();
    }

    private void loadListingCountFromRemote() {
        loadListingCountFromRepositoty(remoteEventRepository);
    }

    public void loadListingCountFromLocal() {
        loadListingCountFromRepositoty(localEventRepository);
    }

    public void loadListingCountFromRepositoty(EventRepository eventRepository){
        Integer listingEventsNumber = eventRepository.getListingCount(idUser);
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
