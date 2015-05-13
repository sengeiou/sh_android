package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.InvalidGetUserException;
import com.shootr.android.domain.exception.ShootrException;
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
    private ErrorCallback errorCallback;

    private String username;

    @Inject
    public GetUserByUsernameInteractor(InteractorHandler interactorHandler,
                                       @Remote UserRepository userRepository,
                                       PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.userRepository = userRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void searchUserByUsername(String username, Callback<User> callback,
                                     ErrorCallback errorCallback){
        this.username = username;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Throwable {
        User user = userRepository.getUserByUsername(username);
        if(user == null){
            notifyError(new InvalidGetUserException("Can't find a user with that username"));
        }else{
            notifyResult(user);
        }
    }

    private void notifyResult(final User user) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(user);
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
