package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Fast;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import javax.inject.Inject;

public class GetCurrentUserInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;

    private Callback<User> callback;

    @Inject public GetCurrentUserInteractor(@Fast InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository,
      @Local UserRepository localUserRepository) {
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
