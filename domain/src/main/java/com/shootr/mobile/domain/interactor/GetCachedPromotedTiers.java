package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.user.PromotedTiers;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import javax.inject.Inject;

public class GetCachedPromotedTiers implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository streamRepository;

  private Callback<PromotedTiers> callback;

  @Inject public GetCachedPromotedTiers(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository streamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.streamRepository = streamRepository;
  }

  public void getPromotedTiers(
      Callback<PromotedTiers> callback) {
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    notify(streamRepository.getPromotedTiers());
  }

  private void notify(final PromotedTiers response) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(response);
      }
    });
  }
}
