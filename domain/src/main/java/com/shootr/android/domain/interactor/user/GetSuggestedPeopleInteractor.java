package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.SuggestedPeople;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetSuggestedPeopleInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;

    private Callback<List<SuggestedPeople>> callback;
    private ErrorCallback errorCallback;

    @Inject public GetSuggestedPeopleInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
    }

    public void obtainSuggestedPeople(Callback<List<SuggestedPeople>> callback, ErrorCallback errorCallback){
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        obtainLocalSuggestedPeople();
        try {
            obtainRemoteSuggestedPeople();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void obtainRemoteSuggestedPeople() {
        List<SuggestedPeople> suggestedPeople = remoteUserRepository.getSuggestedPeople();
        List<SuggestedPeople> suggestedPeoples = retainAllFollowedUsers(suggestedPeople,
          remoteUserRepository.getPeople());
        notifyResult(suggestedPeoples);
    }

    private void obtainLocalSuggestedPeople() {
        List<SuggestedPeople> suggestedPeople = localUserRepository.getSuggestedPeople();
        List<SuggestedPeople> suggestedPeoples = retainAllFollowedUsers(suggestedPeople, localUserRepository.getPeople());
        notifyResult(suggestedPeoples);
    }

    private List<SuggestedPeople> retainAllFollowedUsers(List<SuggestedPeople> suggestedPeople, List<User> people) {
        List<SuggestedPeople> suggestedPeoples = new ArrayList<>();
        for (SuggestedPeople suggested : suggestedPeople) {
            if(!people.contains(suggested.getUser()) && !suggestedPeoples.contains(suggested))
                suggestedPeoples.add(suggested);
        }
        return suggestedPeoples;
    }

    private void notifyResult(final List<SuggestedPeople> suggestedPeople) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(suggestedPeople);
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