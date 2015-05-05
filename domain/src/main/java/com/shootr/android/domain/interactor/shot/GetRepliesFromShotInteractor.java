package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetRepliesFromShotInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;
    private String shotId;
    private Callback<List<Shot>> callback;

    @Inject
    public GetRepliesFromShotInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void loadReplies(String shotId, Callback<List<Shot>> callback) {
        this.shotId = shotId;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        List<Shot> localReplies = localShotRepository.getReplies(shotId);
        if (!localReplies.isEmpty()) {
            notifyLoaded(orderShots(localReplies));
        }
        List<Shot> updatedReplies = remoteShotRepository.getReplies(shotId);
        if (!updatedReplies.isEmpty()) {
            notifyLoaded(orderShots(updatedReplies));
        }
    }

    private List<Shot> orderShots(List<Shot> replies) {
        Collections.sort(replies, new Shot.NewerBelowComparator());
        return replies;
    }

    private void notifyLoaded(final List<Shot> result) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(result);
            }
        });
    }
}
