package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import javax.inject.Inject;

public class GetReplyParentInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository remoteShotRepository;
    private String parentId;
    private Callback<Shot> callback;

    @Inject
    public GetReplyParentInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote ShotRepository remoteShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void loadReplyParent(String parentId, Callback<Shot> callback) {
        this.parentId = parentId;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        Shot replyParent = remoteShotRepository.getShot(parentId);

        notifyLoaded(replyParent);
    }

    private void notifyLoaded(final Shot result) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(result);
            }
        });
    }
}
