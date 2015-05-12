package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.UserRepository;

import javax.inject.Inject;

public class GetUserByUsernameInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository userRepository;
    private final PostExecutionThread postExecutionThread;
    private Callback<User> callback;

    private String username;

    @Inject
    public GetUserByUsernameInteractor(InteractorHandler interactorHandler,
                                       @Remote UserRepository userRepository, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.userRepository = userRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void searchUserByUsername(String username, Callback<User> callback){
        this.username = username;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Throwable {
        User user = userRepository.getUserByUsername(username);
        notifyResult(user);
    }

    private void notifyResult(final User user) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(user);
            }
        });
    }
}
