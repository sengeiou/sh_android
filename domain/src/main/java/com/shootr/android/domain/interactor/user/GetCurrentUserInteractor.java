package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import javax.inject.Inject;

public class GetCurrentUserInteractor implements Interactor {

    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final PostExecutionThread postExecutionThread;
    private Callback<User> callback;

    @Inject public GetCurrentUserInteractor(SessionRepository sessionRepository, @Local UserRepository localUserRepository,
      PostExecutionThread postExecutionThread) {
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void getCurrentUser(Callback<User> callback) throws Throwable {
        this.callback = callback;
        execute();
    }

    @Override public void execute() throws Throwable {
        loadCurrentUser();
    }

    private void loadCurrentUser() {
        String currentUserId = sessionRepository.getCurrentUserId();
        User user = localUserRepository.getUserById(currentUserId);
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
