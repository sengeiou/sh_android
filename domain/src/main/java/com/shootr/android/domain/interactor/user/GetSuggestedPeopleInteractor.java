package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.SuggestedPeople;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class GetSuggestedPeopleInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;

    private Callback<List<SuggestedPeople>> callback;

    @Inject public GetSuggestedPeopleInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
    }

    public void obtainSuggestedPeople(Callback<List<SuggestedPeople>> callback){
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        obtainLocalSuggestedPeople();
        obtainRemoteSuggestedPeople();
    }

    private void obtainRemoteSuggestedPeople() {
        List<SuggestedPeople> suggestedPeople = remoteUserRepository.getSuggestedPeople();
        notifyResult(suggestedPeople);
    }

    private void obtainLocalSuggestedPeople() {
        //TODO real implementation (later)
    }

    private void notifyResult(final List<SuggestedPeople> suggestedPeople) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(suggestedPeople);
            }
        });
    }
}
