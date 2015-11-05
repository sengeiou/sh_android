package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Fast;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import javax.inject.Inject;

public class GetCurrentUserInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;

    private Callback<User> callback;

    @Inject
    public GetCurrentUserInteractor(@Fast InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void getCurrentUser(Callback<User> callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadCurrentUser();
    }

    private void loadCurrentUser() {
        String currentUserId = sessionRepository.getCurrentUserId();
        User user = localUserRepository.getUserById(currentUserId);
        if (user == null) {
            throw new IllegalStateException("Current user can't be null when using this Interactor");
        }
        notifyResult(user);
    }

    private void notifyResult(final User user) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(user);
            }
        });
    }
}
