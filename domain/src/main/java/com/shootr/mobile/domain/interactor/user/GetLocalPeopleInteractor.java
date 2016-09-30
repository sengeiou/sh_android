package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class GetLocalPeopleInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository localUserRepository;
    private final PostExecutionThread postExecutionThread;
    private Interactor.Callback<List<User>> callback;

    @Inject
    public GetLocalPeopleInteractor(InteractorHandler interactorHandler, @Local UserRepository localUserRepository,
      PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.localUserRepository = localUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void obtainPeople(Callback<List<User>> callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        obtainLocalPeople();
    }

    private void obtainLocalPeople() {
        List<User> userList = localUserRepository.getPeople();
        notifyResult(userList);
    }

    private void notifyResult(final List<User> localPeople) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(localPeople);
            }
        });
    }
}
