package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.ShotRepository;
import javax.inject.Inject;

public class DeleteLocalShotsByStream implements Interactor {

    private final ShotRepository localShotRepository;
    private final PostExecutionThread postExecutionThread;
    private final InteractorHandler interactorHandler;
    private String idStream;
    private CompletedCallback completedCallback;

    @Inject
    public DeleteLocalShotsByStream(@Local ShotRepository localShotRepository, PostExecutionThread postExecutionThread,
      InteractorHandler interactorHandler) {
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
