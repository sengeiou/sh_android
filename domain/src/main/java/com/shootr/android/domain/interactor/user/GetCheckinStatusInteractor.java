package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import javax.inject.Inject;

public class GetCheckinStatusInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;
    private String idEvent;
    private Callback<Boolean> callback;

    @Inject
    public GetCheckinStatusInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
                                      @Local UserRepository localUserRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void loadCheckinStatus(String idEvent, Callback<Boolean> callback) {
        this.idEvent = idEvent;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        boolean checkedIn = idEvent.equals(currentUser.getIdCheckedEvent());
        notifyResult(checkedIn);
    }

    private void notifyResult(final boolean checkedIn) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(checkedIn);
            }
        });
    }
}
