package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.ShotFailed;
import com.shootr.mobile.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.util.List;
import javax.inject.Inject;

public class NewMessageBarPresenter implements Presenter, ShotFailed.Receiver {

  private final GetDraftsInteractor getDraftsInteractor;
  private final ErrorMessageFactory errorMessageFactory;
  private final Bus bus;

  private NewShotBarView view;
  private String idChannel;
  private boolean hasBeenPaused;

  @Inject public NewMessageBarPresenter(GetDraftsInteractor getDraftsInteractor,
      ErrorMessageFactory errorMessageFactory, @Main Bus bus) {
    this.getDraftsInteractor = getDraftsInteractor;
    this.errorMessageFactory = errorMessageFactory;
    this.bus = bus;
  }

  public void setView(NewShotBarView newShotBarView) {
    this.view = newShotBarView;
  }

  public void initialize(NewShotBarView newShotBarView, String idTargetUser) {
    this.idChannel = idTargetUser;
    this.setView(newShotBarView);
    this.updateDraftsButtonVisibility();
  }

  public void newMessageFromTextBox() {
    view.openNewShotView();
  }

  public void newMessageFromImage() {
    handleMenuPicker();
  }

  private void handleMenuPicker() {
    view.pickImage();
  }

  public void newMessageImagePicked(File image) {
    view.openNewShotViewWithImage(image);
  }

  public void openEditTopicCustomDialog() {
    view.openEditTopicDialog();
  }

  private void updateDraftsButtonVisibility() {
    getDraftsInteractor.loadDrafts(new GetDraftsInteractor.Callback() {
      @Override public void onLoaded(List<QueuedShot> drafts) {
        if (!drafts.isEmpty()) {
          view.showDraftsButton();
        } else {
          view.hideDraftsButton();
        }
      }
    });
  }

  @Override public void resume() {
    bus.register(this);
    if (hasBeenPaused) {
      updateDraftsButtonVisibility();
    }
  }

  @Override public void pause() {
    bus.unregister(this);
    hasBeenPaused = true;
  }

  @Subscribe @Override public void onShotFailed(ShotFailed.Event event) {
    updateDraftsButtonVisibility();
  }
}
