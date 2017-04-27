package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.ShotFailed;
import com.shootr.mobile.domain.bus.ShotQueued;
import com.shootr.mobile.domain.interactor.shot.DeleteDraftInteractor;
import com.shootr.mobile.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.mobile.domain.interactor.shot.SendDraftInteractor;
import com.shootr.mobile.domain.interactor.timeline.privateMessage.SendPrivateMessageDraftInteractor;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.ui.model.DraftModel;
import com.shootr.mobile.ui.model.PrivateMessageModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.DraftModelMapper;
import com.shootr.mobile.ui.views.DraftsView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class DraftsPresenter implements Presenter, ShotQueued.Receiver, ShotFailed.Receiver {

  private final GetDraftsInteractor getDraftsInteractor;
  private final SendDraftInteractor sendDraftInteractor;
  private final SendPrivateMessageDraftInteractor sendPrivateMessageDraftInteractor;
  private final DeleteDraftInteractor deleteDraftInteractor;
  private final DraftModelMapper draftModelMapper;
  private final Bus bus;

  private DraftsView draftsView;
  private List<DraftModel> drafts;

  @Inject public DraftsPresenter(GetDraftsInteractor getDraftsInteractor,
      SendDraftInteractor sendDraftInteractor,
      SendPrivateMessageDraftInteractor sendPrivateMessageDraftInteractor,
      DeleteDraftInteractor deleteDraftInteractor, DraftModelMapper draftModelMapper,
      @Main Bus bus) {
    this.getDraftsInteractor = getDraftsInteractor;
    this.sendDraftInteractor = sendDraftInteractor;
    this.sendPrivateMessageDraftInteractor = sendPrivateMessageDraftInteractor;
    this.deleteDraftInteractor = deleteDraftInteractor;
    this.draftModelMapper = draftModelMapper;
    this.bus = bus;
  }

  public void initialize(DraftsView draftsView) {
    this.draftsView = draftsView;
    this.loadDrafts();
  }

  private void loadDrafts() {
    getDraftsInteractor.loadDrafts(new GetDraftsInteractor.Callback() {
      @Override public void onLoaded(List<QueuedShot> drafts) {
        ArrayList<DraftModel> drafsModels = new ArrayList<>();
        for (QueuedShot draft : drafts) {
          if (draft.getBaseMessage() instanceof Shot) {
            drafsModels.add(draftModelMapper.transformShot(draft));
          } else {
            drafsModels.add(draftModelMapper.transformPrivateMessage(draft));
          }
        }
        onDraftListLoaded(drafsModels);
      }
    });
  }

  private void onDraftListLoaded(List<DraftModel> drafts) {
    this.drafts = drafts;
    if (drafts.isEmpty()) {
      draftsView.showEmpty();
    } else {
      draftsView.hideEmpty();
    }
    renderViewDraftList(drafts);
    showShootAllButtonIfMoreThanOneDraft(drafts);
  }

  private void renderViewDraftList(List<DraftModel> drafts) {
    draftsView.showDrafts(drafts);
  }

  private void showShootAllButtonIfMoreThanOneDraft(List<DraftModel> drafts) {
    draftsView.hideShootAllButton();
  }

  @Override public void resume() {
    bus.register(this);
  }

  @Override public void pause() {
    bus.unregister(this);
  }

  public void sendDraft(DraftModel draftModel) {
    if (draftModel.getBaseMessageModel() instanceof ShotModel) {
      sendDraftInteractor.sendDraft(draftModel.getIdQueue());
    } else {
      sendPrivateMessageDraftInteractor.sendDraft(draftModel.getIdQueue());
    }
  }

  public void deleteDraft(DraftModel draftModel) {
    deleteDraftInteractor.deleteDraft(draftModel.getIdQueue(),
        draftModel.getBaseMessageModel() instanceof ShotModel,
        new DeleteDraftInteractor.Callback() {
          @Override public void onDeleted() {
            loadDrafts();
          }
        });
  }

  public void shootAll() {
    sendDraftInteractor.sendDrafts(idShots(drafts));
    sendPrivateMessageDraftInteractor.sendDrafts(idPrivateMessages(drafts));
  }

  private List<Long> idShots(List<DraftModel> drafts) {
    List<Long> ids = new ArrayList<>();
    for (DraftModel draft : drafts) {
      if (draft.getBaseMessageModel() instanceof ShotModel) {
        ids.add(draft.getIdQueue());
      }
    }
    return ids;
  }

  private List<Long> idPrivateMessages(List<DraftModel> drafts) {
    List<Long> ids = new ArrayList<>();
    for (DraftModel draft : drafts) {
      if (draft.getBaseMessageModel() instanceof PrivateMessageModel) {
        ids.add(draft.getIdQueue());
      }
    }
    return ids;
  }

  @Subscribe @Override public void onShotQueued(ShotQueued.Event event) {
    this.loadDrafts();
  }

  @Subscribe @Override public void onShotFailed(ShotFailed.Event event) {
    this.loadDrafts();
  }
}
