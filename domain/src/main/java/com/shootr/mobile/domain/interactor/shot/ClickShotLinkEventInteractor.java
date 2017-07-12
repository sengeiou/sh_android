package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.ShootrEvent;
import com.shootr.mobile.domain.model.shot.ShootrEventType;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.shot.ShootrEventRepository;
import javax.inject.Inject;

public class ClickShotLinkEventInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShootrEventRepository localShootrEventRepository;

  private CompletedCallback completedCallback;
  private String idShot;

  @Inject public ClickShotLinkEventInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local ShootrEventRepository shootrEventRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localShootrEventRepository = shootrEventRepository;
  }

  public void countClickLinkEvent(String idShot, CompletedCallback completedCallback) {
    this.idShot = idShot;
    this.completedCallback = completedCallback;
    this.interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    countClickLinkEvent();
    notifyCompleted();
  }

  private void countClickLinkEvent() {
    ShootrEvent shootrEvent = new ShootrEvent();
    shootrEvent.setId(idShot);
    shootrEvent.setType(ShootrEventType.SHOT_LINK_CLICK);
    localShootrEventRepository.clickLink(shootrEvent);
  }

  private void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }
}
