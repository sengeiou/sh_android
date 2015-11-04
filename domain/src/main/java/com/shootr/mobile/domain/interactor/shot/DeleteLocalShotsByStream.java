package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.Local;
import javax.inject.Inject;

public class DeleteLocalShotsByStream implements Interactor {

    private final com.shootr.mobile.domain.repository.ShotRepository localShotRepository;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private String idStream;
    private CompletedCallback completedCallback;

    @Inject
    public DeleteLocalShotsByStream(@Local com.shootr.mobile.domain.repository.ShotRepository localShotRepository, com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler) {
        this.localShotRepository = localShotRepository;
        this.postExecutionThread = postExecutionThread;
        this.interactorHandler = interactorHandler;
    }

    public void deleteShot(String idStream, CompletedCallback completedCallback) {
        this.idStream = idStream;
        this.completedCallback = completedCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        localShotRepository.deleteShotsByStream(idStream);
        notifyLoaded();
    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }
}
