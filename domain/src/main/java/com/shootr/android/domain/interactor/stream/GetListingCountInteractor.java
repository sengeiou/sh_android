package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.StreamRepository;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class GetListingCountInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamRepository localStreamRepository;
    private final StreamRepository remoteStreamRepository;

    private String idUser;
    private Callback<Integer> callback;

    @Inject public GetListingCountInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository localEventRepositoty,
      @Remote StreamRepository remoteEventRepositoty) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localEventRepositoty;
        this.remoteStreamRepository = remoteEventRepositoty;
    }

    public void loadListingCount(String idUser, Callback<Integer> callback){
        this.idUser = checkNotNull(idUser);
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadListingCountFromLocal();
        loadListingCountFromRemote();
    }

    private void loadListingCountFromRemote() {
        loadListingCountFromRepository(remoteStreamRepository);
    }

    private void loadListingCountFromLocal() {
        loadListingCountFromRepository(localStreamRepository);
    }

    private void loadListingCountFromRepository(StreamRepository streamRepository){
        try {
            Integer listingEventsNumber = streamRepository.getListingCount(idUser);
            notifyLoaded(listingEventsNumber);
        } catch (ServerCommunicationException networkError) {
            /* no-op */
        }
    }

    private void notifyLoaded(final Integer listingEventsNumber) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(listingEventsNumber);
            }
        });
    }
}
