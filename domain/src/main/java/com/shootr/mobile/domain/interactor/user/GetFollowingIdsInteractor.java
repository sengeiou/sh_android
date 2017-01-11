package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class GetFollowingIdsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final UserRepository localUserRepository;

  private String idUser;
  private Callback<List<String>> callback;


  @Inject public GetFollowingIdsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local UserRepository localUserRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localUserRepository = localUserRepository;
  }

  public void loadFollowingsIds(String idUser, Callback<List<String>> callback) {
    this.callback = callback;
    this.idUser = idUser;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    notifyLoaded(localUserRepository.getFollowingIds(idUser));
  }

  private void notifyLoaded(final List<String> results) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(results);
      }
    });
  }
}
